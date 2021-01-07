package com.yunya.modules.treatment.controller.web;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.CurrentMonthBillInfoQuery;
import com.yunya.feign.treatment.domain.query.BillAdjustRecordQuery;
import com.yunya.feign.treatment.domain.query.BillPayRecordAdjustQuery;
import com.yunya.feign.treatment.domain.query.BillTollRevokeRecordQuery;
import com.yunya.feign.treatment.domain.query.CurrentMonthBillAdjustQuery;
import com.yunya.feign.treatment.domain.vo.BillOfAdjustRecordVO;
import com.yunya.feign.treatment.domain.vo.BillOfPayRecordAdjustVO;
import com.yunya.feign.treatment.domain.vo.BillOfTollRevokeRecordVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.BillExceptionHandleRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 简介: 账单异常处理控制器
 *
 * @author: chow
 * @date: 2020/9/24 20:27
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "账单异常处理")
@RestController
@RequestMapping("handle")
public class BillExceptionHandleRecordController {

  /** 账单异常处理 */
  private final BillExceptionHandleRecordBiz billExceptionHandleRecordBiz;

  public BillExceptionHandleRecordController(
      BillExceptionHandleRecordBiz billExceptionHandleRecordBiz) {
    this.billExceptionHandleRecordBiz = billExceptionHandleRecordBiz;
  }

  /**
   * 根据账单异常处理记录ID查询账单处理详情
   *
   * @param billHandleRecordId 账单异常处理记录ID
   * @return
   */
  @ApiOperation("根据账单异常处理记录ID查询账单异常处理详情")
  @GetMapping(value = "/detail/{billHandleRecordId}", name = "根据账单异常处理记录ID查询账单异常处理详情")
  public ResponseResult<Map<String, Object>> findBillHandleDetailById(
      @PathVariable(value = "billHandleRecordId") Integer billHandleRecordId) {
    Map<String, Object> resultMap =
        billExceptionHandleRecordBiz.findBillHandleDetail(billHandleRecordId);
    return ResponseUtil.success(resultMap);
  }

  /**
   * 根据条件查询账单调整记录列表
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("数据记录-账单记录-账单调整记录")
  @PostMapping(value = "/bill/adjust/list", name = "根据条件查询账单调整记录列表")
  public ResponseResult<PageInfo<BillOfAdjustRecordVO>> billAdjustRecordList(
      @RequestBody @Validated BillAdjustRecordQuery query) {
    List<BillOfAdjustRecordVO> resultList =
        billExceptionHandleRecordBiz.findBillAdjustRecord(query);
    if (query.getWhetherPage()) {
      Integer pageNum = query.getPageNum();
      Integer pageSize = query.getPageSize();
      int total = resultList.size();
      PageInfo<BillOfAdjustRecordVO> pageInfo = new PageInfo<>();
      pageInfo.setPageNum(pageNum);
      pageInfo.setPageSize(pageSize);
      pageInfo.setTotal(total);
      List<BillOfAdjustRecordVO> list =
          resultList.subList(pageSize * (pageNum - 1), (Math.min((pageSize * pageNum), total)));
      pageInfo.setList(list);
      return ResponseUtil.success(pageInfo);
    }
    return ResponseUtil.success(new PageInfo<>(resultList));
  }

  /**
   * 根据条件导出账单调整记录
   *
   * @param response http响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("数据记录-账单记录-导出账单调整记录")
  @PostMapping(value = "/bill/adjust/export", name = "数据记录-账单记录-导出账单调整记录")
  public ResponseResult<T> exportBillAdjustRecord(
      HttpServletResponse response, @RequestBody @Validated BillAdjustRecordQuery query)
      throws IOException {
    billExceptionHandleRecordBiz.exportBillAdjustRecord(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询账单调整记录列表
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("数据记录-账单记录-账单收费撤销记录")
  @PostMapping(value = "/bill/revoke/list", name = "根据条件查询账单收费撤销记录")
  public ResponseResult<PageInfo<BillOfTollRevokeRecordVO>> billRevokeRecordList(
      @RequestBody @Validated BillTollRevokeRecordQuery query) {
    List<BillOfTollRevokeRecordVO> resultList =
        billExceptionHandleRecordBiz.findBillRevokeRecordList(query);
    if (query.getWhetherPage()) {
      Integer pageNum = query.getPageNum();
      Integer pageSize = query.getPageSize();
      int total = resultList.size();
      PageInfo<BillOfTollRevokeRecordVO> pageInfo = new PageInfo<>();
      pageInfo.setPageNum(pageNum);
      pageInfo.setPageSize(pageSize);
      pageInfo.setTotal(total);
      List<BillOfTollRevokeRecordVO> list =
          resultList.subList(pageSize * (pageNum - 1), (Math.min((pageSize * pageNum), total)));
      pageInfo.setList(list);
      return ResponseUtil.success(pageInfo);
    }
    return ResponseUtil.success(new PageInfo<>(resultList));
  }

  /**
   * 根据条件导出账单收费撤销记录
   *
   * @param response http响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("数据记录-账单记录-导出账单撤销记录")
  @PostMapping(value = "/bill/revoke/export", name = "数据记录-账单记录-导出账单撤销记录")
  public ResponseResult<T> exportBillTollRevokeRecord(
      HttpServletResponse response, @RequestBody @Validated BillTollRevokeRecordQuery query)
      throws IOException {
    billExceptionHandleRecordBiz.exportBillTollRevokeRecord(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询收费支付方式调整记录列表
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("数据记录-账单记录-调整入账方式记录")
  @PostMapping(value = "/bill/pay/adjust/list", name = "根据条件查询调整入账方式记录")
  public ResponseResult<PageInfo<BillOfPayRecordAdjustVO>> billRevokeRecordList(
      @RequestBody @Validated BillPayRecordAdjustQuery query) {
    List<BillOfPayRecordAdjustVO> resultList =
        billExceptionHandleRecordBiz.findBillPayAdjustRecordList(query);
    if (query.getWhetherPage()) {
      Integer pageNum = query.getPageNum();
      Integer pageSize = query.getPageSize();
      int total = resultList.size();
      PageInfo<BillOfPayRecordAdjustVO> pageInfo = new PageInfo<>();
      pageInfo.setPageNum(pageNum);
      pageInfo.setPageSize(pageSize);
      pageInfo.setTotal(total);
      List<BillOfPayRecordAdjustVO> list =
          resultList.subList(pageSize * (pageNum - 1), (Math.min((pageSize * pageNum), total)));
      pageInfo.setList(list);
      return ResponseUtil.success(pageInfo);
    }
    return ResponseUtil.success(new PageInfo<>(resultList));
  }

  /**
   * 根据条件导出收费方式调整记录
   *
   * @param response http响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("数据记录-账单记录-导出入账方式调整记录")
  @PostMapping(value = "/bill/pay/adjust/export", name = "数据记录-账单记录-导出入账方式调整记录")
  public ResponseResult<T> exportBillPayAdjustRecord(
      HttpServletResponse response, @RequestBody @Validated BillPayRecordAdjustQuery query)
      throws IOException {
    billExceptionHandleRecordBiz.exportBillPayAdjustRecord(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件导出门诊当月调整账单列表
   *
   * @param response http响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-财务报表-对账单-本月调整账单-导出")
  @PostMapping(value = "/current/bill/adjust/export", name = "根据条件导出门诊当月调整账单列表")
  public ResponseResult<T> exportCurrentMonthAdjustBill(
      HttpServletResponse response, @RequestBody @Validated CurrentMonthBillAdjustQuery query)
      throws IOException {
    billExceptionHandleRecordBiz.exportCurrentMonthAdjustBill(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询门诊当月撤销收费记录
   *
   * @param response http响应
   * @param query 查询条件
   * @return
   * @throws IOException
   */
  @ApiOperation("公司端报表-财务报表-对账单-本月撤销收费-导出")
  @PostMapping(value = "/current/bill/revoke/export", name = "公司端报表-财务报表-对账单-本月撤销收费")
  public ResponseResult<T> billRevokeRecordExport(
      HttpServletResponse response, @RequestBody @Validated CurrentMonthBillInfoQuery query)
      throws IOException {
    billExceptionHandleRecordBiz.exportCurrentMonthBillRevokePayRecord(response, query);
    return ResponseUtil.success(null);
  }
}
