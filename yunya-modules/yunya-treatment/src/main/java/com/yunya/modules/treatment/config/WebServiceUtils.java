package com.yunya.modules.treatment.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.report.domain.query.BillOfReceivableQuery;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.service.model.*;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yunya.framework.common.constant.OperationCodeConstants.DATA_ERROR;

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
	private static String url = "http://127.0.0.1:8595/user?wsdl";
	// 命名空间
	private static String namespace = "http://ws.biz.treatment.modules.yunya.com";

    private static String methodName = "findPatientInfoById";
    private static String webserviceName = "MyWebService";

    private static Map<String, Client> clientMap = new HashMap<>();

    /**
     * @param wsdlUrl  wsdl的地址：http://localhost:8001/demo/HelloServiceDemoUrl?wsdl
     * @param methodName
     */
    public static Client initClient(String wsdlUrl, String methodName) {
        wsdlUrl = StringHelper.defaultString(wsdlUrl, url);
        // 创建动态客户端
        JaxWsDynamicClientFactory factory = JaxWsDynamicClientFactory.newInstance();
        // 创建客户端连接
        Client client = factory.createClient(wsdlUrl);
        clientMap.put(methodName, client);
        log.info("webservice动态客户端初始化完成，已链接到webservice服务：{}", wsdlUrl);
        return client;
    }

    /**
     * 通过json对象格式入参方式调用webservice暴露的服务和方法
     *
     * @param methodName 调用的方法名称 selectOrderInfo
     * @param targetNamespace 目标命名空间 http://service.limp.com/
     * @param webServiceName  暴露webservice的服务名称
     * @param params 参数集合
     * @throws Exception
     */
    public  static String callByJson(String wsdlUrl, String methodName, String targetNamespace,
                                       String webServiceName, Object params) throws Exception{
        String param = JSONObject.toJSONString(params);
        return callWebService(wsdlUrl, methodName, targetNamespace, webServiceName, param);
    }

    /**
     * 通过json数组格式入参方式调用webservice暴露的服务和方法
     *
     * @param methodName 调用的方法名称 selectOrderInfo
     * @param targetNamespace 目标命名空间 http://service.limp.com/
     * @param webServiceName  暴露webservice的服务名称
     * @param params 参数集合
     * @throws Exception
     */
    public  static String callByJArray(String wsdlUrl, String methodName, String targetNamespace,
                                       String webServiceName, Object...params) throws Exception{
        String param = JSONArray.toJSONString(params);
        return callWebService(wsdlUrl, methodName, targetNamespace, webServiceName, param);
    }

    /**
     * 通过json格式入参和出参方式调用webservice暴露的服务和方法
     *
     * @param methodName 调用的方法名称 selectOrderInfo
     * @param targetNamespace 目标命名空间 http://service.limp.com/
     * @param webServiceName  暴露webservice的服务名称
     * @param params 参数集合
     * @throws Exception
     */
    public  static String callWebService(String wsdlUrl, String methodName, String targetNamespace,
                                     String webServiceName, Object... params) throws Exception{
        //从缓存中换取 endpoint、client
        Client client = clientMap.computeIfAbsent(methodName, name->initClient(wsdlUrl, name));
        Endpoint endpoint = client.getEndpoint();
        // Make use of CXF service model to introspect the existing WSDL
        ServiceInfo serviceInfo = endpoint.getService().getServiceInfos().get(0);
        // 创建QName来指定NameSpace和要调用的service
        String localPart = webServiceName + "SoapBinding";
        QName bindingName = new QName(targetNamespace, localPart);
        BindingInfo binding = serviceInfo.getBinding(bindingName);

        //创建QName来指定NameSpace和要调用的方法绑定方法
        QName opName = new QName(targetNamespace, methodName);//selectOrderInfo

        BindingOperationInfo boi = binding.getOperation(opName);
//		BindingMessageInfo inputMessageInfo = boi.getInput();
        BindingMessageInfo inputMessageInfo = null;
        if (!boi.isUnwrapped()) {
            //OrderProcess uses document literal wrapped style.
            inputMessageInfo = boi.getWrappedOperation().getInput();
        } else {
            inputMessageInfo = boi.getUnwrappedOperation().getInput();
        }

        List<MessagePartInfo> parts = inputMessageInfo.getMessageParts();

        /***********************以下是初始化参数，组装参数；处理返回结果的过程******************************************/
        Object[] parameters = new Object[parts.size()];
        for(int m=0; m<parts.size(); m++){
            MessagePartInfo part = parts.get(m);
            // 取得webservice服务方法入参Class对象
            Class<?> partClass = part.getTypeClass();//OrderInfo.class;
            System.out.println("入参类型：" + partClass.getCanonicalName()); // GetAgentDetails
            //实例化对象
            Object initDomain=null;
            //普通参数的形参，不需要fastJson转换直接赋值即可
            String param = params[m].toString();
            if ("java.lang.String".equalsIgnoreCase(partClass.getCanonicalName())
                    ||"int".equalsIgnoreCase(partClass.getCanonicalName())) {
                initDomain = param;
            } else if (partClass.getCanonicalName().indexOf("[]")>-1){
                //转换数组
                initDomain = JSON.parseArray(param, partClass.getComponentType());
            } else {
                initDomain = JSON.parseObject(param, partClass);
            }
            parameters[m] = initDomain;
        }

        //定义返回结果集
        Object[] result = null;
        //普通参数情况 || 对象参数情况  1个参数 ||ArryList集合
        try {
            result = client.invoke(opName, parameters);
        } catch (Exception e) {
            log.error("invoke webservice:{}, method:{}, error:{}", url, methodName, e);
            throw new ClientServiceException("invoke webservice error", DATA_ERROR);
        }
        //返回调用结果
        log.info("invoke service: {}, method:{}, result:{}", url, methodName, result);
        if(result.length>0){
            return result[0].toString();
        }
        return  "invoke success, but is void ";
    }

    public static void main(String[] args) throws Exception {
        String str = "{\"keyword\":\"\",\"queryDate\":\"2023-08-01\",\"orgIds\":[63],\"pageNum\":1,\"pageSize\":10,\"regDentistIds\":[],\"whetherPage\":true}";
        BillOfReceivableQuery query = JSONObject.parseObject(str, BillOfReceivableQuery.class);
        String result = callByJson(url, methodName, namespace, webserviceName, query);
        System.out.println("webservice结果：");
        System.out.println(result);
    }
}
