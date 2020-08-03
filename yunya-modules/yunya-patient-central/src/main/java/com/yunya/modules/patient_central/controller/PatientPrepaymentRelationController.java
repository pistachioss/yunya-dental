package com.yunya.modules.patient_central.controller;

import com.yunya.feign.patient_central.domain.vo.PatientPrepaymentsInfoVo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.PatientPrepaymentRelationBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简单介绍:</br> 患者预付款 控制层
 *
 * @author: WY
 * @date 2020/7/31 9:29
 * @description:
 * @since: 1.0.0
 */
@Api(value = "患者预付款",description = "患者预付款（增删查改）")
@RestController
@RequestMapping("prepayment")
public class PatientPrepaymentRelationController {

    private PatientPrepaymentRelationBiz patientPrepaymentBiz;

    public PatientPrepaymentRelationController(PatientPrepaymentRelationBiz patientPrepaymentBiz) {
        this.patientPrepaymentBiz = patientPrepaymentBiz;
    }

    @ApiOperation("患者预付款基本信息查询")
    @GetMapping("/findPrepaymentInfo/{id}")
    public ResponseResult findPrepaymentInfo(@PathVariable("id") Integer id){
        return ResponseUtil.success(patientPrepaymentBiz.findPrepaymentInfo(id));
    }


}
