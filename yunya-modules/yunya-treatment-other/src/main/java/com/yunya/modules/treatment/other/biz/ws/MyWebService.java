package com.yunya.modules.treatment.other.biz.ws;

import javax.jws.WebService;

@WebService(
        name = "MyWebService", // 暴露服务名称
        targetNamespace = "http://ws.biz.treatment.modules.yunya.com"// 命名空间,一般是接口的包名倒序
)
public interface MyWebService {

    String findPatientInfoById(/*@WebParam(name = "id", targetNamespace = "http://ws.biz.treatment.modules.yunya.com")*/ String query);
}