package com.yunya.modules.treatment.other.controller;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.report.domain.query.BillOfReceivableQuery;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.other.utils.WebServiceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: chenlin
 * @date: 2023/9/6 12:46
 * @description:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("/qc")
public class QcDoctorAdviceController {

    private static String url = "http://60.191.117.126:8088/csp/hsb/DHC.Published.PUB0013.BS.PUB0013.CLS?WSDL=1";
    // 命名空间
    private static String namespace = "http://www.dhcc.com.cn";

    private static String methodName = "HIPMessageServer";
    private static String webserviceName = "PUB0013";


    @GetMapping("/test")
    public ResponseResult test() {
        String str = "{\"keyword\":\"\",\"queryDate\":\"2023-08-01\",\"orgIds\":[63],\"pageNum\":1,\"pageSize\":10,\"regDentistIds\":[],\"whetherPage\":true}";
        BillOfReceivableQuery query = JSONObject.parseObject(str, BillOfReceivableQuery.class);
        String str2 = "{\"id\":2}";
//        String result = callByJson(url, methodName, namespace, webserviceName, query);
//        String result = callByJArray(url, methodName, namespace, webserviceName, query, str2);
        String result = WebServiceUtils.callWebService(url, methodName, namespace, webserviceName, query, str2);
        System.out.println("webservice结果：");
        System.out.println(result);
        return ResponseUtil.success(result);
    }
}
