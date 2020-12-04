package com.yunya.report.ultimate.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.report.ultimate.biz.BaseBillBiz;
import com.yunya.report.ultimate.biz.BaseBillPayBiz;
import com.yunya.report.ultimate.biz.BaseRefundBiz;
import com.yunya.report.ultimate.biz.BaseTreatmentProcessBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 简介: 公司端报表-数据统计控制器
 *
 * @author: chow
 * @date: 2020/10/26 14:03
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "公司端报表-报表统计-数据记录")
@RestController
@RequestMapping("record")
public class CompanyReportOfDataRecordController {

  /** 就诊流程 */
  @Autowired private BaseTreatmentProcessBiz treatmentProcessBiz;
  /** 账单 */
  @Autowired private BaseBillBiz billBiz;
  /** 账单收费 */
  @Autowired private BaseBillPayBiz billPayBiz;
  /** 账单退费 */
  @Autowired private BaseRefundBiz baseRefundBiz;

  /**
   * 根据条件查询就诊记录列表
   *
   * @param query 查询条件
   * @return list
   */
  @ApiOperation("数据记录-就诊记录-根据条件查询就诊记录列表")
  @PostMapping(value = "/treatment/list", name = "根据条件查询就诊记录")
  public ResponseResult<PageInfo<TreatmentRecordReportVO>> findTreatmentList(
      @RequestBody TreatmentRecordQuery query) {
    PageInfo<TreatmentRecordReportVO> resultList = treatmentProcessBiz.findTreatmentList(query);
    return ResponseUtil.success(resultList);
  }

  /**
   * 根据条件查询就诊记录列表并导出Excel
   *
   * @param response 响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("数据记录-就诊记录-根据条件查询就诊列表并导出Excel")
  @PostMapping(value = "/treatment/export", name = "根据条件查询就诊列表并导出Excel")
  public ResponseResult<T> exportTreatmentList(
      HttpServletResponse response, @RequestBody @Validated TreatmentRecordQuery query)
      throws IOException {
    treatmentProcessBiz.exportTreatmentList(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询开单记录列表
   *
   * @param query 查询条件
   * @return list
   */
  @ApiOperation("根据条件查询开单记录列表")
  @PostMapping(value = "/bill/order/list", name = "数据记录-账单记录-开单记录列表")
  public ResponseResult<PageInfo<BillOfOrderRecordVO>> findBillRecordOfOrderList(
      @RequestBody @Validated OrderRecordQuery query) {
    PageInfo<BillOfOrderRecordVO> resultList = billBiz.findBillRecordOfOrderList(query);
    return ResponseUtil.success(resultList);
  }

  /**
   * 根据条件导出开单记录列表
   *
   * @param response 响应
   * @param query 查询条件
   * @return list
   */
  @ApiOperation("根据条件导出:数据记录-账单记录-开单记录列表")
  @PostMapping(value = "/bill/order/export", name = "导出数据记录-账单记录-开单记录列表")
  public ResponseResult<T> exportBillOfOrderRecord(
      HttpServletResponse response, @RequestBody @Validated OrderRecordQuery query)
      throws IOException {
    billBiz.exportBillOfOrderRecord(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询账单收费记录
   *
   * @param query 查询条件
   * @return list
   */
  @ApiOperation("根据条件查询账单收费记录")
  @PostMapping(value = "/bill/pay/list", name = "数据记录-收费记录-根据条件查询账单收费记录")
  public ResponseResult<PageInfo<BillOfPayRecordVO>> findBillRecordOfPayList(
      @RequestBody @Validated BillPayRecordQuery query) {
    PageInfo<BillOfPayRecordVO> resultList = billPayBiz.findBillRecordOfPayList(query);
    return ResponseUtil.success(resultList);
  }

  /**
   * 根据条件导出账单收费记录
   *
   * @param response 响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("根据条件导出:数据记录-账单记录-收费记录")
  @PostMapping(value = "/bill/pay/export", name = "根据条件导出账单收费记录")
  public ResponseResult<T> exportBillOfPayRecord(
      HttpServletResponse response, @RequestBody @Validated BillPayRecordQuery query)
      throws IOException {
    billPayBiz.exportBillOfPayRecord(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询账单退费记录
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("数据记录-账单记录-账单退费记录")
  @PostMapping(value = "/bill/refund/list", name = "数据记录-账单记录-账单退费记录")
  public ResponseResult<PageInfo<BillOfRefundRecordVO>> findBillRefundRecordList(
      @RequestBody @Validated BillRefundRecordQuery query) {
    PageInfo<BillOfRefundRecordVO> resultList = baseRefundBiz.findBillRefundRecord(query);
    return ResponseUtil.success(resultList);
  }

  /**
   * 根据条件导出账单退费记录-数据记录-账单记录-账单退费记录
   *
   * @param response http响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("根据条件导出账单退费记录-数据记录-账单记录-账单退费记录")
  @PostMapping(value = "/bill/refund/export", name = "根据条件导出账单退费记录")
  public ResponseResult<T> exportBillRefundRecord(
      HttpServletResponse response, @RequestBody @Validated BillRefundRecordQuery query) throws IOException {
    baseRefundBiz.exportBillRefundRecord(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询配诊记录
   *
   * @param query 查询条件
   * @return list
   */
  @ApiOperation("根据条件查询配诊记录")
  @PostMapping(value = "/matching/list", name = "数据记录-配诊记录-根据条件查询配诊记录")
  public ResponseResult<PageInfo<TreatmentMatchingRecordVO>> treatmentMatchingRecord(
      @RequestBody @Validated TreatmentMatchingRecordQuery query) {
    PageInfo<TreatmentMatchingRecordVO> list =
        treatmentProcessBiz.findTreatmentMatchingRecord(query);
    return ResponseUtil.success(list);
  }

  /**
   * 导出就诊配诊记录列表
   *
   * @param response 响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("导出就诊配诊记录列表")
  @PostMapping(value = "/matching/export", name = "数据记录-配诊记录-导出就诊配诊记录列表")
  public ResponseResult<T> exportTreatmentMatchingRecord(
      HttpServletResponse response, @RequestBody @Validated TreatmentMatchingRecordQuery query)
      throws IOException {
    treatmentProcessBiz.exportTreatmentMatchingRecord(response, query);
    return ResponseUtil.success(null);
  }
}
