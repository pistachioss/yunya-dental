package com.yunya.modules.patient_central.controller.web;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.query.CustomerPatientQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.PatientSimpleInfoVO;
import com.yunya.feign.patient_central.domain.vo.web.PatientSimpleRefererVO;
import com.yunya.feign.patient_central.domain.vo.PatientTrajectoryVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.CustomerPatientBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author: chenlin
 * @date: 2022/11/7 10:25
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "客户-患者")
@RestController
@RequestMapping("/customer")
public class CustomerPatientController {

    @Autowired
    private CustomerPatientBiz customerPatientBiz;

    /**
     * 根据患者id查询患者基础资料
     *
     * @param patientId
     * @return
     */
    @ApiOperation("根据患者id查询患者基础资料")
    @GetMapping("/patientBase/{patientId}")
    public ResponseResult<PatientSimpleInfoVO> findPatientSimpleInfo(@PathVariable(value = "patientId") Integer patientId) {
        PatientSimpleInfoVO info = customerPatientBiz.findPatientSimpleInfo(patientId);
        return ResponseUtil.success(info);
    }

    /**
     * 根据患者id查询患者推荐人列表
     *
     */
    @ApiOperation("根据患者id查询患者推荐人列表")
    @PostMapping("/patient/referer")
    public ResponseResult<PageInfo<PatientSimpleRefererVO>> findPatientReferrerList(@RequestBody @Validated CustomerPatientQueryForm query) {
        PageInfo<PatientSimpleRefererVO> page = customerPatientBiz.findPatientReferrerList(query);
        return ResponseUtil.success(page);
    }

    /**
     * 根据患者id查询患者动态列表
     *
     */
    @ApiOperation("根据患者id查询患者动态列表")
    @PostMapping("/patient/trajectory")
    public ResponseResult<PageInfo<PatientTrajectoryVO>> findPatientTrajectoryList(@RequestBody @Validated CustomerPatientQueryForm query) {
        PageInfo<PatientTrajectoryVO> page = customerPatientBiz.findPatientTrajectoryList(query);
        return ResponseUtil.success(page);
    }
}
