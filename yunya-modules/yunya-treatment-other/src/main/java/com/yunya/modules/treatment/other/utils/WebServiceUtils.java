package com.yunya.modules.treatment.other.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.treatment_other.domain.common.QcPatientInfo;
import com.yunya.feign.treatment_other.domain.common.QcTreatmentInfo;
import com.yunya.feign.treatment_other.domain.form.QcAdviceItemStatusForm;
import com.yunya.feign.treatment_other.domain.form.QcAdviceStatusForm;
import com.yunya.feign.treatment_other.domain.form.QcAdviceUploadForm;
import com.yunya.feign.treatment_other.domain.form.QcAdviceUploadItemForm;
import com.yunya.feign.treatment_other.domain.query.QcDoctorAdviceQuery;
import com.yunya.feign.treatment_other.domain.vo.QcAdviceStatusVO;
import com.yunya.feign.treatment_other.domain.vo.QcDoctorAdviceRecordVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPHeaderElement;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.encoding.XMLType;
import java.net.URL;

@Slf4j
public class WebServiceUtils {
    // wsdl地址
    private static String url = "http://60.191.117.126:8088/csp/hsb/DHC.Published.PUB0013.BS.PUB0013.CLS";
    // 命名空间
    private static String namespace = "http://www.dhcc.com.cn";
    // 方法
    private static String methodName = "HIPMessageServer";

    private static String soapAction = "http://www.dhcc.com.cn/DHC.Published.PUB0013.BS.PUB0013.HIPMessageServer";
    // 超时时长
    private static Integer timeout = 10 * 60 * 1000;

    public static void main1(String[] args) {
        try {
                 String endpoint = url;
                 //直接引用远程的wsdl文件
                 //以下都是套路
                 Service service = new Service();
                 Call call = (Call) service.createCall();
                 call.setTargetEndpointAddress(endpoint);
                 call.setOperationName(methodName);//WSDL里面描述的接口名称
                 call.addParameter("input1", org.apache.axis.encoding.XMLType.XSD_STRING,
                               javax.xml.rpc.ParameterMode.IN);//接口的参数
                 call.addParameter("input2", org.apache.axis.encoding.XMLType.XSD_STRING,
                         javax.xml.rpc.ParameterMode.IN);
                 call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);//设置返回类型
                 Object[] datas = {"MES0085", "{\"end_date\":\"2023-09-19\",\"start_date\":\"2023-09-19\", \"patient_no\":\"\", \"werif_code\":\"\",\"cred_no\":\"330106198311080076\"}"};
            Object invoke = call.invoke(datas);
            String result = (String)invoke;
    //给方法传递参数，并且调用方法
                 System.out.println("调用结果："+result);
        } catch (Exception e) {
             System.err.println(e.toString());
        }
   }

    public static void main(String[] args) {
//        String input1 = "MES0083"; //医嘱上传
//        String input1 = "MES0084"; //医嘱撤销上传
        String input1 = "MES0085"; //医嘱查询
//        String input1 = "MES0086"; //医嘱下载执行确认
//        String input1 = "MES0087"; //医嘱报告回写url
//        String input1 = "MES0090"; //医嘱状态变更
//        String input1 = "MES0091"; //状态变更通知

        QcDoctorAdviceQuery query = new QcDoctorAdviceQuery();
        query.setStart_date("2023-09-01");
        query.setEnd_date("2023-10-01");
//        query.setCred_no("330106198311080076");
        String data = JSONObject.toJSONString(query);
        System.out.println("参数：" + data);
        WebServiceParam param1 = new WebServiceParam();
        param1.setInName("input1");
        param1.setData(input1);
        WebServiceParam param2 = new WebServiceParam();
        param2.setInName("input2");
        param2.setData(data);
        String result = callWebService(url, namespace, methodName, param1, param2);
        QcDoctorAdviceRecordVO vo = JSONObject.parseObject(result, QcDoctorAdviceRecordVO.class);
    }

    /**
     * 调用webservice接口
     *
     * @param methodName
     * @param params
     * @return json字符串
     */
    public static String callWebService(String wsdlUrl, String namespace, String methodName, WebServiceParam...params){
        return callWebService(wsdlUrl, namespace, methodName, methodName, params);
    }

    public static String callWebService(String wsdlUrl, String namespace, String methodName, String soapAction, WebServiceParam...params){
        String result = null;
        try {
            // 服务端的url，需要根据情况更改。
            String endpointURL = wsdlUrl;
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
            log.info("WebService: {}, method: {}, param: {}", url, methodName, paramDatas);
            log.info("result：{}", result);
        } catch (Exception e) {
            log.error("webservice: {}, method: {}, error: {}", url, methodName, e);
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