package com.yunya.report.ultimate.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.bo.ClinicDataStatisticsInfoVO;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.report.ultimate.biz.BaseBillDetailBiz;
import com.yunya.report.ultimate.biz.BaseTariffInfoBiz;
import com.yunya.report.ultimate.biz.BaseUserPostBiz;
import com.yunya.report.ultimate.biz.ClinicDataStatisticsBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 简介: 公司端报表-报表统计-运营报表
 *
 * @author: chow
 * @date: 2020/12/4 14:25
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "公司端报表-报表统计-运营报表")
@RestController
@RequestMapping("operation")
public class CompanyReportOfOperationController {

  /** 账单明细 */
  @Autowired private BaseBillDetailBiz billDetailBiz;
  /** 开单项目 */
  @Autowired private BaseTariffInfoBiz tariffInfoBiz;
  /** 员工 */
  @Autowired private BaseUserPostBiz userPostBiz;
  /** 门诊数据统计 */
  @Autowired private ClinicDataStatisticsBiz clinicDataStatisticsBiz;

  /**
   * 根据条件查询员工工作量列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeWorkloadOfOperationVO>
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-员工工作量")
  @PostMapping(value = "/employee/workload/list", name = "根据条件查询员工工作量列表")
  public ResponseResult<PageInfo<EmployeeWorkloadOfOperationVO>> employeeWorkloadListOfOperation(
      @RequestBody @Validated EmployeeWorkloadQuery query) {
    PageInfo<EmployeeWorkloadOfOperationVO> pageInfo =
        billDetailBiz.findEmployeeWorkloadListOfOperation(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出员工工作量报表
   *
   * @param response http响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-员工工作量-导出")
  @PostMapping(value = "/workload/list/export", name = "根据条件导出员工工作量报表")
  public ResponseResult<T> exportEmployeeWorkloadListOfOperationVO(
      HttpServletResponse response, @RequestBody @Validated EmployeeWorkloadQuery query)
      throws IOException {
    billDetailBiz.exportEmployeeWorkloadListOfOperation(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询员工看诊情况列表
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-员工看诊情况")
  @PostMapping(value = "/employee/diagnosis/list", name = "根据条件查询员工看诊情况列表")
  public ResponseResult<PageInfo<EmployeeDiagnosisInfoVO>> employeeDiagnosisInfoList(
      @RequestBody @Validated EmployeeDiagnosisQuery query) {
    PageInfo<EmployeeDiagnosisInfoVO> pageInfo = userPostBiz.findEmployeeDiagnosisInfoList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出员工看诊情况列表
   *
   * @param response http响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-员工看诊情况-导出")
  @PostMapping(value = "/employee/diagnosis/list/export", name = "根据条件导出员工看诊情况列表")
  public ResponseResult<T> exportEmployeeDiagnosisInfoList(
      HttpServletResponse response, @RequestBody @Validated EmployeeDiagnosisQuery query)
      throws IOException {
    userPostBiz.exportEmployeeDiagnosisInfoList(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 获取全部项目分类列表
   *
   * @return List<ItemCategoryInfoVO>
   */
  @ApiOperation("获取全部项目分类列表（价目表+商品）")
  @GetMapping(value = "/item/category/list", name = "根据条件查询项目分类列表")
  public ResponseResult<List<ItemCategoryInfoVO>> itemCategoryList() {
    List<ItemCategoryInfoVO> categoryList = tariffInfoBiz.findItemCategoryList();
    return ResponseUtil.success(categoryList);
  }

  /**
   * 根据条件查询开单项目数量列表
   *
   * @param query 查询条件
   * @return PageInfo<BillingItemInfoVO>
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-开单项目数量")
  @PostMapping(value = "/billing/item/list", name = "根据条件查询开单项目数量列表")
  public ResponseResult<PageInfo<BillingItemInfoVO>> billingItemStatisticsList(
      @RequestBody @Validated BillingItemStatisticsQuery query) {
    PageInfo<BillingItemInfoVO> pageInfo = billDetailBiz.findBillingItemInfoVOList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出开单项目数量列表
   *
   * @param response http响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-开单项目数量-导出")
  @PostMapping(value = "/billing/item/list/export", name = "根据条件导出开单项目数量列表")
  public ResponseResult<T> exportBillingItemInfoList(
      HttpServletResponse response, @RequestBody @Validated BillingItemStatisticsQuery query)
      throws IOException {
    billDetailBiz.exportBillingItemInfoList(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询开单项目统计明细列表
   *
   * @param query 查询条件
   * @return PageInfo<BillingItemDetailVO>
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-开单项目数量-开单项目统计明细")
  @PostMapping(value = "/billing/item/detail/list", name = "根据条件查询开单项目统计明细列表")
  public ResponseResult<PageInfo<BillingItemDetailVO>> billingItemDetailList(
      @RequestBody @Validated BillingItemDetailQuery query) {
    PageInfo<BillingItemDetailVO> pageInfo = billDetailBiz.findBillingItemDetailList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出开单项目统计明细列表
   *
   * @param response http响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-开单项目数量-开单项目统计明细-导出")
  @PostMapping(value = "/billing/item/detail/list/export", name = "根据条件导出开单项目统计明细列表")
  public ResponseResult<T> exportBillingItemDetailList(
      HttpServletResponse response, @RequestBody @Validated BillingItemDetailQuery query)
      throws IOException {
    billDetailBiz.exportBillingItemDetailList(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询门诊业务目标列表
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-工作目标-业务目标")
  @PostMapping(value = "/goal/business/list", name = "根据条件查询门诊业务目标列表")
  public ResponseResult<T> clinicWorkGoalList(@RequestBody @Validated ClinicWorkGoalQuery query) {

    return ResponseUtil.success(null);
  }

  /**
   * 根据条件导出门诊业务目标列表
   *
   * @param response http响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-工作目标-业务目标-导出")
  @PostMapping(value = "/goal/business/list/export", name = "根据条件导出门诊业务目标列表")
  public ResponseResult<T> exportClinicWorkGoalList(
      HttpServletResponse response, @RequestBody @Validated ClinicWorkGoalQuery query) {

    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询门诊运营分析数据总览
   *
   * @param query 查询条件
   * @return ClinicDataStatisticsInfoVO
   */
  @ApiOperation("公司端报表-报表统计-运营报表-运营分析-数据总览")
  @PostMapping(value = "/analysis/data/statistic", name = "根据条件查询门诊运营分析数据总览")
  public ResponseResult<ClinicDataStatisticsInfoVO> operationalAnalysisDataStatistics(
      @RequestBody @Validated DataStatisticsQuery query) {
    ClinicDataStatisticsInfoVO dataStatisticsInfo =
        clinicDataStatisticsBiz.findClinicDataStatisticsInfo(query);
    return ResponseUtil.success(dataStatisticsInfo);
  }
}
