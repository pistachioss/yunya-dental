package com.yunya.modules.treatment.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment.domain.query.PatientTreatmentRecordQueryForm;
import com.yunya.feign.treatment.domain.query.TreatmentRecordQueryForm;
import com.yunya.feign.treatment.domain.vo.PatientTreatmentRecordVO;
import com.yunya.feign.treatment.domain.vo.TreatmentPatientInfoVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.TreatmentRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 简介: 患者接诊管理控制器
 *
 * @author: chow
 * @date: 2020/8/12 14:48
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "患者接诊管理（开始接诊、就诊中列表查询）")
@RestController
@RequestMapping("admission")
public class TreatmentRecordController {

  /** 注入服务 */
  private final TreatmentRecordBiz treatmentRecordBiz;

  public TreatmentRecordController(TreatmentRecordBiz treatmentRecordBiz) {
    this.treatmentRecordBiz = treatmentRecordBiz;
  }

  /**
   * 根据挂号ID接诊患者
   *
   * @param regId 挂号ID
   * @return
   */
  @ApiOperation("开始接诊")
  @ApiImplicitParam(name = "regId", required = true, value = "患者挂号ID")
  @CurrentUser
  @GetMapping("/start/{regId}")
  public ResponseResult startTreatment(@PathVariable(value = "regId") Integer regId) {
    treatmentRecordBiz.startTreatment(regId);
    return ResponseUtil.success();
  }

  /**
   * 根据条件查询就诊中患者列表信息（可分页）
   *
   * @param queryForm 查询条件
   * @return
   */
  @ApiOperation("根据条件查询就诊患者列表信息（可分页）")
  @PostMapping("/list")
  public ResponseResult<PageInfo<TreatmentPatientInfoVO>> findTreatList(
      @RequestBody @Validated TreatmentRecordQueryForm queryForm) {
    PageInfo<TreatmentPatientInfoVO> resultList = treatmentRecordBiz.findTreatList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 治疗完成
   *
   * @param treatmentRecordId 就诊记录ID
   * @return
   */
  @ApiOperation("治疗完成")
  @ApiImplicitParam(
      name = "treatmentRecordId",
      value = "接诊记录ID",
      required = true,
      dataType = "int",
      paramType = "path")
  @GetMapping("/complete/{treatmentRecordId}")
  public ResponseResult completeTreatment(
      @PathVariable(value = "treatmentRecordId") Integer treatmentRecordId) {
    treatmentRecordBiz.completeTreatment(treatmentRecordId);
    return ResponseUtil.success();
  }

  /**
   * 根据条件查询患者就诊记录列表
   *
   * @param queryForm 查询条件
   * @return resultList
   */
  @ApiOperation("根据条件查询患者就诊记录列表(患者档案就诊列表)")
  @ApiImplicitParam(name = "queryFrom", value = "患者就诊记录列表查询参数模型", required = true)
  @PostMapping(value = "/patient/list", name = "患者就诊记录列表")
  public ResponseResult<PageInfo<PatientTreatmentRecordVO>> treatmentRecordBiz(
      @RequestBody @Validated PatientTreatmentRecordQueryForm queryForm) {
    PageInfo<PatientTreatmentRecordVO> resultList =
        treatmentRecordBiz.findPatientTreatList(queryForm);
    return ResponseUtil.success(resultList);
  }
}
