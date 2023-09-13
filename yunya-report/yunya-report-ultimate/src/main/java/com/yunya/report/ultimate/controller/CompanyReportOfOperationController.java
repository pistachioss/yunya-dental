package com.yunya.report.ultimate.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.bo.ClinicDataStatisticsInfoVO;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.report.ultimate.biz.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
  /** 卡券*/
  @Autowired private BaseCardBiz baseCardBiz;

  /**
   * 根据条件查询员工工作量列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeWorkloadOfOperationVO>
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-员工工作量")
  @PostMapping(value = "/employee/workload/list/single", name = "根据条件查询员工工作量列表")
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
  @PostMapping(value = "/workload/list/export/single", name = "根据条件导出员工工作量报表")
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
      @RequestBody @Validated EmployeeDiagnosisQuery query) throws Exception {
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
      throws Exception {
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
   * 根据项目类型（价目/商品）查询项目分类列表
   *
   * @return List<ItemCategoryInfoVO>
   */
  @ApiOperation("根据项目类型（价目/商品）查询项目分类列表")
  @PostMapping(value = "/item/categoryList", name = "根据项目类型（价目/商品）查询项目分类列表")
  public ResponseResult<List<ItemCategoryInfoVO>> itemCategoryList(
      @RequestBody @Validated CategoryQuery query) {
    List<ItemCategoryInfoVO> categoryList = tariffInfoBiz.findItemCategoryList(query.getItemType());
    return ResponseUtil.success(categoryList);
  }

  /**
   * 根据项目分类ID查询全部项目列表
   *
   * @return List<ItemInfoVO>
   */
  @ApiOperation("根据项目分类ID查询全部项目列表")
  @PostMapping(value = "/item/itemList", name = "根据项目分类ID查询全部项目列表")
  public ResponseResult<List<ItemInfoVO>> itemListByCategoryId(
      @RequestBody @Validated CategoryQuery query) {
    List<ItemInfoVO> categoryList =
        tariffInfoBiz.findItemListByCategoryId(query.getItemType(), query.getCategoryId());
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

  /**
   * 根据条件导出门诊运营分析数据总览
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-运营分析-数据总览导出")
  @PostMapping(value = "/analysis/data/statistic/export", name = "根据条件导出门诊运营分析数据总览")
  public ResponseResult<T> operationalAnalysisDataStatisticsExport(
      HttpServletResponse response, @RequestBody @Validated DataStatisticsQuery query)
      throws IOException {
    clinicDataStatisticsBiz.exportClinicDataStatisticsInfo(query, response);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询门诊运营分析患者数据总览
   *
   * @param query 查询条件
   * @return ClinicDataStatisticsInfoVO
   */
  @ApiOperation("公司端报表-报表统计-运营报表-运营分析-运营BI（患者数据统计）")
  @PostMapping(value = "/analysis/patient/data/statistic", name = "根据条件查询门诊运营分析数据总览")
  public ResponseResult<PatientDataStatisticsVO> operationalAnalysisPatientDataStatistics(
      @RequestBody @Validated DataStatisticsQuery query) {
    PatientDataStatisticsVO patientDataStatisticsInfo =
        clinicDataStatisticsBiz.findClinicPatientDataStatisticsInfo(query);
    return ResponseUtil.success(patientDataStatisticsInfo);
  }

  /**
   * 根据条件查询不同来源患者初诊占比信息列表
   *
   * @param query 查询条件
   * @return PatientFirstTreatOriginInfoVO
   */
  @ApiOperation("公司端报表-报表统计-运营报表-运营分析-运营BI-初诊患者来源分布")
  @PostMapping(value = "/patient/first/treat/origin", name = "公司端报表-报表统计-运营报表-运营分析-运营BI-初诊患者来源分布")
  public ResponseResult<PatientFirstTreatOriginInfoVO> firstTreatPatientOrigin(
      @RequestBody @Validated PatientFirstTreatOriginQuery query) {
    PatientFirstTreatOriginInfoVO resultDate =
        clinicDataStatisticsBiz.findPatientFirstTreatOriginInfo(query);
    return ResponseUtil.success(resultDate);
  }

  /**
   * 根据条件查询门诊随访（提醒）完成率
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-运营分析-运营BI-随访（提醒）完成率")
  @PostMapping(value = "/visit/remind/completed", name = "公司端报表-报表统计-运营报表-运营分析-运营BI-随访（提醒）完成率")
  public ResponseResult<VisitAndRemindCompletedInfoVO> visitAndRemindCompletedInfo(
      @RequestBody @Validated VisitAndRemindCompletedInfoQuery query) {
    VisitAndRemindCompletedInfoVO resultData =
        clinicDataStatisticsBiz.findVisitAndRemindCompletedInfo(query);
    return ResponseUtil.success(resultData);
  }

  /**
   * 根据条件查询门诊运营综合数据
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-运营分析-运营BI-工作量完成率、初诊人数目标完成率、老患者介绍率、随访完成率、提醒完成率")
  @PostMapping(value = "/complex/data", name = "根据条件查询门诊运营综合数据")
  public ResponseResult<List<OperationDataComplexInfoVO>> operationDataComplexInfo(
      @RequestBody @Validated OperationDataComplexQuery query) {
    List<OperationDataComplexInfoVO> resultList =
        clinicDataStatisticsBiz.findOperationDataComplexInfo(query);
    return ResponseUtil.success(resultList);
  }

  /**
   * 根据条件查询运营报表的患者数据
   *
   * @param query 查询条件
   * @return PageInfo<PatientDataStatisticsVO>
   */
  @ApiOperation("公司端报表-报表统计-运营报表-运营分析-患者数据")
  @PostMapping(value = "/analysis/patient/data/list", name = "根据条件查询运营报表的患者数据")
  public ResponseResult<PageInfo<PatientDataStatisticsVO>> operationalAnalysisPatientDataList(
      @RequestBody @Validated DataStatisticsQuery query) {
    PageInfo<PatientDataStatisticsVO> result =
        clinicDataStatisticsBiz.findClinicPatientDataList(query);
    return ResponseUtil.success(result);
  }

  /**
   * 根据条件查询运营报表的患者数据导出
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-运营分析-患者数据导出")
  @PostMapping(value = "/analysis/patient/data/export", name = "根据条件查询运营报表的患者数据导出")
  public ResponseResult<T> operationalAnalysisPatientDataExport(
      HttpServletResponse response, @RequestBody @Validated DataStatisticsQuery query)
      throws IOException {
    clinicDataStatisticsBiz.ClinicPatientDataExport(query, response);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询运营报表的业务目标
   *
   * @param query 查询条件
   * @return PageInfo<PatientDataStatisticsVO>
   */
  @ApiOperation("公司端报表-报表统计-运营报表-运营分析-业务目标")
  @PostMapping(value = "/analysis/business/goal/list", name = "根据条件查询运营报表的业务目标")
  public ResponseResult<PageInfo<OperationDataBusinessGoalVO>> findAnalysisBusinessGoalList(
      @RequestBody @Validated DataStatisticsQuery query) {
    PageInfo<OperationDataBusinessGoalVO> result =
        clinicDataStatisticsBiz.findAnalysisBusinessGoalList(query);
    return ResponseUtil.success(result);
  }

  /**
   * 根据条件查询运营报表的业务目标导出
   *
   * @param query 查询条件
   * @return PageInfo<PatientDataStatisticsVO>
   */
  @ApiOperation("公司端报表-报表统计-运营报表-运营分析-业务目标导出")
  @PostMapping(value = "/analysis/business/goal/export", name = "根据条件查询运营报表的业务目标导出")
  public ResponseResult<PageInfo<OperationDataBusinessGoalVO>> findAnalysisBusinessGoalExport(
      HttpServletResponse response, @RequestBody @Validated DataStatisticsQuery query)
      throws IOException {
    clinicDataStatisticsBiz.analysisBusinessGoalExport(query, response);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询个人开单数量及金额列表
   *
   * @param query 查询条件
   * @return PageInfo<BillingItemInfoVO>
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-个人开单数量及金额")
  @PostMapping(value = "/billItem/statistics/list", name = "根据条件查询个人开单数量及金额列表")
  public ResponseResult<PageInfo<BillItemStatisticsVO>> billItemStatistics(
      @RequestBody @Validated BillItemInfoQuery query) {
    PageInfo<BillItemStatisticsVO> pageInfo = billDetailBiz.billItemStatistics(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件查询个人开单数量及金额列表导出
   *
   * @param query 查询条件
   * @return PageInfo<BillingItemInfoVO>
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-个人开单数量及金额导出")
  @PostMapping(value = "/billItem/statistics/list/export", name = "根据条件查询个人开单数量及金额列表导出")
  public ResponseResult<T> billItemStatisticsExport(
      HttpServletResponse response, @RequestBody @Validated BillItemInfoQuery query)
      throws IOException {
    billDetailBiz.billItemStatisticsExport(query, response);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询开单数量及金额统计明细列表
   *
   * @param query 查询条件
   * @return PageInfo<BillingItemDetailVO>
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-开单项目数量-开单数量及金额明细")
  @PostMapping(value = "/billItem/statistics/detail", name = "根据条件查询开单数量及金额统计明细列表")
  public ResponseResult<PageInfo<BillItemStatisticsDetailVO>> billItemStatisticsDetail(
      @RequestBody @Validated BillItemDetailQuery query) {
    PageInfo<BillItemStatisticsDetailVO> pageInfo = billDetailBiz.billItemStatisticsDetail(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件查询开单数量及金额统计明细列表导出
   *
   * @param query 查询条件
   * @return PageInfo<BillingItemDetailVO>
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-开单项目数量-开单数量及金额明细导出")
  @PostMapping(value = "/billItem/statistics/detail/export", name = "根据条件查询开单数量及金额统计明细列表导出")
  public ResponseResult<T> billItemStatisticsDetailExport(
      HttpServletResponse response, @RequestBody @Validated BillItemDetailQuery query)
      throws IOException {
    billDetailBiz.billItemStatisticsDetailExport(query, response);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询开单数量及金额全部明细列表导出
   *
   * @param query 查询条件
   * @return PageInfo<BillingItemDetailVO>
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-开单项目数量-开单数量及金额导出统计明细")
  @PostMapping(value = "/billItem/statistics/detail/allExport", name = "根据条件查询开单数量及金额全部明细列表导出")
  public ResponseResult<T> billItemStatisticsDetailAllExport(
      HttpServletResponse response, @RequestBody @Validated BillItemInfoQuery query)
      throws Exception {
    billDetailBiz.billItemStatisticsDetailAllExport(query, response);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询开单数量及金额导出明细一体表
   *
   * @param query 查询条件
   * @return PageInfo<BillingItemDetailVO>
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-开单项目数量-开单数量及金额导出明细一体表")
  @PostMapping(value = "/billItem/statistics/detail/integExport", name = "开单数量及金额导出明细一体表")
  public ResponseResult<T> billItemStatisticsDetailIntegrationExport(
          HttpServletResponse response, @RequestBody @Validated BillItemInfoQuery query)
          throws Exception {
    billDetailBiz.billItemStatisticsDetailIntegrationExport(query, response);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询产品使用报表
   *
   * @param query
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-产品使用报表")
  @PostMapping(value = "/coupon/executored/list", name = "根据条件查询产品使用报表")
  public ResponseResult<PageInfo<CouponExecutoredVO>> couponExecutoredList(
      @RequestBody @Validated CouponExecutoredQuery query) {
    PageInfo<CouponExecutoredVO> pageInfo = billDetailBiz.couponExecutoredList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件查询产品使用报表导出
   *
   * @param query
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-产品使用报表导出")
  @PostMapping(value = "/coupon/executored/export", name = "根据条件查询产品使用报表导出")
  public ResponseResult<T> couponExecutoredExport(
      HttpServletResponse response, @RequestBody @Validated CouponExecutoredQuery query)
      throws IOException {
    billDetailBiz.couponExecutoredExport(query, response);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询产品使用报表明细
   *
   * @param query
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-产品使用报表明细")
  @PostMapping(value = "/coupon/executored/detail", name = "根据条件查询产品使用报表明细")
  public ResponseResult<PageInfo<CouponExecutoredDetailVO>> couponExecutoredDetails(
      @RequestBody @Validated CouponExecutoredDetailQuery query) {
    PageInfo<CouponExecutoredDetailVO> pageInfo = billDetailBiz.couponExecutoredDetails(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 月工作量完成度导出
   *
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-运营BI-月工作量完成度")
  @PostMapping(value = "/workload/monthGoalCompleted", name = "月工作量完成度查询")
  public ResponseResult<DynamicHeaderPageInfo<JSONObject>> findWorkloadMonthGoalCompleted(@RequestBody @Validated DataStatisticsQuery query) {
    DynamicHeaderPageInfo<JSONObject> pageInfo = billDetailBiz.findWorkloadCompleted(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 月工作量完成度导出
   *
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-运营BI-月工作量完成度导出")
  @PostMapping(value = "/workload/monthGoalCompleted/export", name = "月工作量完成度导出")
  public ResponseResult<T> workloadMonthGoalCompletedExport(HttpServletResponse response)
      throws IOException {
    billDetailBiz.workloadMonthGoalCompletedExport(response);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询项目收费工作量
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-收费项目工作量统计")
  @PostMapping(value = "/tariff/pay/workload/list/single", name = "公司端报表-报表统计-运营报表-收费项目工作量统计")
  public ResponseResult<PageInfo<BillItemTollAndWorkloadVO>> tariffPaymentWorkloadStatistics(
      @RequestBody @Validated BillItemTollAndWorkloadQuery query) {
    PageInfo<BillItemTollAndWorkloadVO> pageInfo =
        billDetailBiz.findStatisticsTariffPaymentWorkloadList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出项目收费及工作量列表
   *
   * @param response http响应
   * @param query 查询参数
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-收费项目工作量统计-导出")
  @PostMapping(value = "/tariff/pay/workload/list/export/single", name = "导出项目收费金额及工作量")
  public ResponseResult<T> exportTariffPaymentWorkloadList(
      HttpServletResponse response, @RequestBody @Validated BillItemTollAndWorkloadQuery query)
      throws IOException {
    billDetailBiz.exportTariffPaymentWorkloadList(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询员工个人收费项目已收工作量明细列表
   *
   * @param query 查询条件
   * @return list
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-收费项目已收工作量-查看明细")
  @PostMapping(value = "/personal/tariff/received/workload/detail", name = "个人收费项目已收工作量明细")
  public ResponseResult<PageInfo<PersonalBillItemReceivedWorkloadDetailVO>>
      personalTariffReceivedWorkloadStatistics(
          @RequestBody @Validated PersonalBillItemTollAndWorkloadQuery query) {
    PageInfo<PersonalBillItemReceivedWorkloadDetailVO> pageInfo =
        billDetailBiz.findPersonalBillItemReceivedWorkloadDetailList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件查询员工个人收费项目已收工作量明细列表导出
   *
   * @param query 查询条件
   * @return list
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-收费项目已收工作量-导出")
  @PostMapping(value = "/personal/tariff/received/workload/export", name = "个人收费项目已收工作量明细导出")
  public ResponseResult<T> exportPersonalTariffReceivedWorkloadStatistics(
      HttpServletResponse response,
      @RequestBody @Validated PersonalBillItemTollAndWorkloadQuery query)
      throws IOException {
    billDetailBiz.exportPersonalBillItemReceivedWorkloadList(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询员工个人收费项目已收工作量明细列表
   *
   * @param query 查询条件
   * @return list
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-收费项目免单工作量-查看明细")
  @PostMapping(value = "/personal/tariff/free/workload/detail", name = "个人收费项目免单工作量明细")
  public ResponseResult<PageInfo<PersonalBillItemFreeWorkloadDetailVO>>
      personalTariffFreeWorkloadStatistics(
          @RequestBody @Validated PersonalBillItemTollAndWorkloadQuery query) {
    PageInfo<PersonalBillItemFreeWorkloadDetailVO> pageInfo =
        billDetailBiz.findPersonalBillItemFreeWorkloadDetailList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件查询员工个人收费项目已收工作量明细列表导出
   *
   * @param query 查询条件
   * @return list
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-收费项目免单工作量明细-导出")
  @PostMapping(value = "/personal/tariff/free/workload/export", name = "个人收费项目免单工作量明细导出")
  public ResponseResult<T> exportPersonalTariffFreeWorkloadStatistics(
      HttpServletResponse response,
      @RequestBody @Validated PersonalBillItemTollAndWorkloadQuery query)
      throws IOException {
    billDetailBiz.exportPersonalBillItemFreeWorkloadList(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询员工个人收费项目已收工作量明细列表
   *
   * @param query 查询条件
   * @return list
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-收费项目补入工作量-查看明细")
  @PostMapping(value = "/personal/tariff/supply/workload/detail", name = "个人收费项目补入工作量明细")
  public ResponseResult<PageInfo<PersonalBillItemSupplyWorkloadDetailVO>>
      personalTariffSupplyWorkloadStatistics(
          @RequestBody @Validated PersonalBillItemTollAndWorkloadQuery query) {
    PageInfo<PersonalBillItemSupplyWorkloadDetailVO> pageInfo =
        billDetailBiz.findPersonalBillItemSupplyWorkloadDetailList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件查询员工个人收费项目已收工作量明细列表导出
   *
   * @param query 查询条件
   * @return list
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-收费项目补入工作量明细-导出")
  @PostMapping(value = "/personal/tariff/supply/workload/export", name = "个人收费项目补入工作量明细导出")
  public ResponseResult<T> exportPersonalTariffSupplyWorkloadStatistics(
      HttpServletResponse response,
      @RequestBody @Validated PersonalBillItemTollAndWorkloadQuery query)
      throws IOException {
    billDetailBiz.exportPersonalBillItemSupplyWorkloadList(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询员工个人收费项目已收工作量明细列表
   *
   * @param query 查询条件
   * @return list
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-收费项目退费工作量-查看明细")
  @PostMapping(value = "/personal/tariff/refund/workload/detail", name = "个人收费项目退费工作量明细")
  public ResponseResult<PageInfo<PersonalBillItemRefundWorkloadDetailVO>>
      personalTariffRefundWorkloadStatistics(
          @RequestBody @Validated PersonalBillItemTollAndWorkloadQuery query) {
    PageInfo<PersonalBillItemRefundWorkloadDetailVO> pageInfo =
        billDetailBiz.findPersonalBillItemRefundWorkloadDetailList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件查询门诊业绩
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-门诊业绩&业务报表-门诊工作量统计")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message =
                "响应格式：{\"status\":0,\"msg\":\"success\",\"data\":{\"total\":11,\"list\":[{\"33\":20,\"orgId1\":10,\"name\":\"工作量\",\"date\":\"时间\"}],\"pageNum\":1,\"pageSize\":10,\"size\":10,\"startRow\":1,\"endRow\":10,\"pages\":2,\"prePage\":0,\"nextPage\":2,\"isFirstPage\":true,\"isLastPage\":false,\"hasPreviousPage\":false,\"hasNextPage\":true,\"navigatePages\":8,\"navigatepageNums\":[1,2],\"navigateFirstPage\":1,\"navigateLastPage\":2,\"header\":null,\"map\":{\"26\":\"古墩路门诊\",\"orgId1\":\"金沙大道门诊\",\"date\":\"时间\",\"name\":\"工作量\",\"total\":\"合计\"},\"lastPage\":2,\"firstPage\":1},\"audit\":true}")
      })
  @PostMapping(value = "/clinic/performance/list", name = "公司端报表-报表统计-运营报表-门诊业绩&业务报表-门诊工作量统计")
  public ResponseResult<DynamicHeaderPageInfo<JSONObject>> clinicPerformanceList(
      @RequestBody @Validated ClinicPerformanceBusinessQuery query) {
    DynamicHeaderPageInfo<JSONObject> pageInfo = billDetailBiz.clinicPerformanceList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出门诊业绩
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-门诊业绩&业务报表-门诊工作量统计导出")
  @PostMapping(value = "/clinic/performance/export", name = "公司端报表-报表统计-运营报表-门诊业绩&业务报表-门诊工作量统计导出")
  public ResponseResult<T> clinicPerformanceExport(
      HttpServletResponse response, @RequestBody @Validated ClinicPerformanceBusinessQuery query)
      throws IOException {
    billDetailBiz.clinicPerformanceExport(query, response);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询初诊来源数量分析
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-门诊业绩&业务报表-初诊来源数量分析")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message =
                "响应格式：{\"status\":0,\"msg\":\"success\",\"data\":{\"total\":11,\"list\":[{\"33\":0,\"orgId1\":0,\"originType\":\"患者来源\",\"date\":\"时间\"}],\"pageNum\":1,\"pageSize\":10,\"size\":10,\"startRow\":1,\"endRow\":10,\"pages\":2,\"prePage\":0,\"nextPage\":2,\"isFirstPage\":true,\"isLastPage\":false,\"hasPreviousPage\":false,\"hasNextPage\":true,\"navigatePages\":8,\"navigatepageNums\":[1,2],\"navigateFirstPage\":1,\"navigateLastPage\":2,\"header\":null,\"map\":{\"26\":\"古墩路门诊\",\"orgId1\":\"金沙大道门诊\",\"date\":\"时间\",\"name\":\"患者来源\",\"total\":\"合计\"},\"lastPage\":2,\"firstPage\":1},\"audit\":true}")
      })
  @PostMapping(value = "/clinic/firstVisitSource/list", name = "公司端报表-报表统计-运营报表-门诊业绩&业务报表-初诊来源数量分析")
  public ResponseResult<DynamicHeaderPageInfo<JSONObject>> clinicFirstVisitSourceList(
      @RequestBody @Validated ClinicPerformanceBusinessQuery query) {
    DynamicHeaderPageInfo<JSONObject> pageInfo = billDetailBiz.clinicFirstVisitSourceList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出初诊来源数量分析
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-门诊业绩&业务报表-初诊来源数量分析导出")
  @PostMapping(
      value = "/clinic/firstVisitSource/export",
      name = "公司端报表-报表统计-运营报表-门诊业绩&业务报表-初诊来源数量分析导出")
  public ResponseResult<T> clinicFirstVisitSourceExport(
      HttpServletResponse response, @RequestBody @Validated ClinicPerformanceBusinessQuery query)
      throws IOException {
    billDetailBiz.clinicFirstVisitSourceExport(query, response);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询门诊专科项目数量统计
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-门诊业绩&业务报表-门诊专科项目数量统计")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 200,
            message =
                "响应格式：{\"status\":0,\"msg\":\"success\",\"data\":{\"total\":11,\"list\":[{\"33\":20,\"orgId1\":10,\"name\":\"专科项目\",\"date\":\"时间\"}],\"pageNum\":1,\"pageSize\":10,\"size\":10,\"startRow\":1,\"endRow\":10,\"pages\":2,\"prePage\":0,\"nextPage\":2,\"isFirstPage\":true,\"isLastPage\":false,\"hasPreviousPage\":false,\"hasNextPage\":true,\"navigatePages\":8,\"navigatepageNums\":[1,2],\"navigateFirstPage\":1,\"navigateLastPage\":2,\"header\":null,\"map\":{\"26\":\"古墩路门诊\",\"orgId1\":\"金沙大道门诊\",\"date\":\"时间\",\"name\":\"专科项目\",\"total\":\"合计\"},\"lastPage\":2,\"firstPage\":1},\"audit\":true}")
      })
  @PostMapping(value = "/clinic/specialProject/list", name = "公司端报表-报表统计-运营报表-门诊业绩&业务报表-门诊专科项目数量统计")
  public ResponseResult<DynamicHeaderPageInfo<JSONObject>> clinicSpecialItemList(
      @RequestBody @Validated ClinicPerformanceBusinessQuery query) {
    DynamicHeaderPageInfo<JSONObject> pageInfo = billDetailBiz.clinicSpecialItemList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出门诊专科项目数量统计
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-门诊业绩&业务报表-门诊专科项目数量统计导出")
  @PostMapping(
      value = "/clinic/specialProject/export",
      name = "公司端报表-报表统计-运营报表-门诊业绩&业务报表-门诊专科项目数量统计导出")
  public ResponseResult<T> clinicSpecialItemExport(
      HttpServletResponse response, @RequestBody @Validated ClinicPerformanceBusinessQuery query)
      throws IOException {
    billDetailBiz.clinicSpecialItemExport(query, response);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询门诊365卡销售激活统计
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-门诊业绩&业务报表-门诊365卡销售激活统计")
  @PostMapping(
      value = "/clinic/365CardSaleActivited/list",
      name = "公司端报表-报表统计-运营报表-门诊业绩&业务报表-门诊365卡销售激活统计")
  public ResponseResult<PageInfo<SaleActivited365CardVO>> clinic365CardSaleActivitedList(
      @RequestBody @Validated ClinicPerformanceBusinessQuery query) {
    PageInfo<SaleActivited365CardVO> pageInfo = billDetailBiz.clinic365CardSaleActivitedList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出门诊365卡销售激活统计
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-门诊业绩&业务报表-门诊365卡销售激活统计导出")
  @PostMapping(
      value = "/clinic/365CardSaleActivited/export",
      name = "公司端报表-报表统计-运营报表-门诊业绩&业务报表-门诊365卡销售激活统计导出")
  public ResponseResult<T> clinic365CardSaleActivitedExport(
      HttpServletResponse response, @RequestBody @Validated ClinicPerformanceBusinessQuery query)
      throws IOException {
    billDetailBiz.clinic365CardSaleActivitedExport(query, response);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询员工个人收费项目已收工作量明细列表导出
   *
   * @param query 查询条件
   * @return list
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-收费项目退费工作量明细-导出")
  @PostMapping(value = "/personal/tariff/refund/workload/export", name = "个人收费项目退费工作量明细导出")
  public ResponseResult<T> exportPersonalTariffRefundWorkloadStatistics(
      HttpServletResponse response,
      @RequestBody @Validated PersonalBillItemTollAndWorkloadQuery query)
      throws IOException {
    billDetailBiz.exportPersonalBillItemRefundWorkloadList(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询个人开单项目应收明细表
   *
   * @param query 查询条件
   * @return PageInfo<BillItemStatisticsInfoVO>
   */
  @ApiOperation("公司端报表-报表统计-运营报表-个人开单项目应收明细表")
  @PostMapping(value = "/billItem/statistics/info", name = "根据条件查询个人开单项目应收明细表")
  public ResponseResult<PageInfo<BillItemStatisticsInfoVO>> billItemStatisticsInfo(
      @RequestBody @Validated BillItemInfoQuery query) {
    PageInfo<BillItemStatisticsInfoVO> pageInfo = billDetailBiz.billItemStatisticsInfo(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出个人开单项目应收明细表
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-个人开单项目应收明细表导出")
  @PostMapping(value = "/billItem/statistics/info/export", name = "根据条件查询个人开单项目应收明细表导出")
  public ResponseResult<T> billItemStatisticsInfoExport(
      HttpServletResponse response, @RequestBody @Validated BillItemInfoQuery query)
      throws IOException {
    billDetailBiz.billItemStatisticsInfoExport(query, response);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询个人开单项目实收明细表
   *
   * @param query 查询条件
   * @return PageInfo<BillItemStatisticsInfoVO>
   */
  @ApiOperation("公司端报表-报表统计-运营报表-个人开单项目实收明细表")
  @PostMapping(value = "/billItem/receivable/statistics", name = "根据条件查询个人开单项目实收明细表")
  public ResponseResult<PageInfo<BillItemReceivedStatisticsVO>> billItemReceivableStatistics(
          @RequestBody @Validated BillItemInfoQuery query) {
    PageInfo<BillItemReceivedStatisticsVO> pageInfo = billDetailBiz.billItemReceivedStatistics(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出个人开单项目实收明细表
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-个人开单项目实收明细表")
  @PostMapping(value = "/billItem/receivable/statistics/export", name = "根据条件导出个人开单项目实收明细表")
  public ResponseResult<T> billItemReceivableStatisticsInfoExport(
          HttpServletResponse response, @RequestBody @Validated BillItemInfoQuery query)
          throws IOException {
    billDetailBiz.billItemReceivedStatisticsExport(query, response);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询365卡产品售出激活统计表
   *
   * @param query 查询条件
   * @return PageInfo<Coupon365SoldActivedStatisticsVO>
   */
  @ApiOperation("公司端报表-报表统计-运营报表-365卡产品售出激活统计表")
  @PostMapping(value = "/coupon365/soldActived/statistics", name = "365卡产品售出激活统计表")
  public ResponseResult<PageInfo<Coupon365SoldActivedStatisticsVO>> coupon365SoldActivedStatistics(
          @RequestBody @Validated Coupon365SoldActivedStatisticsQuery query) {
    PageInfo<Coupon365SoldActivedStatisticsVO> pageInfo = baseCardBiz.coupon365SoldActivedStatistics(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出365卡产品售出激活统计表
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-365卡产品售出激活统计表导出")
  @PostMapping(value = "/coupon365/soldActived/statistics/export", name = "根据条件导出365卡产品售出激活统计表")
  public ResponseResult coupon365SoldActivedStatisticsExport(
          HttpServletResponse response, @RequestBody @Validated Coupon365SoldActivedStatisticsQuery query)
          throws IOException {
    baseCardBiz.coupon365SoldActivedStatisticsExport(query, response);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询365卡产品售出明细表
   *
   * @param query 查询条件
   * @return PageInfo<Coupon365SoldDetailVO>
   */
  @ApiOperation("公司端报表-报表统计-运营报表-365卡产品售出明细表")
  @PostMapping(value = "/coupon365/sold/detail", name = "365卡产品售出明细表")
  public ResponseResult<PageInfo<Coupon365SoldDetailVO>> findCoupon365SoldDetail(
          @RequestBody @Validated Coupon365SoldDetailQuery query) {
    PageInfo<Coupon365SoldDetailVO> pageInfo = baseCardBiz.findCoupon365SoldDetail(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出365卡产品售出明细表
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-365卡产品售出明细表导出")
  @PostMapping(value = "/coupon365/sold/detail/export", name = "根据条件导出365卡产品售出明细表")
  public ResponseResult findCoupon365SoldDetailExport(
          HttpServletResponse response, @RequestBody @Validated Coupon365SoldDetailQuery query)
          throws IOException {
    baseCardBiz.findCoupon365SoldDetailExport(query, response);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询365卡产品激活明细表
   *
   * @param query 查询条件
   * @return PageInfo<Coupon365SoldDetailVO>
   */
  @ApiOperation("公司端报表-报表统计-运营报表-365卡产品激活明细表")
  @PostMapping(value = "/coupon365/actived/detail", name = "365卡产品激活明细表")
  public ResponseResult<PageInfo<Coupon365ActivedDetailVO>> findCoupon365ActivedDetail(
          @RequestBody @Validated Coupon365ActivedDetailQuery query) {
    PageInfo<Coupon365ActivedDetailVO> pageInfo = baseCardBiz.findCoupon365ActivedDetail(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出365卡产品激活明细表
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-365卡产品激活明细表导出")
  @PostMapping(value = "/coupon365/actived/detail/export", name = "根据条件导出365卡产品激活明细表")
  public ResponseResult findCoupon365ActivedDetailExport(
          HttpServletResponse response, @RequestBody @Validated Coupon365ActivedDetailQuery query)
          throws IOException {
    baseCardBiz.findCoupon365ActivedDetailExport(query, response);
    return ResponseUtil.success(null);
  }
}
