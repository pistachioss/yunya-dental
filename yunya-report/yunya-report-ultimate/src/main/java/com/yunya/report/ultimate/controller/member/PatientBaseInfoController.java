package com.yunya.report.ultimate.controller.member;

import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.feign.report.domain.vo.PatientDataVo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.report.BasePatient;
import com.yunya.report.ultimate.service.PatientBaseInfoBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简介:
 *
 * @author: WY
 * @date: 2020/11/24 13:29
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "公司端-患者资料-患者预约信息")
@RestController
@RequestMapping("patient")
public class PatientBaseInfoController {

    /** 注入服务 */
    @Autowired
    PatientBaseInfoBiz patientBaseInfoBiz;


    /**
     * 查询患者资料信息
     * @param id 患者id
     * @return BasePatient
     */
    @ApiOperation("查询患者资料预约信息")
    @GetMapping("/patientInfo/{id}")
    public ResponseResult<PatientDataVo> patientInfo(@PathVariable("id") Integer id) {
        PatientDataVo patientDataVo = patientBaseInfoBiz.PatientDataVo(id);
        return ResponseUtil.success(patientDataVo);
    }



}