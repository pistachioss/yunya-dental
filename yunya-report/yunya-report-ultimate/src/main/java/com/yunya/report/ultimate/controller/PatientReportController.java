package com.yunya.report.ultimate.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.form.PatientNotSeenForm;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.query.base.FuchaForm;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.models.report.BaseEmployee;
import com.yunya.report.ultimate.biz.PatientReportBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 简介:患者报表控制层
 *
 * @author: WY
 * @date: 2020/10/27 20:08
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "公司端-运营报表-患者报表")
@RestController
@RequestMapping("patient")
public class PatientReportController {

  /** 注入服务 */
  @Autowired private PatientReportBiz patientReportBiz;
  /**
   * 复查患者报表
   *
   * @return 复查患者报表
   */
  @ApiOperation("复查患者报表")
  @PostMapping("/fucha/list")
  public ResponseResult<PageInfo<FuchaVO>> fuchaList( @RequestBody FuchaForm fuchaForm) {
    return ResponseUtil.success(this.patientReportBiz.fuchaList(fuchaForm));
  }
  /**
   * 导出复查患者报表
   *
   * @param response 导出响应
   * @param fuchaForm 查询条件
   * @return 未复查患者报表
   */
  @ApiOperation("导出复查患者报表")
  @PostMapping(value = "/fucha/export", name = "公司端-运营报表-患者报表-复查患者报表-导出")
  public ResponseResult<T> exportfuchaList(
          HttpServletResponse response,
          @RequestBody @Validated FuchaForm fuchaForm)
          throws IOException {
    patientReportBiz.exportfuchaList(response, fuchaForm);
    return ResponseUtil.success(null);
  }
  /**
   * 末次接诊医生
   *
   * @return 末次接诊医生
   */
  @ApiOperation("末次接诊医生")
  @GetMapping("/employee/list/{orgId}")
  public ResponseResult<List<BaseEmployee>> employeeList(@PathVariable Integer orgId) {
    return ResponseUtil.success(this.patientReportBiz.employeeList(orgId));
  }

  /**
   * 未复诊预约且未提醒
   *
   * @param patientReportQueryForm 未复诊预约且未提醒form
   * @return 未复诊预约且未提醒集合
   */
  @ApiOperation("未复诊预约且未提醒")
  @PostMapping("/notSeen/List")
  public ResponseResult<PageInfo<BasePatientNotSeenVo>> notSeenList(
      @RequestBody PatientReportQueryForm patientReportQueryForm) {
    PageInfo<BasePatientNotSeenVo> basePatientNotSeenVoList =
        patientReportBiz.notSeenList(patientReportQueryForm);
    return ResponseUtil.success(basePatientNotSeenVoList);
  }
  /**
   * 删除未复诊预约未提醒患者记录
   *
   * @param form 参数
   * @return void
   */
  @ApiOperation("删除未复诊预约且未提醒")
  @PostMapping("/notSeen/delete")
  public ResponseResult<T> notSeenList(@RequestBody @Validated PatientNotSeenForm form) {
    patientReportBiz.deleteTrent(form);
    return ResponseUtil.success(null);
  }
  /**
   * 导出未复诊预约且未提醒记录列表
   *
   * @param response 导出响应
   * @param patientReportQueryForm 查询条件
   * @return 未复诊预约且未提醒记录列表
   */
  @ApiOperation("导出未复诊预约且未提醒记录列表")
  @PostMapping(value = "/notSeen/export", name = "公司端-运营报表-患者报表-导出")
  public ResponseResult<T> exportNotSeenList(
      HttpServletResponse response,
      @RequestBody @Validated PatientReportQueryForm patientReportQueryForm)
      throws IOException {
    patientReportBiz.exportNotSeenList(response, patientReportQueryForm);
    return ResponseUtil.success(null);
  }

  /**
   * 欠费查询
   *
   * @param arrearsQueryForm 欠费查询form
   * @return List<ArrearsVo>
   */
  @ApiOperation("欠费查询")
  @PostMapping("/arrears")
  public ResponseResult<ArrearsStatisticsVo> arrears(
      @RequestBody ArrearsQueryForm arrearsQueryForm) {
    ArrearsStatisticsVo arrears = patientReportBiz.findArrears(arrearsQueryForm);
    return ResponseUtil.success(arrears);
  }

  /**
   * 导出欠费查询记录列表
   *
   * @param response 响应
   * @param arrearsQueryForm 查询条件
   * @return 欠费查询记录列表
   */
  @ApiOperation("导出欠费查询记录列表")
  @PostMapping(value = "/arrears/export", name = "公司端-运营报表-患者报表-导出")
  public ResponseResult<T> exportArrearsList(
      HttpServletResponse response, @RequestBody @Validated ArrearsQueryForm arrearsQueryForm)
      throws IOException {
    patientReportBiz.exportArrearsList(response, arrearsQueryForm);
    return ResponseUtil.success(null);
  }

  /**
   * 就诊患者分析
   *
   * @param patientAnalysisQueryForm 就诊患者分析form
   * @return List<ArrearsVo>
   */
  @ApiOperation("就诊患者分析")
  @PostMapping("/analysis")
  public ResponseResult<AnalysisVo> analysis(
      @RequestBody PatientAnalysisQueryForm patientAnalysisQueryForm) {
    return ResponseUtil.success(patientReportBiz.analysis(patientAnalysisQueryForm));
  }

  /**
   * 根据条件查询老客回访列表
   *
   * @return
   */
  @ApiOperation("根据条件查询老客回访列表")
  @PostMapping("/returnVisit/list")
  public ResponseResult<PageInfo<BaseReturnVisitVO>> findReturnVisitList(@RequestBody @Validated BaseReturnVisitQuery query) {
    PageInfo<BaseReturnVisitVO> page = patientReportBiz.findReturnVisitList(query);
    return ResponseUtil.success(page);
  }

  /**
   * 根据条件导出老客回访列表
   *
   * @return
   */
  @ApiOperation("根据条件导出老客回访列表")
  @PostMapping("/returnVisit/export")
  public ResponseResult exportReturnVisitList(@RequestBody @Validated BaseReturnVisitQuery query, HttpServletResponse response) throws IOException {
    patientReportBiz.exportReturnVisitList(query, response);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询个人接诊患者列表（门诊端-个人中心-个人报表）
   *
   * @param query
   * @return
   * @throws Exception
   */
  @ApiOperation("根据条件查询个人接诊患者列表（门诊端-个人中心-个人报表）")
  @PostMapping("/empReception/list")
  public ResponseResult<PageInfo<EmployeeReceptionPatientVO>> findEmployeeReceptionPatientList(@RequestBody @Validated EmployeeReceptionPatientQueryForm query){
    PageInfo<EmployeeReceptionPatientVO> pageInfo = patientReportBiz.findEmployeeReceptionPatientList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出个人接诊患者列表（门诊端-个人中心-个人报表）
   *
   * @param query
   * @return
   * @throws Exception
   */
  @ApiOperation("根据条件查询个人接诊患者列表（门诊端-个人中心-个人报表）")
  @PostMapping("/empReception/export")
  public ResponseResult exportEmployeeReceptionPatientList(@RequestBody @Validated EmployeeReceptionPatientQueryForm query, HttpServletResponse response) throws Exception{
    patientReportBiz.exportEmployeeReceptionPatientList(query, response);
    return ResponseUtil.success();
  }
}
