package com.yunya.report.ultimate.controller;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.report.ultimate.biz.BaseBillBiz;
import com.yunya.report.ultimate.biz.BaseBillDetailBiz;
import com.yunya.report.ultimate.service.DiscountBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

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
   * @return
   */
  @ApiOperation("公司端报表-财务报表-导出项目收入明细列表")
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
   * @return
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
   * @return
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
   * @return
   */
  @ApiOperation("公司端报表-财务报表-账单优惠明细")
  @PostMapping(value = "/bill/privilege/list", name = "根据条件查询账单优惠明细")
  public ResponseResult<PageInfo<BillOfDiscountDetailVO>> billDiscountDetailList(
      @RequestBody @Validated BillOfDiscountDetailQuery query) {
    PageInfo<BillOfDiscountDetailVO> resultList = baseBillBiz.findBillDiscountDetailList(query);
    return ResponseUtil.success(resultList);
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
}
