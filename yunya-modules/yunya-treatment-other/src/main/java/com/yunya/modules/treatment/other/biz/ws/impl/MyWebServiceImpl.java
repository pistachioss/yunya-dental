package com.yunya.modules.treatment.other.biz.ws.impl;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.report.domain.query.BillOfReceivableQuery;
import com.yunya.modules.treatment.other.biz.ws.MyWebService;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service
@WebService(serviceName = "MyWebService", // 与接口中指定的name一致, 都可以不写
        targetNamespace = "http://ws.biz.treatment.modules.yunya.com", // 与接口中的命名空间一致,一般是接口的包名倒，都可以不用写
        endpointInterface = "com.yunya.modules.treatment.biz.ws.MyWebService" // 接口类全路径
)
public class MyWebServiceImpl implements MyWebService {

    @Override
    public String findPatientInfoById(String queryStr) {
        System.out.println(">>>>>>>>获取到请求参数：" + queryStr);
        BillOfReceivableQuery query = JSONObject.parseObject(queryStr, BillOfReceivableQuery.class);
        return JSONObject.toJSONString(query);
    }
}