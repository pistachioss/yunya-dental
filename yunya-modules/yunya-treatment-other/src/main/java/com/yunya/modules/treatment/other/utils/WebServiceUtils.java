package com.yunya.modules.treatment.other.utils;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPHeaderElement;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.encoding.XMLType;
import java.net.URL;
import java.util.Iterator;

@Slf4j
public class WebServiceUtils {
    private static String url = "http://60.191.117.126:8088/csp/hsb/DHC.Published.PUB0013.BS.PUB0013.CLS";
    // 命名空间
    private static String namespace = "http://www.dhcc.com.cn";

    private static String methodName = "HIPMessageServer";

    private static String soapAction = "http://www.dhcc.com.cn/DHC.Published.PUB0013.BS.PUB0013.HIPMessageServer";

    private static Integer timeout = 10 * 60 * 1000;

    public static void main1(String[] args) {
        try {
                 String endpoint = url;
                 //直接引用远程的wsdl文件
                 //以下都是套路
                 Service service = new Service();
                 Call call = (Call) service.createCall();
                 call.setTargetEndpointAddress(endpoint);
                 call.setOperationName("HIPMessageServer");//WSDL里面描述的接口名称
                 call.addParameter("input1", org.apache.axis.encoding.XMLType.XSD_DATE,
                               javax.xml.rpc.ParameterMode.IN);//接口的参数
                 call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);//设置返回类型
                 String temp = "测试人员";
                 String result = (String)call.invoke(new Object[]{temp});
    //给方法传递参数，并且调用方法
                 System.out.println("result is "+result);
        } catch (Exception e) {
             System.err.println(e.toString());
        }
   }

    public static void main(String[] args) {
        callWebService(methodName,
                WebServiceParam.builder().inName("input1").data("1").build(),
                WebServiceParam.builder().inName("input2").data("2").build());
    }

    public static String callByJson(String methodName, String inName, Object json){
        String data = JSONArray.toJSONString(json);
        return callWebService(methodName,
                WebServiceParam.builder().inName(inName).data(data).build());
    }

    public static String callWebService(String methodName, WebServiceParam...params){
        String result = null;
        try {
            // 服务端的url，需要根据情况更改。
            String endpointURL = url;
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTimeout(timeout);
            call.setTargetEndpointAddress(new URL(endpointURL));
            call.setSOAPActionURI(soapAction);
            call.setOperationName(new QName(namespace, methodName));// 设置操作的名称。
//            appendAuthericate2Header(call, userId, password);
            call.setReturnType(XMLType.XSD_STRING);// 返回的数据类型
            Object[] paramDatas = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                WebServiceParam param = params[i];
                call.addParameter(new QName(namespace, param.getInName()), param.getInType(), ParameterMode.IN);// 参数的类型
                paramDatas[i] = param.getData();
            }
            result = (String) call.invoke(paramDatas);// 执行调用
            log.info("WebService: {}, method: {}, 调用结果：{}", url, methodName, result);
            // 结果信息解析
            Document document = DocumentHelper.parseText(result);
            Element rootElement = document.getRootElement();
            Iterator iter = rootElement.elementIterator("State");
            while(iter.hasNext()){
                Element recordEle = (Element) iter.next();
                String code = recordEle.getTextTrim();// State值
                if("0".equals(code)){ //成功
                    log.error("调用接口成功");
                }else{ // 失败保存log
                    log.error(result);
                }
            }
        } catch (Exception e) {
            log.error("调用接口失败",e);
        }
        return result;
    }

    /**
     * 认证参数
     *
     * @param call
     * @param userId
     * @param password
     * @throws Exception
     */
    private static void appendAuthericate2Header(Call call, String userId, String password) throws Exception {
        // 由于需要认证，故需要设置调用的用户名和密码。
        SOAPHeaderElement soapHeaderElement = new SOAPHeaderElement(namespace, "UserSoapHeader");
        soapHeaderElement.setNamespaceURI("http://tempuri.org/");
        soapHeaderElement.addChildElement("UserId").setValue(userId);
        soapHeaderElement.addChildElement("PassWord").setValue(password);
        call.addHeader(soapHeaderElement);
    }
}  