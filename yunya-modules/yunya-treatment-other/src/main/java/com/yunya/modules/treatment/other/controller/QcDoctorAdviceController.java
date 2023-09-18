package com.yunya.modules.treatment.other.controller;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.other.utils.WebServiceParam;
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
    // wsdl
    private static String url = "http://60.191.117.126:8088/csp/hsb/DHC.Published.PUB0013.BS.PUB0013.CLS?WSDL=1";
    // 命名空间
    private static String namespace = "http://www.dhcc.com.cn";
    // 方法名
    private static String methodName = "HIPMessageServer";
    // 暴露服务名
    private static String webserviceName = "PUB0013";


    @GetMapping("/test")
    public ResponseResult test() {
        String result = WebServiceUtils.callWebService(methodName,
                WebServiceParam.builder().inName("input1").data("1").build(),
                WebServiceParam.builder().inName("input2").data("2").build());
        System.out.println("webservice结果：");
        System.out.println(result);
        return ResponseUtil.success(result);
    }
}
