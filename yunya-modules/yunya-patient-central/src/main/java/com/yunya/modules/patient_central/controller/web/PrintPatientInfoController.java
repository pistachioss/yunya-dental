package com.yunya.modules.patient_central.controller.web;

import com.yunya.feign.patient_central.domain.query.PrintInfoQuery;
import com.yunya.feign.patient_central.domain.vo.web.PrintInfoVo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.PrintPatientInfoBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: 患者相关信息打印接口
 * @author: LHB
 * @create: 2020-11-17 10:49
 **/
@Api(value = "PrintPatientInfoController",description = "患者相关信息打印接口")
@RestController
public class PrintPatientInfoController {
    /** 打印业务 */
    @Autowired
    private PrintPatientInfoBiz printPatientInfoBiz;
    /**
     * 打印信息
     * @param query 就诊ID(就诊记录ID)
     * @return 返回打印信息
     */
    @ApiOperation(value = "打印信息")
    @PostMapping("/print/{patientId}")
    public ResponseResult<PrintInfoVo> printInfo(
            @PathVariable("patientId") Integer patientId,
            @RequestBody @Validated PrintInfoQuery query) {
        PrintInfoVo printInfoVo = printPatientInfoBiz.printInfo(patientId,query);
        return ResponseUtil.success(printInfoVo);
    }
}
