package com.yunya.modules.system.controller;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.system.biz.ClinicExtInfoBiz;
import com.yunya.modules.system.form.MedicalOrganizationInfoForm;
import com.yunya.modules.system.vo.MedicalOrganizationInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简单介绍:</br> 医疗机构扩展信息控制层
 *
 * @author: chow
 * @date: 2020/6/6 11:01
 * @description:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("organization")
@Api(value = "门诊基础信息管理", description = "门诊基础信息管理（门诊基础信息查询，编辑）")
public class MedicalOrganizationController {

  /** 注入对象 */
  private final ClinicExtInfoBiz clinicExtInfoBiz;

  public MedicalOrganizationController(ClinicExtInfoBiz clinicExtInfoBiz) {
    this.clinicExtInfoBiz = clinicExtInfoBiz;
  }

  /**
   * 根据组织ID获取医疗机构完整信息
   *
   * @param companyId 组织ID
   * @return map
   */
  @ApiOperation("根据组织ID查询门诊基础信息")
  @ApiImplicitParam(
      name = "companyId",
      value = "组织ID",
      dataType = "int",
      required = true,
      paramType = "path")
  @GetMapping("/medical/{companyId}")
  public ResponseResult findMedicalOrganizationInfo(@PathVariable Integer companyId) {
    MedicalOrganizationInfoVO resultVO = clinicExtInfoBiz.findMedicalOrganizationInfo(companyId);
    return ResponseUtil.success(resultVO);
  }

  /**
   * 编辑医疗机构信息
   *
   * @param companyId 组织ID
   * @param form 参数封装
   * @return map
   */
  @ApiOperation("编辑门诊基础信息")
  @ApiImplicitParam(
      name = "companyId",
      value = "组织ID",
      dataType = "int",
      required = true,
      paramType = "path")
  @PutMapping("/medical/edit/{companyId}")
  public ResponseResult edit(
      @PathVariable Integer companyId, @RequestBody @Validated MedicalOrganizationInfoForm form) {
    clinicExtInfoBiz.edit(companyId, form);
    return ResponseUtil.success();
  }
}
