//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunya.modules.patient_central.controller.web;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.form.PatientKinRelationForm;
import com.yunya.feign.patient_central.domain.model.PatientKinRelationModel;
import com.yunya.feign.patient_central.domain.query.PatientKinRelationQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.PatientKinRelationVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.patient_central.biz.PatientKinRelationBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简介: 亲属关系控制器
 *
 * @author: WY
 * @date: 2020/9/18 11:14
 * @description:
 * @since: 1.0.0
 */
@Api(value = "亲属关系", description = "亲属关系（增删查改）")
@RestController
@RequestMapping("kin")
public class PatientKinRelationController {

  /** 注入对象 */
  private final PatientKinRelationBiz patientKinRelationBiz;

  public PatientKinRelationController(PatientKinRelationBiz patientKinRelationBiz) {
    this.patientKinRelationBiz = patientKinRelationBiz;
  }

  /**
   * 查询患者关系亲属列表
   *
   * @param patientKinRelationQueryForm 患者亲属关系信息查询QueryFrom
   * @return ResponseResult<PageInfo<PatientKinRelationVo>>
   */
  @ApiOperation("查询患者关系亲属列表（可分页)")
  @PostMapping("/findList")
  public ResponseResult<PageInfo<PatientKinRelationVo>> findList(
      @RequestBody PatientKinRelationQueryForm patientKinRelationQueryForm) {
    return ResponseUtil.success(this.patientKinRelationBiz.findList(patientKinRelationQueryForm));
  }

  /**
   * 添加患者亲属关系
   *
   * @param patientKinRelationModel 患者亲属关系添加模板
   * @return ResponseResult
   */
  @CurrentUser
  @ApiOperation("添加患者亲属关系")
  @PostMapping("/add")
  public ResponseResult add(
      @RequestBody @Validated PatientKinRelationModel patientKinRelationModel) {
    return this.patientKinRelationBiz.add(patientKinRelationModel);
  }

  /**
   * 修改患者亲属关系
   *
   * @param patientKinRelationForm 患者亲属关系修改模板
   * @return ResponseResult
   */
  @CurrentUser
  @ApiOperation("修改患者亲属关系")
  @PostMapping("/update")
  public ResponseResult update(
      @RequestBody @Validated PatientKinRelationForm patientKinRelationForm) {
    this.patientKinRelationBiz.update(patientKinRelationForm);
    return ResponseUtil.success();
  }

  /**
   * 删除患者亲属关系
   *
   * @param id 患者id
   * @return ResponseResult
   */
  @ApiOperation("删除患者亲属关系")
  @DeleteMapping("/deleteById/{id}")
  public ResponseResult deleteById(@PathVariable("id") Integer id) {
    patientKinRelationBiz.tombstone(id);
    return ResponseUtil.success();
  }
}
