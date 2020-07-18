package com.yunya.clinic.base.controller;


import com.yunya.clinic.base.model.request.MedicalClinicInfoReq;
import com.yunya.clinic.base.service.ClinicExtInfoBiz;
import com.yunya.clinic.base.model.response.MedicalClinicExtInfoRes;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author bruce
 */
@Api(tags = {"门诊信息api"})
@RestController
public class ClinicExtController {

    @Autowired
    private ClinicExtInfoBiz clinicExtInfoBiz;

    /**
     * 根据组织ID获取医疗机构完整信息
     *
     * @param companyId 组织ID
     * @return map
     */
    @ApiOperation("根据门诊ID查询门诊基础信息")
    @GetMapping("clinic/base/medical/{companyId}")
    public ResponseResult findMedicalOrganizationInfo(@PathVariable Integer companyId) {
        MedicalClinicExtInfoRes resultVO = clinicExtInfoBiz.findMedicalOrganizationInfo(companyId);
        return ResponseUtil.success(resultVO);
    }

    /**
     * 编辑医疗机构信息
     *
     * @param companyId 组织ID
     * @param form 参数封装
     * @return map
     */
    @ApiOperation("编辑医疗机构信息")
    @PutMapping("clinic/base/medical/edit/{companyId}")
    public ResponseResult edit(
            @PathVariable Integer companyId, @RequestBody @Validated MedicalClinicInfoReq form) {
        clinicExtInfoBiz.edit(companyId, form);
        return ResponseUtil.success();
    }

}
