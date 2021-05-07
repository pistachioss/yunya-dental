package com.yunya.modules.treatment.controller.web;

import com.yunya.feign.report.domain.query.CurrentMonthBillInfoQuery;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.BillPayRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 简介: 账单收费记录管理（撤销账单收费记录）
 *
 * @author: chow
 * @date: 2020/9/14 10:54
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "账单收费记录管理（撤销账单收费记录）")
@RestController
@RequestMapping("bill")
public class BillPayRecordController {

  /** 注入对象 */
  private final BillPayRecordBiz billPayRecordBiz;

  public BillPayRecordController(BillPayRecordBiz billPayRecordBiz) {
    this.billPayRecordBiz = billPayRecordBiz;
  }

  /**
   * 根据账单收费记录ID撤销账单收费记录
   *
   * @param billPayRecordId 账单收费记录ID
   * @return
   */
  @CurrentUser
  @ApiOperation("根据账单收费记录ID撤销账单收费记录")
  @GetMapping(value = "/revoke/{billPayRecordId}", name = "根据账单收费记录ID撤销账单收费记录")
  public ResponseResult<T> revokeBillPayRecord(
      @PathVariable(value = "billPayRecordId") Integer billPayRecordId) {
    billPayRecordBiz.revoke(billPayRecordId);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件导出门诊本月（使用优惠）账单列表
   *
   * @param response http响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端财务报表-对账单-本月收欠费（使用优惠）账单-导出")
  @PostMapping(value = "/collete/debt/export", name = "本月收欠费（使用优惠）账单导出")
  public ResponseResult<T> exportCollectDebtUsePrivilegeList(
      HttpServletResponse response, @RequestBody @Validated CurrentMonthBillInfoQuery query)
      throws IOException {
    billPayRecordBiz.exportBillCollectionDebt(response, query);
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
  @PostMapping(value = "/pay/export", name = "公司端报表-财务报表-对账单-本月收费明细-导出")
  public ResponseResult<T> billPayRecordExport(
      HttpServletResponse response, @RequestBody @Validated CurrentMonthBillInfoQuery query)
      throws IOException {
    billPayRecordBiz.exportCurrentMonthBillPayRecord(response, query);
    return ResponseUtil.success(null);
  }
}
