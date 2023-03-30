package com.yunya.framework.common.utils;

import com.yunya.framework.common.exception.ClientServiceException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPOutputStream;

import static com.yunya.framework.common.enums.ExceptionCode.*;

public class QztXmlToMap {

    public static Map<String, Object> pcXmlToMap(String fileUrl, Date date, int size, String code) {
        return xmlToMap(fileUrl, date, size, code, null, 0, null);
    }

    public static Map<String, Object> dcXmlToMap(String fileUrl, Date date, String code, String totalTaskId, int sn) {
        return xmlToMap(fileUrl, date, 0, code, totalTaskId, sn, null);
    }

    public static Map<String, Object> sjXmlToMap(String fileUrl, String totalTaskId, String code
            , String businessData) {
        return xmlToMap(fileUrl, null, 0, code, totalTaskId, 0, businessData);
    }

    public static Map<String, Object> xmlToMap(String fileUrl) {
        return xmlToMap(fileUrl, null, 0, null, null, 0, null);
    }

    private static Map<String, Object> xmlToMap(String fileUrl, Date date, int size, String code
            , String totalTaskId, int sn, String businessData) {
        Map<String, Object> xmlMap = new HashMap<>();
        try {
            InputStream input = QztXmlToMap.class.getResourceAsStream(fileUrl);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            Document doc = factory.newDocumentBuilder().parse(input);
            Element root = doc.getDocumentElement();
            xmlMap.put(root.getNodeName(), elementToMap(root, date, size, code, totalTaskId, sn, businessData));
        } catch (Exception e) {
            throw ClientServiceException.wrap(XML_PARSE_ERROR, e);
        }
        return xmlMap;
    }

    public static String stringGZIP(String xmlStr) {
        if (StringUtils.isBlank(xmlStr)) {
            return "";
        }
        // xml报文压缩
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(xmlStr.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw ClientServiceException.wrap(XML_ZIP_ERROR, e);
        }
        // 压缩后转base64字符串
        return Base64.encodeBase64String(out.toByteArray());
    }

    public static String mapToXml(Map<String, Object> map, boolean include) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            appendMap(document, null, map);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            if (!include) {
                transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
            }
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            return writer.getBuffer().toString().replaceAll("\n|\r", "");
        } catch (Exception e) {
            throw ClientServiceException.wrap(XML_PARSE_ERROR, e);
        }
    }

    public static Node getNode(String xml, String path) {
        try {
            // 创建DOM解析器
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
            NodeList nodeList = document.getElementsByTagName(path);
            // 获取指定节点
            return nodeList.item(0);
        } catch (Exception e) {
            throw ClientServiceException.wrap(XML_NODE_GET, e);
        }
    }

    private static void appendMap(Document document, Element parent, Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Element element = document.createElement(key);
            if (Objects.isNull(parent)) {
                document.appendChild(element);
            }
            if (value instanceof Map) {
                appendMap(document, element, (Map<String, Object>) value);
            } else if (value instanceof List) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) value;
                for (Map<String, Object> objectMap : list) {
                    String key1 = objectMap.keySet().toArray()[0].toString();
                    Map<String, Object> entity = (Map<String, Object>) objectMap.get(key1);
                    Element element1 = document.createElement(key1);
                    if (!Objects.isNull(parent)) {
                        parent.appendChild(element1);
                    }
                    appendMap(document, element1, entity);
                }
                parent = null;
            } else {
                element.appendChild(document.createTextNode(Objects.isNull(value) ? "" : value.toString()));
            }
            if (!Objects.isNull(parent)) {
                parent.appendChild(element);
            }
        }
    }

    private static Map<String, Object> elementToMap(Element element, Date date, int size, String code
            , String totalTaskId, int sn, String businessData) {
        Map<String, Object> result = new LinkedHashMap<>();
        NamedNodeMap attributes = element.getAttributes();
        if (attributes.getLength() > 0) {
            Map<String, String> attributeMap = new LinkedHashMap<>();
            for (int i = 0; i < attributes.getLength(); i++) {
                Attr attribute = (Attr) attributes.item(i);
                attributeMap.put(attribute.getName(), attribute.getValue());
            }
            result.put("attributes", attributeMap);
        }
        NodeList children = element.getChildNodes();
        Map<String, Object> childMap = new LinkedHashMap<>();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE) {
                continue;
            }
            Map<String, Object> map = elementToMap((Element) child, date, size, code, totalTaskId, sn, businessData);
            Object value = map.get("#value");
            childMap.put(child.getNodeName(), Objects.isNull(value) ? map : value);
            changeValue(childMap, date, size, code, totalTaskId, sn, businessData);
        }
        if (!childMap.isEmpty()) {
            result.putAll(childMap);
        } else if (!element.getTextContent().isEmpty()) {
            result.put("#value", element.getTextContent());
        }
        return result;
    }

    private static void changeValue(Map<String, Object> map, Date date, int size, String code
            , String totalTaskId, int sn, String businessData) {
        if (Objects.nonNull(map.get("begindatetime"))) {
            map.put("begindatetime", DateUtil.format(date, "yyyyMMdd") + "000000");
        }
        if (Objects.nonNull(map.get("enddatetime"))) {
            map.put("enddatetime", DateUtil.format(date, "yyyyMMdd") + "235959");
        }
        if (Objects.nonNull(map.get("daqtaskid")) && Objects.isNull(businessData)) {
            map.put("daqtaskid", DateUtil.format(date, "yyyyMMddHHmmss"));
        }
        if (Objects.nonNull(map.get("daqtaskid")) && Objects.nonNull(businessData)) {
            map.put("daqtaskid", totalTaskId);
        }
        if (Objects.nonNull(map.get("tasknum"))) {
            int taskNum = (int) Math.ceil((double) size / 1000);
            map.put("tasknum", taskNum);
        }
        if (size != 0 && Objects.nonNull(map.get("datanum"))) {
            map.put("datanum", size);
        }
        if (Objects.nonNull(map.get("colrescode"))) {
            map.put("colrescode", "REQ." + code);
        }
        if (Objects.nonNull(map.get("setcode"))) {
            map.put("setcode", code);
        }
        if (Objects.nonNull(map.get("totaltaskid"))) {
            map.put("totaltaskid", totalTaskId);
        }
        if (Objects.nonNull(map.get("sn"))) {
            map.put("sn", sn);
        }
        if (Objects.nonNull(map.get("standardcode")) && sn == 0) {
            map.put("standardcode", "REQ." + code);
        }
        if (Objects.nonNull(map.get("businessdata")) && Objects.equals("SJSCJ", map.get("businessdata"))) {
            map.put("businessdata", businessData);
        }
    }

}
