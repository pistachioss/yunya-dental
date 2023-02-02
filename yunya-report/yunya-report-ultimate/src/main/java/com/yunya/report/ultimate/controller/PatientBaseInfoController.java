package com.yunya.report.ultimate.controller;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.patient_central.domain.query.PatientSearchQuery;
import com.yunya.feign.report.domain.query.PatientManageQuery;
import com.yunya.feign.report.domain.query.PatientOriginConsumptionQuery;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.report.ultimate.biz.PatientBaseInfoBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
  @Autowired private PatientBaseInfoBiz patientBaseInfoBiz;

  /**
   * 根据关键字搜索患者信息
   *
   * @param query 查询条件
   * @return 患者信息
   */
  @ApiOperation("根据条件搜索患者信息")
  @PostMapping(value = "/full/search", name = "根据条件搜索患者信息")
  public ResponseResult<PageInfo<PatientInfoVO>> patientSearch(
      @RequestBody PatientSearchQuery query) {
    PageInfo<PatientInfoVO> patientInfo = patientBaseInfoBiz.findPatientInfoByExample(query);
    return ResponseUtil.success(patientInfo);
  }

  /**
   * 查询患者资料信息
   *
   * @param patientId 患者id
   * @return 患者资料信息
   */
  @ApiOperation("查询患者资料就诊信息")
  @GetMapping("/patientInfo/{patientId}")
  public ResponseResult<PatientDataVo> patientInfo(@PathVariable("patientId") Integer patientId) {
    PatientDataVo patientDataVo = patientBaseInfoBiz.patientDataVo(patientId);
    return ResponseUtil.success(patientDataVo);
  }

  @ApiOperation("客服中心-患者管理")
  @PostMapping("/manage/page")
  public ResponseResult<PageInfo<PatientManageVo>> patientInfo(@RequestBody PatientManageQuery query) {
    PageInfo<PatientManageVo> page = patientBaseInfoBiz.getPatientManagePage(query);
    return ResponseUtil.success(page);
  }

  @ApiOperation("患者报表-患者生日表")
  @PostMapping("/manage/birthday")
  public ResponseResult<PageInfo<PatientBirthdayVo>> patientBirthdayInfo(@RequestBody PatientManageQuery query) {
    PageInfo<PatientBirthdayVo> page = patientBaseInfoBiz.getPatientBirthdayPage(query);
    return ResponseUtil.success(page);
  }

  @ApiOperation(value = "客服中心-患者管理-导出")
  @PostMapping("/manage/page/export")
  public void exportPatientManage(HttpServletResponse response, @RequestBody PatientManageQuery query) throws IOException {
    patientBaseInfoBiz.buildResponse(response, "患者报表");
    EasyExcel.write(response.getOutputStream(), PatientManageVo.class)
            .sheet("sheet").doWrite(patientBaseInfoBiz.listPatientManage(query));

  }

  @ApiOperation(value = "患者报表-患者生日表-导出")
  @PostMapping("/manage/birthday/export")
  public void exportPatientBirthday(HttpServletResponse response, @RequestBody PatientManageQuery query) throws IOException {
    patientBaseInfoBiz.buildResponse(response, "患者生日表");
    EasyExcel.write(response.getOutputStream(), PatientBirthdayVo.class)
            .sheet("sheet").doWrite(patientBaseInfoBiz.getPatientBirthdayPage(query).getList());

  }

  /**
   * 根据条件查询渠道来源消费报表
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-市场报表-渠道来源消费报表")
  @PostMapping(value = "/originConsumption/list", name = "公司端报表-报表统计-市场报表-渠道来源消费报表")
  public ResponseResult<PageInfo<PatientOriginConsumptionVO>> findPatientOriginConsumption(
          @RequestBody @Validated PatientOriginConsumptionQuery query) {
    PageInfo<PatientOriginConsumptionVO> pageInfo = patientBaseInfoBiz.findPatientOriginConsumption(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出渠道来源患者消费数据
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-市场报表-渠道来源消费报表导出")
  @PostMapping(value = "/originConsumption/list/export", name = "公司端报表-报表统计-市场报表-渠道来源消费报表导出")
  public ResponseResult<T> exportPatientOriginConsumption(
          HttpServletResponse response, @RequestBody @Validated PatientOriginConsumptionQuery query)
          throws Exception {
    patientBaseInfoBiz.exportPatientOriginConsumption(query, response);
    return ResponseUtil.success(null);
  }

  /**
   * 根据患者id查询末次就诊信息
   *
   * @param patientId
   * @return
   */
  @ApiOperation("客户画像-患者末次就诊信息")
  @GetMapping("/lastTreatment/{patientId}")
  public ResponseResult<PatientTreatInfoVo> findPatientLastTreatmentInfo(@PathVariable(value = "patientId") Integer patientId) {
    PatientTreatInfoVo result = patientBaseInfoBiz.findPatientLastTreatmentInfo(patientId);
    return ResponseUtil.success(result);
  }
}
