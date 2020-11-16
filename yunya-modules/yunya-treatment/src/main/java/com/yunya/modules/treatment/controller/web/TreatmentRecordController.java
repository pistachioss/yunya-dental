package com.yunya.modules.treatment.controller.web;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.treatment.domain.model.TreatmentModel;
import com.yunya.feign.treatment.domain.query.PatientTreatmentRecordQueryForm;
import com.yunya.feign.treatment.domain.query.TreatmentCountQuery;
import com.yunya.feign.treatment.domain.query.TreatmentRecordQueryForm;
import com.yunya.feign.treatment.domain.vo.LastTreatmentInfoVO;
import com.yunya.feign.treatment.domain.vo.PatientTreatmentRecordVO;
import com.yunya.feign.treatment.domain.vo.TreatmentPatientInfoVO;
import com.yunya.feign.treatment.domain.vo.TreatmentRecordVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.TreatmentRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
   * @param model 接诊信息
   * @return
   */
  @CurrentUser
  @ApiOperation("开始接诊")
  @PostMapping("/start")
  public ResponseResult<T> startTreatment(@RequestBody @Validated TreatmentModel model) {
    treatmentRecordBiz.startTreatment(model);
    return ResponseUtil.success(null);
  }

  /**
   * 根据就诊记录ID查询就诊记录信息
   *
   * @param id 就诊记录ID
   * @return
   */
  @ApiOperation("根据就诊记录ID查询就诊记录信息")
  @ApiImplicitParams({@ApiImplicitParam(name = "id", value = "就诊记录ID", required = true)})
  @GetMapping(value = "/one/{id}", name = "根据就诊ID查询就诊记录信息")
  public ResponseResult<TreatmentRecordVO> findById(@PathVariable(value = "id") Integer id) {
    TreatmentRecordVO resultData = treatmentRecordBiz.findTreatmentInfoById(id);
    return ResponseUtil.success(resultData);
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
  @CurrentUser
  public ResponseResult<T> completeTreatment(
      @PathVariable(value = "treatmentRecordId") Integer treatmentRecordId) {
    treatmentRecordBiz.completeTreatment(treatmentRecordId);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询患者就诊记录列表
   *
   * @param queryForm 查询条件
   * @return resultList
   */
  @ApiOperation("根据条件查询患者就诊记录列表(患者档案就诊列表)")
  @PostMapping(value = "/patient/list", name = "患者就诊记录列表")
  public ResponseResult<PageInfo<PatientTreatmentRecordVO>> treatmentRecordBiz(
      @RequestBody @Validated PatientTreatmentRecordQueryForm queryForm) {
    PageInfo<PatientTreatmentRecordVO> resultList =
        treatmentRecordBiz.findPatientTreatList(queryForm);
    return ResponseUtil.success(resultList);
  }

  /**
   * 根据患者ID查询患者最后一次就诊信息
   *
   * @param patientId 患者ID
   * @return 实体
   */
  @ApiOperation("根据患者ID查询患者最后一次就诊信息(随访管理、随访提醒--添加)")
  @GetMapping(value = "/last/treatment/info/{patientId}", name = "患者ID")
  public ResponseResult<LastTreatmentInfoVO> lastTreatmentInfo(
      @PathVariable("patientId") Integer patientId) {
    LastTreatmentInfoVO lastTreatmentInfoVO = this.treatmentRecordBiz.lastTreatmentInfo(patientId);
    return ResponseUtil.success(lastTreatmentInfoVO);
  }

  /**
   * 查询门诊某天的就诊列表数量
   *
   * @param orgId 组织ID
   * @param queryDate 查询日期
   * @return
   */
  @ApiOperation("就诊列表数量统计")
  @ApiImplicitParams({
    @ApiImplicitParam(
        name = "orgId",
        value = "组织ID",
        required = true,
        dataType = "int",
        paramType = "path"),
    @ApiImplicitParam(
        name = "queryDate",
        value = "查询日期（yyyy-MM-dd）",
        required = true,
        dataType = "String",
        paramType = "path")
  })
  @PostMapping(value = "/count", name = "就诊列表数量统计")
  public ResponseResult<Map<String, Integer>> count(@RequestBody TreatmentCountQuery query) {
    Map<String, Integer> resultMap = treatmentRecordBiz.countTreatList(query);
    return ResponseUtil.success(resultMap);
  }
}
