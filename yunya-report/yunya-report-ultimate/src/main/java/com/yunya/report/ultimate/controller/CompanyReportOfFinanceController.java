package com.yunya.report.ultimate.controller;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.report.ultimate.biz.BaseAccountItemBiz;
import com.yunya.report.ultimate.biz.BaseBillBiz;
import com.yunya.report.ultimate.biz.BaseBillDetailBiz;
import com.yunya.report.ultimate.service.DiscountBiz;
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
  /** 卡券 */
  @Autowired private DiscountBiz discountBiz;
  /** 支付方式 */
  @Autowired private BaseAccountItemBiz accountItemBiz;

  @ApiOperation(value = "公司端报表-财务报表-产品售出统计-产品维度")
  @PostMapping("/coupon/sold/statistics")
  public ResponseResult<PageInfo<CouponSoldStatisticsVo>> getCouponSold(
      @Valid @RequestBody CouponSoldStatisticsQuery query) {
    return ResponseUtil.success(discountBiz.getCouponSoldPage(query));
  }

  @ApiOperation(value = "公司端报表-财务报表-产品售出统计-时间维度")
  @PostMapping("/card/sold/statistics")
  public ResponseResult<PageInfo<CardSoldStatisticsVo>> getCardSold(
      @Valid @RequestBody CardSoldStatisticsQuery query) {
    return ResponseUtil.success(discountBiz.getCardSoldPage(query));
  }

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

  @ApiOperation(value = "公司端报表-财务报表-产品使用统计-产品维度")
  @PostMapping("/coupon/used/statistics")
  public ResponseResult<PageInfo<CouponUsedVo>> getCouponUsed(
      @Valid @RequestBody CouponUsedQuery query) {
    return ResponseUtil.success(discountBiz.getCouponUsedPage(query));
  }

  @ApiOperation(value = "公司端报表-财务报表-产品使用统计-时间维度")
  @PostMapping("/card/used/statistics")
  public ResponseResult<PageInfo<CardUsedStatisticsVo>> getCouponUsed(
      @Valid @RequestBody CardUsedStatisticsQuery query) {
    return ResponseUtil.success(discountBiz.getCardUsedPage(query));
  }

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

  @ApiOperation(value = "公司端报表-财务报表-充值卡充值统计")
  @PostMapping("/recharge/statistics")
  public ResponseResult<PageInfo<RechargeVo>> getRechargePage(
      @Valid @RequestBody RechargeQuery query) {
    return ResponseUtil.success(discountBiz.getRechargePage(query));
  }

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
    @ApiImplicitParam(name = "billId", value = "账单ID", dataTypeClass = Integer.class),
    @ApiImplicitParam(
        name = "pageSize",
        value = "页大小",
        dataTypeClass = Integer.class,
        defaultValue = "10"),
    @ApiImplicitParam(
        name = "pageNum",
        value = "页码",
        dataTypeClass = Integer.class,
        defaultValue = "1"),
    @ApiImplicitParam(
        name = "whetherPage",
        value = "是否分页",
        dataTypeClass = Boolean.class,
        defaultValue = "true"),
  })
  public ResponseResult<PageInfo<BillDiscountDetailInifoVO>> billDiscountDetailInfo(
      @PathVariable("billId") @NotNull(message = "账单ID不能为空") Integer billId,
      @RequestParam("pageNum") Integer pageNum,
      @RequestParam("pageSize") Integer pageSize,
      @RequestParam("whetherPage") Boolean whetherPage) {
    PageInfo<BillDiscountDetailInifoVO> resultPageInfo =
        baseBillBiz.billDiscountDetailInfo(billId, pageNum, pageSize, whetherPage);
    return ResponseUtil.success(resultPageInfo);
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
}
