package com.yunya.modules.patient_central.controller.web;

import com.yunya.feign.patient_central.domain.form.PatientOriginForm;
import com.yunya.feign.patient_central.domain.model.PatientOriginModel;
import com.yunya.feign.patient_central.domain.query.OriginTypeQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientAndStaffListInfoQueryForm;
import com.yunya.feign.patient_central.domain.vo.web.PatientOriginTreeVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientOriginVo;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.patient_central.PatientOrigin;
import com.yunya.modules.patient_central.biz.PatientOriginBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 简单介绍:</br> 患者来源信息 控制层
 *
 * @author: WY
 * @date 2020/7/30 13:22
 * @description: 患者来源增删改查
 * @since: 1.0.0
 */
@Api(value = "患者来源", description = "患者来源（增删查改）")
@RestController
@RequestMapping("origin")
public class PatientOriginController {

  /** 注入对象 */
  private final PatientOriginBiz patientOriginBiz;

  public PatientOriginController(PatientOriginBiz patientOriginBiz) {
    this.patientOriginBiz = patientOriginBiz;
  }

  /**
   * 新建患者来源分类
   *
   * @param patientOriginModel 患者来源分类添加模板类
   * @return ResponseResult<PageInfo<PrepaidExpendRecordVo>>
   */
  @CurrentUser
  @ApiOperation("新建患者来源分类")
  @PostMapping("/add")
  public ResponseResult add(@RequestBody @Validated PatientOriginModel patientOriginModel) {
    return patientOriginBiz.add(patientOriginModel);
  }

  /**
   * 查询患者来源树状结构列表
   *
   * @return ResponseResult<List<PatientOriginTreeVo>>
   */
  @ApiOperation("查询患者来源树状结构列表")
  @GetMapping("/initPatientOriginTree")
  public ResponseResult<List<PatientOriginTreeVo>> initPatientOriginTree() {
    List<PatientOriginTreeVo>list = this.patientOriginBiz.initPatientOriginTree();
//    PatientOriginTreeVo patientOriginTreeVo = new PatientOriginTreeVo();
//    patientOriginTreeVo.setInservice(true);
//    patientOriginTreeVo.setOriginType(0);
//    patientOriginTreeVo.setName("未知来源");
//    list.add(patientOriginTreeVo);
    return ResponseUtil.success(list);
  }

  /**
   * 修改患者来源
   *
   * @param patientOriginForm 患者来源修改Form
   * @return ResponseResult
   */
  @CurrentUser
  @ApiOperation("修改患者来源")
  @PostMapping("/update")
  public ResponseResult update(@RequestBody PatientOriginForm patientOriginForm) {
    return this.patientOriginBiz.update(patientOriginForm);
  }

  /**
   * 修改赠金返点比例
   *
   * @return ResponseResult
   */
  @CurrentUser
  @ApiOperation("修改患者来源")
  @PostMapping("/giftrebaterate")
  public ResponseResult updateGift(@RequestBody PatientOriginForm patientOriginForm) {
    return this.patientOriginBiz.updateRate(patientOriginForm);
  }

  /**
   * 删除患者来源
   *
   * @param id 患者来源id
   * @return ResponseResult
   */
  @ApiOperation("删除患者来源")
  @DeleteMapping("/delete/{id}")
  public ResponseResult delete(@PathVariable("id") Integer id) {
    return this.patientOriginBiz.deleteOriginById(id);
  }

  /**
   * 模糊查询员工/老患者信息
   *
   * @param form 模糊查询员工和患者信息QueryForm
   * @return ResponseResult
   */
  @ApiOperation("模糊查询员工/老患者信息")
  @PostMapping("/originType")
  public ResponseResult findPatientAndStaffListInfo(
      @RequestBody @Validated PatientAndStaffListInfoQueryForm form) {
    return this.patientOriginBiz.findPatientAndStaffListInfo(form);
  }

  /**
   * 查询活动/合作商信息
   *
   * @param form 查询患者来源类型
   * @return ResponseResult<List<PatientOrigin>>
   */
  @ApiOperation("查询活动/合作商信息")
  @PostMapping("/originTypeList")
  public ResponseResult<List<PatientOrigin>> findPatientOriginByTypt(
      @RequestBody OriginTypeQueryForm form) {
    return ResponseUtil.success(this.patientOriginBiz.findPatientOriginByTypt(form));
  }

  /**
   * 查询患者来源类型
   *
   * @return ResponseResult<List<PatientOriginVo>>
   */
  @ApiOperation("查询患者来源类型")
  @GetMapping("/originalType")
  public ResponseResult<List<PatientOriginVo>> findoriginalType() {
    return ResponseUtil.success(this.patientOriginBiz.originalType());
  }

  /**
   * 查询患者来源类型及其子类型
   *
   * @return ResponseResult<List<PatientOriginVo>>
   */
  @ApiOperation("查询患者来源类型及其子类型")
  @GetMapping("/originalTypeAndChildren")
  public ResponseResult<List<PatientOriginVo>> findOriginalTypeAndChildren() {
    List<PatientOriginVo> result = patientOriginBiz.findOriginalTypeAndChildren();
    return ResponseUtil.success(result);
  }


  /**
   * 迁移患者信息来源到患者来源变更日志表
   * @return void
   */
  @ApiOperation("迁移患者信息来源到患者来源变更日志表")
  @PostMapping("/move/origin")
  public ResponseResult<Boolean> moveOrigin() throws InterruptedException {
    this.patientOriginBiz.moveOrigin();
    return ResponseUtil.success();
  }

}
