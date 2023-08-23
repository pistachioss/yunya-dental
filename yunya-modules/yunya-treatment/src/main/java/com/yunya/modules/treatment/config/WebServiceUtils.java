package com.yunya.modules.treatment.config;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.types.Schema;
import org.apache.commons.collections.MapUtils;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * WebService - 工具类
 * Title: WebServiceUtils
 * Description:
 *
 * @author Micromaple
 * @version 1.0.0
 * @date 2022/7/1 12:14
 */
@Slf4j
public class WebServiceUtils {
	// 接口调用地址
	private static String url = "http://127.0.0.1:9090/user?wsdl";
	// 命名空间
	private static String namespace = "http://ws.biz.treatment.modules.yunya.com";
	
    /**
     * WebService - 调用接口
     *
     * @param methodName 函数名
     * @param params     参数
     * @param clazz      返回对象class
     * @return 返回结果(Object)
     */
    public static <T> T call(String methodName, Map<String, String> params, Class<T> clazz) {
        // log.info("调用 WebService 发送参数==>" + MapperUtils.mapToJson(params));
        String soapActionURI = namespace + methodName;
        try {
            Service service = new Service();

            SOAPHeaderElement header = new SOAPHeaderElement(namespace, methodName);
            header.setNamespaceURI(namespace);

            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(url);

            call.setOperationName(new QName(namespace, methodName));

            // 添加参数
            List<String> parameterList = Lists.newArrayList();
            if (params != null) {
                Set<String> paramsKey = params.keySet();
                for (String key : paramsKey) {
                    call.addParameter(new QName(namespace, key), XMLType.XSD_STRING, ParameterMode.IN);
                    String pValue = MapUtils.getString(params, key);
                    header.addChildElement(key).setValue(pValue);
                    parameterList.add(pValue);
                }
            }
            call.setUseSOAPAction(true);
            call.setSOAPActionURI(soapActionURI);
            call.addHeader(header);

            // 进行序列化  实体类也要序列化 implements Serializable
            call.registerTypeMapping(clazz, new QName(namespace, soapActionURI),
                    new BeanSerializerFactory(clazz, new QName(namespace, soapActionURI)),
                    new BeanDeserializerFactory(clazz, new QName(namespace, soapActionURI)));
            // 设置输出的类
            call.setReturnClass(clazz);
            // 接口返回结果
            T result = (T) call.invoke(parameterList.toArray());
            log.info("调用 WebService 接口返回===>" + result);
            return result;
        } catch (Exception e) {
            log.error("调用 WebService 接口错误信息==>" + e.getMessage());
        }
        return null;
    }

    /**
     * WebService - 接口调用
     *
     * @param methodName 函数名
     * @param params     参数
     * @return 返回结果(String)
     */
    public static String call(String methodName, Map<String, String> params) {
        // log.info("调用 WebService 发送参数==>" + MapperUtils.mapToJson(params));
        String soapActionURI = namespace + methodName;
        try {
            Service service = new Service();

            SOAPHeaderElement header = new SOAPHeaderElement(namespace, methodName);
            header.setNamespaceURI(namespace);

            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(url);

            call.setOperationName(new QName(namespace, methodName));

            // 添加参数
            List<String> parameterList = Lists.newArrayList();
            if (params != null) {
                Set<String> paramsKey = params.keySet();
                for (String key : paramsKey) {
                    call.addParameter(new QName(namespace, key), XMLType.XSD_STRING, ParameterMode.IN);
                    String pValue = MapUtils.getString(params, key);
                    header.addChildElement(key).setValue(pValue);
                    parameterList.add(pValue);
                }
            }
            call.setUseSOAPAction(true);
            call.setSOAPActionURI(soapActionURI);
            call.addHeader(header);
            // 设置返回类型
            call.setReturnType(new QName(namespace, methodName), String.class);
            // 接口返回结果
            String result = (String) call.invoke(parameterList.toArray());
            log.info("调用 WebService 接口返回===>" + result);
            return result;
        } catch (Exception e) {
            log.error("调用 WebService 接口错误信息==>" + e.getMessage());
        }
        return null;
    }

    /**
     * WebService - 调用接口
     *
     * @param methodName 函数名
     * @param params     参数
     * @return 返回结果(String)
     */
    public static String call2(String methodName, Map<String, String> params) {
        // log.info("调用 WebService 发送参数==>" + MapperUtils.mapToJson(params));
        String soapActionURI = namespace + methodName;
        try {
            Service service = new Service();

            SOAPHeaderElement header = new SOAPHeaderElement(namespace, methodName);
            header.setNamespaceURI(namespace);

            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(url);

            call.setOperationName(new QName(namespace, methodName));

            // 添加参数
            List<String> parameterList = Lists.newArrayList();
            if (params != null) {
                Set<String> paramsKey = params.keySet();
                for (String key : paramsKey) {
                    call.addParameter(new QName(namespace, key), XMLType.XSD_STRING, ParameterMode.IN);
                    String pValue = MapUtils.getString(params, key);
                    header.addChildElement(key).setValue(pValue);
                    parameterList.add(pValue);
                }
            }
            call.setUseSOAPAction(true);
            call.setSOAPActionURI(soapActionURI);
            call.addHeader(header);
            // 设置返回类型
            call.setReturnType(XMLType.XSD_SCHEMA);
            // 接口返回结果
            Schema schemaResult = (Schema)call.invoke(parameterList.toArray());
            String result = "";
            for(int i = 0; i<schemaResult.get_any().length; i++){
                result = result + schemaResult.get_any()[i];
            }
            log.error("调用 WebService 接口返回===>" + result);
            return result;
        } catch (Exception e) {
            log.error("调用 WebService 接口错误信息==>" + e.getMessage());
        }
        return null;
    }


    public static void main(String[] args) {
        String methodName = "findPatientInfoById";
        Map<String, String> params = new HashMap<>();
        params.put("id", "12345");
        System.out.println(call(methodName, params));
    }
}
