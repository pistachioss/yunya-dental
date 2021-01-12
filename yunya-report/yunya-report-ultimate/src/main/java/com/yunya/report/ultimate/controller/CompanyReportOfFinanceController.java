package com.yunya.report.ultimate.controller;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.report.ultimate.biz.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

/**
 * 简介: 公司端报表-财务报表控制层
 *
 * @author: chow
 * @date: 2020/10/29 11:04
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "公司端报表-财务报表")
@RestController
@RequestMapping("finance")
public class CompanyReportOfFinanceController {

  /** 账单 */
  @Autowired private BaseBillBiz baseBillBiz;
  /** 账单详情 */
  @Autowired private BaseBillDetailBiz billDetailBiz;
  /** 折扣 */
  @Autowired private DiscountBiz discountBiz;
  /** 支付方式 */
  @Autowired private BaseAccountItemBiz accountItemBiz;
  /** 支付记录 */
  @Autowired private BaseBillPayBiz billPayBiz;
  /** 患者储值卡（会员卡或预付卡）充值记录 */
  @Autowired private MemberOccurLogBiz patientMemberOccurLogBiz;
  /** 卡券基础信息 */
  @Autowired private BaseCardBiz baseCardBiz;

  /**
   * 公司端报表-财务报表-产品售出统计-产品维度
   *
   * @param query
   * @return
   */
  @ApiOperation(value = "公司端报表-财务报表-产品售出统计-产品维度")
  @PostMapping("/coupon/sold/statistics")
  public ResponseResult<PageInfo<CouponSoldStatisticsVo>> getCouponSold(
      @Valid @RequestBody CouponSoldStatisticsQuery query) {
    return ResponseUtil.success(discountBiz.getCouponSoldPage(query));
  }

  /**
   * 公司端报表-财务报表-产品售出统计-时间维度
   *
   * @param query
   * @return
   */
  @ApiOperation(value = "公司端报表-财务报表-产品售出统计-时间维度")
  @PostMapping("/card/sold/statistics")
  public ResponseResult<PageInfo<CardSoldStatisticsVo>> getCardSold(
      @Valid @RequestBody CardSoldStatisticsQuery query) {
    return ResponseUtil.success(discountBiz.getCardSoldPage(query));
  }

  /**
   * 公司端报表-财务报表-产品记录-产品售出记录 - 导出
   *
   * @param response
   * @param query
   * @throws IOException
   */
  @ApiOperation(value = "公司端报表-财务报表-产品记录-产品售出记录 - 导出")
  @PostMapping("/coupon/sold/record/export")
  public void exportCouponSold(
      HttpServletResponse response, @Valid @RequestBody CardSoldRecordQuery query)
      throws IOException {
    discountBiz.buildResponse(response, discountBiz.getOrgName(query.getOrgId()) + "产品售出记录表");
    EasyExcel.write(response.getOutputStream(), RechargeCardStatisticsVo.class)
        .sheet("sheet")
        .doWrite(discountBiz.getCardSoldRecordList(query));
  }

  /**
   * 公司端报表-财务报表-产品使用统计-产品维度
   *
   * @param query
   * @return
   */
  @ApiOperation(value = "公司端报表-财务报表-产品使用统计-产品维度")
  @PostMapping("/coupon/used/statistics")
  public ResponseResult<PageInfo<CouponUsedVo>> getCouponUsed(
      @Valid @RequestBody CouponUsedQuery query) {
    return ResponseUtil.success(discountBiz.getCouponUsedPage(query));
  }

  /**
   * 公司端报表-财务报表-产品使用统计-时间维度
   *
   * @param query
   * @return
   */
  @ApiOperation(value = "公司端报表-财务报表-产品使用统计-时间维度")
  @PostMapping("/card/used/statistics")
  public ResponseResult<PageInfo<CardUsedStatisticsVo>> getCouponUsed(
      @Valid @RequestBody CardUsedStatisticsQuery query) {
    return ResponseUtil.success(discountBiz.getCardUsedPage(query));
  }

  /**
   * 公司端报表-财务报表-产品记录-产品使用记录 - 导出
   *
   * @param response
   * @param query
   * @throws IOException
   */
  @ApiOperation(value = "公司端报表-财务报表-产品记录-产品使用记录 - 导出")
  @PostMapping("/coupon/used/record/export")
  public void exportCouponUsedRecord(
      HttpServletResponse response, @Valid @RequestBody CardUsedRecordQuery query)
      throws IOException {
    discountBiz.buildResponse(response, discountBiz.getOrgName(query.getOrgId()) + "产品使用记录表");
    EasyExcel.write(response.getOutputStream(), CardSoldStatisticsVo.class)
        .sheet("sheet")
        .doWrite(discountBiz.getCardUsedRecordList(query));
  }

  /**
   * 公司端报表-财务报表-充值卡充值统计
   *
   * @param query
   * @return
   */
  @ApiOperation(value = "公司端报表-财务报表-充值卡充值统计")
  @PostMapping("/recharge/statistics")
  public ResponseResult<PageInfo<RechargeVo>> getRechargePage(
      @Valid @RequestBody RechargeQuery query) {
    return ResponseUtil.success(discountBiz.getRechargePage(query));
  }

  /**
   * 公司端报表-财务报表-充值卡充值统计-充值统计
   *
   * @param couponId
   * @param query
   * @return
   */
  @ApiOperation(value = "公司端报表-财务报表-充值卡充值统计-充值统计")
  @PostMapping("/{couponId}/rechargeCard/statistics")
  public ResponseResult<PageInfo<RechargeDetailVo>> getRechargePage(
      @PathVariable(value = "couponId") Integer couponId,
      @Valid @RequestBody RechargeDetailQuery query) {
    return ResponseUtil.success(discountBiz.getRechargeDetailPage(couponId, query));
  }

  /**
   * 根据条件查询项目收入明细列表
   *
   * @param query 查询条件
   * @return list
   */
  @ApiOperation("公司端报表-财务报表-项目收入明细")
  @PostMapping(value = "/tariff/income/list", name = "根据条件查询项目收入明细")
  public ResponseResult<PageInfo<BillTariffIncomeDetailVO>> billDetailIncomeDetail(
      @RequestBody @Validated BillDetailIncomeDetailQuery query) {
    PageInfo<BillTariffIncomeDetailVO> resultList = billDetailBiz.findBillDetailIncomeList(query);
    return ResponseUtil.success(resultList);
  }

  /**
   * 导出项目收入明细列表
   *
   * @param response 响应
   * @param query 查询条件
   * @return void
   */
  @ApiOperation("公司端报表-财务报表-项目收入明细列表-导出")
  @PostMapping(value = "/tariff/income/export", name = "导出项目收入明细列表")
  public ResponseResult<T> exportBillDetailIncome(
      HttpServletResponse response, @RequestBody @Validated BillDetailIncomeDetailQuery query)
      throws IOException {
    billDetailBiz.exportBillDetailIncome(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询项目分类收入汇总列表
   *
   * @param query 查询条件
   * @return PageInfo<CategoryInfoIncomeVO>
   */
  @ApiOperation("公司端报表-财务报表-分类收入汇总")
  @PostMapping(value = "/category/income/list", name = "billDetailBiz")
  public ResponseResult<PageInfo<CategoryInfoIncomeVO>> categoryIncomeList(
      @RequestBody @Validated BillCategoryIncomeQuery query) {
    PageInfo<CategoryInfoIncomeVO> resultList = billDetailBiz.findCategoryIncomeList(query);
    return ResponseUtil.success(resultList);
  }

  /**
   * 根据条件导出项目分类收入汇总列表
   *
   * @param response 响应
   * @param query 查询条件
   * @return void
   */
  @ApiOperation("公司端报表-财务报表-分类收入汇总-导出")
  @PostMapping(value = "/category/income/export", name = "根据条件导出项目分类收入汇总列表")
  public ResponseResult<T> exportCategoryIncome(
      HttpServletResponse response, @RequestBody @Validated BillCategoryIncomeQuery query)
      throws IOException {
    billDetailBiz.exportCategoryIncome(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询账单优惠明细列表
   *
   * @param query 查询条件
   * @return PageInfo<BillOfDiscountDetailVO>
   */
  @ApiOperation("公司端报表-财务报表-账单优惠明细")
  @PostMapping(value = "/bill/privilege/list", name = "根据条件查询账单优惠明细")
  public ResponseResult<PageInfo<BillOfDiscountDetailVO>> billDiscountDetailList(
      @RequestBody @Validated BillOfDiscountDetailQuery query) {
    PageInfo<BillOfDiscountDetailVO> resultList = baseBillBiz.findBillDiscountDetailList(query);
    return ResponseUtil.success(resultList);
  }

  /**
   * 公司端报表-财务报表-账单优惠明细-查看明细
   *
   * @param billId 根据账单ID查询优惠明细
   * @return PageInfo<BillDiscountDetailInifoVO>
   */
  @ApiOperation("公司端报表-财务报表-账单优惠明细-查看明细")
  @GetMapping("/bill/privilege/info/{billId}")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "billId", value = "账单ID", dataTypeClass = Integer.class)
  })
  public ResponseResult<BillDiscountVO> billDiscountDetailInfo(
      @PathVariable("billId") @NotNull(message = "账单ID不能为空") Integer billId) {
    BillDiscountVO result = baseBillBiz.billDiscountDetailInfo(billId);
    return ResponseUtil.success(result);
  }

  /**
   * 根据条件导出账单优惠明细列表
   *
   * @param response 响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-财务报表-账单优惠明细-导出")
  @PostMapping(value = "/bill/privilege/export", name = "根据条件导出账单优惠明细列表")
  public ResponseResult<T> exportDiscountDetailList(
      HttpServletResponse response, @RequestBody @Validated BillOfDiscountDetailQuery query)
      throws IOException {
    baseBillBiz.exportDiscountDetailList(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询应收账款余额表
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-财务报表-应收账款余额表")
  @PostMapping(value = "/bill/receivable/list", name = "根据条件查询应收账款余额表")
  public ResponseResult<PageInfo<BillRestReceivableAmountVO>> billReceivableAmount(
      @RequestBody @Validated BillOfReceivableQuery query) {
    PageInfo<BillRestReceivableAmountVO> resultList = baseBillBiz.findBillReceivableAmount(query);
    return ResponseUtil.success(resultList);
  }

  /**
   * 根据条件导出应收账款余额表
   *
   * @param response 响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-财务报表-应收账款余额表-导出")
  @PostMapping(value = "/bill/receivable/export", name = "根据条件导出应收账款余额表")
  public ResponseResult<T> exportBillReceivableAmountList(
      HttpServletResponse response, @RequestBody @Validated BillOfReceivableQuery query)
      throws IOException {
    baseBillBiz.exportBillReceivableAmountList(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 获取全部支付方式表头
   *
   * @return List<BaseAccountItemVO>
   */
  @ApiOperation("公司端报表-财务报表-支付方式表头")
  @GetMapping(value = "/statement/payment/list", name = "获取全部支付方式表头")
  public ResponseResult<List<BaseAccountItemVO>> findAllPaymentList() {
    List<BaseAccountItemVO> resultList = accountItemBiz.findAllPaymentList();
    return ResponseUtil.success(resultList);
  }

  /**
   * 根据条件查询门诊出入账对账单
   *
   * @param query 查询条件
   * @return Map<String, Object>
   */
  @ApiOperation("公司端报表-财务报表-对账单")
  @PostMapping(value = "/statement", name = "根据条件查询门诊出入账对账单")
  public ResponseResult<List<ClinicInboundAndOutboundVO>> inboundAndOutboundStatement(
      @RequestBody @Validated InboundAndOutboundStatementQuery query) {
    List<ClinicInboundAndOutboundVO> resultList =
        accountItemBiz.findInboundAndOutboundStatement(query);
    return ResponseUtil.success(resultList);
  }

  /**
   * 根据条件查询门诊账单收费明细（首次收费）
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-财务报表-对账单-账单收费明细（本月）-查询明细")
  @PostMapping(value = "/bill/charge/detail/list", name = "根据条件查询门诊账单收费明细（首次收费）")
  public ResponseResult<PageInfo<StatementBillChargeDetailVO>> billChargeDetailInfoList(
      @RequestBody @Validated StatementBillChargeDetailInfoQuery query) {
    PageInfo<StatementBillChargeDetailVO> pageInfo =
        billPayBiz.selectBillChargeDetailInfoList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件查询导出本月账单收费明细 todo：导出未完成
   *
   * @param response
   * @param query
   * @return
   */
  @ApiOperation("公司端报表-财务报表-对账单-账单收费明细（本月）-账单收费明细列表-导出")
  @PostMapping(value = "/bill/charge/detail/export", name = "根据条件查询门诊账单收费明细（首次收费）")
  public ResponseResult<T> exportBillChargeDetailInfoList(
      HttpServletResponse response,
      @RequestBody @Validated StatementBillChargeDetailInfoQuery query) {

    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询门诊账单收欠费明细（本月账单）todo：完善收费记录的支付方式列表
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-财务报表-对账单-账单收欠费(本月)-查询明细")
  @PostMapping(value = "/bill/current/debt/detail/list", name = "公司端报表-财务报表-对账单-账单收欠费")
  public ResponseResult<PageInfo<StatementBillChargeDetailVO>> billCurrentCollectDebtDetailList(
      @RequestBody @Validated StatementBillChargeDetailInfoQuery query) {
    PageInfo<StatementBillChargeDetailVO> pageInfo =
        billPayBiz.findBillCurrentCollectDebtDetailList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件查询导出账单收欠费明细（本月账单本月收欠费） todo：导出未完成
   *
   * @param response
   * @param query
   * @return
   */
  @ApiOperation("公司端报表-财务报表-对账单-账单收欠费明细（本月）-账单收欠（本月）费明细列表-导出")
  @PostMapping(value = "/bill/current/debt/detail/export", name = "根据条件查询门诊账单收费明细（非首次收费）")
  public ResponseResult<T> exportBillCollectDebtDetailList(
      HttpServletResponse response,
      @RequestBody @Validated StatementBillChargeDetailInfoQuery query) {

    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询门诊账单收欠费明细（本月账单）todo：完善收费记录的支付方式列表
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-财务报表-对账单-账单收欠费(非本月)-查询明细")
  @PostMapping(value = "/bill/other/debt/detail/list", name = "公司端报表-财务报表-对账单-账单收欠费")
  public ResponseResult<PageInfo<StatementBillChargeDetailVO>> billOtherCollectDebtDetailList(
      @RequestBody @Validated StatementBillChargeDetailInfoQuery query) {
    PageInfo<StatementBillChargeDetailVO> pageInfo =
        billPayBiz.findOtherCollectDebtDetailList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件查询导出账单收欠费明细（非本月账单本月收欠费） todo：导出未完成
   *
   * @param response
   * @param query
   * @return
   */
  @ApiOperation("公司端报表-财务报表-对账单-账单收欠费明细（非本月）-账单收欠费（非本月）明细列表-导出")
  @PostMapping(value = "/bill/other/debt/detail/export", name = "根据条件查询门诊账单收费明细（非首次收费）")
  public ResponseResult<T> exportBillOtherCollectDebtDetailList(
      HttpServletResponse response,
      @RequestBody @Validated StatementBillChargeDetailInfoQuery query) {

    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询门诊会员卡充值明细列表
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-财务报表-对账单-会员卡充值-查询明细")
  @PostMapping(value = "/member/recharge/detail/list", name = "根据条件查询门诊会员卡充值明细列表")
  public ResponseResult<PageInfo<StatementPatientCardRechargeDetailVO>> memberRechargeDetailList(
      @RequestBody @Validated StatementPatientCardRechargeDetailInfoQuery query) {
    PageInfo<StatementPatientCardRechargeDetailVO> pageInfo =
        patientMemberOccurLogBiz.findPatientCardRechargeDetailList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出会员充值记录明细
   *
   * @param response 响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-财务报表-对账单-会员充值-会员充值记录明细列表-导出")
  @PostMapping(value = "/member/recharge/detail/export", name = "根据条件查询导出会员充值记录明细")
  public ResponseResult<T> exportMemberRechargeDetailList(
      HttpServletResponse response,
      @RequestBody @Validated StatementPatientCardRechargeDetailInfoQuery query)
      throws IOException {
    patientMemberOccurLogBiz.exportMemberRechargeDetailList(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询门诊产品售出明细列表
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-财务报表-对账单-产品售出-查看明细")
  @PostMapping(value = "/product/sold/detail/list", name = "公司端报表-财务报表-对账单-产品售出-查看明细")
  public ResponseResult<PageInfo<StatementProductSoldDetailVO>> productSoldDetailList(
      @RequestBody @Validated StatementProductSoldDetailQuery query) {
    PageInfo<StatementProductSoldDetailVO> pageInfo = baseCardBiz.findProductSoldDetailList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出产品售出记录明细
   *
   * @param response 响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-财务报表-对账单-产品售出-导出")
  @PostMapping(value = "/product/sold/detail/export", name = "根据条件导出产品售出记录明细")
  public ResponseResult<T> exportProductSoldDetailList(
      HttpServletResponse response, @RequestBody @Validated StatementProductSoldDetailQuery query)
      throws IOException {
    baseCardBiz.exportProductSoldDetailList(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询诊所代收(本月)明细列表
   *
   * @param query 查询条件
   * @return PageInfo<StatementBillChargeDetailVO>
   */
  @ApiOperation("公司端报表-财务报表-对账单-诊所代收(本月)-查询明细")
  @PostMapping(value = "/bill/current/collection/detail/list", name = "根据条件查询诊所代收(本月)明细列表")
  public ResponseResult<PageInfo<StatementBillChargeDetailVO>> currentBillCollectionDetailList(
      @RequestBody @Validated StatementBillChargeDetailInfoQuery query) {
    PageInfo<StatementBillChargeDetailVO> pageInfo =
        billPayBiz.findCurrentBillCollectionDetailList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出诊所代收(本月)记录明细
   *
   * @param response 响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-财务报表-对账单-诊所代收-导出")
  @PostMapping(value = "/bill/current/collection/detail/export", name = "根据条件导出产品售出记录明细")
  public ResponseResult<T> exportCurrentBillCollectionDetailList(
      HttpServletResponse response,
      @RequestBody @Validated StatementBillChargeDetailInfoQuery query)
      throws IOException {
    billPayBiz.exportCurrentBillCollectionDetailList(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询诊所代非本月收明细列表
   *
   * @param query 查询条件
   * @return PageInfo<StatementBillChargeDetailVO>
   */
  @ApiOperation("公司端报表-财务报表-对账单-诊所代收(非本月)-查询明细")
  @PostMapping(value = "/bill/other/collection/detail/list", name = "公司端报表-财务报表-对账单-诊所代收(非本月)-查询明细")
  public ResponseResult<PageInfo<StatementBillChargeDetailVO>> otherBillCollectionDetailList(
      @RequestBody @Validated StatementBillChargeDetailInfoQuery query) {
    PageInfo<StatementBillChargeDetailVO> pageInfo =
        billPayBiz.findOtherBillCollectionDetailList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出诊所代收(本月)记录明细
   *
   * @param response 响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-财务报表-对账单-诊所代收(非本月)-导出")
  @PostMapping(value = "/bill/other/collection/detail/export", name = "根据条件导出产品售出记录明细")
  public ResponseResult<T> exportOtherBillCollectionDetailList(
      HttpServletResponse response,
      @RequestBody @Validated StatementBillChargeDetailInfoQuery query)
      throws IOException {
    billPayBiz.exportOtherBillCollectionDetailList(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询本月对账单账单收支统计信息
   *
   * @param query 查询条件
   * @return StatementBillIncomeStatisticVO
   */
  @Deprecated
  @ApiOperation("公司端报表-财务报表-对账单-本月账单收支统计")
  @PostMapping(value = "/statement/statistic", name = "公司端报表-财务报表-对账单-月收支统计")
  public ResponseResult<StatementBillIncomeStatisticVO> statementStatistic(
      @RequestBody @Validated StatementStatisticQuery query) {
    StatementBillIncomeStatisticVO resultData = baseBillBiz.findStatementStatistic(query);
    return ResponseUtil.success(resultData);
  }

  /**
   * 根据条件查询本月对账单账单统计信息
   *
   * @param query 查询条件
   * @return StatementBillIncomeStatisticVO
   */
  @ApiOperation("公司端报表-财务报表-对账单-本月账单统计")
  @PostMapping(value = "/bill/statistic", name = "公司端报表-财务报表-对账单-月账单统计")
  public ResponseResult<CurrentMonthBillStatisticVO> currentMonthStatementStatistic(
      @RequestBody @Validated StatementStatisticQuery query) {
    CurrentMonthBillStatisticVO resultData = baseBillBiz.findCurrentMonthStatementStatistic(query);
    return ResponseUtil.success(resultData);
  }

  /**
   * 根据条件导出门诊当月账单明细
   *
   * @param response http响应
   * @param query 查询条件
   * @return
   * @throws IOException
   */
  @ApiOperation("公司端报表-财务报表-对账单-本月账单明细-导出")
  @PostMapping(value = "/bill/detail/export", name = "公司端报表-财务报表-对账单-本月账单明细")
  public ResponseResult<T> exportCurrentMonthBillDetail(
      HttpServletResponse response, @RequestBody @Validated CurrentMonthBillInfoQuery query)
      throws IOException {
    billDetailBiz.exportCurrentMonthBillDetail(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件导出门诊当月账单收欠费（使用优惠列表）
   *
   * @param response http响应
   * @param query 查询条件
   * @return
   * @throws IOException
   */
  @ApiOperation("公司端报表-财务报表-对账单-本月账单收欠费（使用优惠）-导出")
  @PostMapping(value = "/bill/collete/debt/export", name = "根据条件导出门诊当月账单收欠费（使用优惠列表）")
  public ResponseResult<T> exportBillCollectionDebt(
      HttpServletResponse response, @RequestBody @Validated CurrentMonthBillInfoQuery query)
      throws IOException {
    baseBillBiz.exportBillCollectionDebt(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件导出门诊当月收费明细
   *
   * @param response http响应
   * @param query 查询条件
   * @return
   * @throws IOException
   */
  @ApiOperation("公司端报表-财务报表-对账单-本月收费明细-导出")
  @PostMapping(value = "/bill/pay/export", name = "公司端报表-财务报表-对账单-本月账单收费明细-导出")
  public ResponseResult<T> billPayRecordExport(
      HttpServletResponse response, @RequestBody @Validated CurrentMonthBillInfoQuery query)
      throws IOException {
    billDetailBiz.exportCurrentMonthBillPayRecord(response, query);
    return ResponseUtil.success(null);
  }
}
