package com.yunya.modules.treatment.controller.web;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.BillOfReceivableQuery;
import com.yunya.feign.report.domain.query.StatementStatisticQuery;
import com.yunya.feign.report.domain.vo.BillRestReceivableAmountVO;
import com.yunya.feign.report.domain.vo.CurrentMonthBillStatisticVO;
import com.yunya.feign.treatment.domain.model.BillRefundModel;
import com.yunya.feign.treatment.domain.model.TreatBillRefundModel;
import com.yunya.feign.treatment.domain.vo.BillDetailGroupVO;
import com.yunya.feign.treatment.domain.vo.OrderDetailChargeVO;
import com.yunya.feign.treatment.domain.vo.PatientBillStatistics;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.treatment.biz.BillRecordBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 简介: 账单详情信息控制器
 *
 * @author: chow
 * @date: 2020/9/11 17:02
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "账单、开单详情查询")
@RestController
@RequestMapping("bill")
public class BillRecordController {

  /** 注入对象 */
  @Autowired private BillRecordBiz billRecordBiz;

  /**
   * 根据开单记录ID查询开单详情与账单详情信息
   *
   * @param orderRecordId 就诊记录ID
   * @return
   */
  @ApiOperation("根据开单ID查询开单详情与账单详情信息")
  @ApiImplicitParam(
      name = "orderRecordId",
      value = "开单记录ID",
      required = true,
      dataType = "int",
      paramType = "path")
  @GetMapping(value = "/detail/{orderRecordId}", name = "根据开单记录ID查询开单详情与账单详情信息")
  public ResponseResult<BillDetailGroupVO> findOrderDetailAndBillDetailByOrderRecordId(
      @PathVariable(value = "orderRecordId") Integer orderRecordId) {
    BillDetailGroupVO resultList = billRecordBiz.findOrderDetailAndBillDetail(orderRecordId);
    return ResponseUtil.success(resultList);
  }


  /**
   * 患者档案-账单详情-编辑备注提交
   * @param orderDetails 账单明细
   * @return
   */
  @ApiOperation("患者档案-账单详情-编辑备注提交")
  @PostMapping(value = "/edit/remarks",name = "患者档案-账单详情-编辑备注提交")
  @CurrentUser
  public ResponseResult editRemarks(@RequestBody List<OrderDetailChargeVO> orderDetails) {
    return billRecordBiz.editRemarks(orderDetails);
  }




  /**
   * 账单退费
   *
   * @param model 账单退费参数模型
   * @return
   */
  @RepeatSubmit
  @CurrentUser
  @ApiOperation("账单退费")
  @PostMapping(value = "/treat/refund", name = "账单退费")
  public ResponseResult billRefund(@RequestBody @Validated TreatBillRefundModel model) {
    billRecordBiz.billRefund(model);
    return ResponseUtil.success(null);
  }

  /**
   * 账单退费
   *
   * @param model 账单退费参数模型
   * @return
   */
  @RepeatSubmit
  @CurrentUser
  @ApiOperation(value = "账单退费", notes = "弃用")
  @PostMapping(value = "/refund", name = "账单退费")
  @Deprecated
  public ResponseResult billRefund(@RequestBody @Validated BillRefundModel model) {
    billRecordBiz.refund(model);
    return ResponseUtil.success(null);
  }

  /**
   * 根据患者ID查询患者账单消费信息
   *
   * @param patientId 患者ID
   * @return
   */
  @ApiOperation("根据患者ID查询患者账单消费信息")
  @ApiImplicitParam(
      name = "patientId",
      value = "患者ID",
      required = true,
      dataType = "int",
      paramType = "path")
  @GetMapping(value = "/count/{patientId}", name = "根据患者ID查询患者账单消费信息")
  public ResponseResult<PatientBillStatistics> statisticsBill(
      @PathVariable(value = "patientId") Integer patientId) {
    PatientBillStatistics statistics = billRecordBiz.statisticsBill(patientId);
    return ResponseUtil.success(statistics);
  }

  /**
   * 根据条件查询应收款余额表
   *
   * @param query 查询条件
   * @return list
   */
  @ApiOperation("公司端报表-财务报表-应收款余额表")
  @PostMapping(value = "/debt/list", name = "公司端报表-财务报表-应收款余额表")
  public ResponseResult<PageInfo<BillRestReceivableAmountVO>> findDebtList(
          @RequestBody @Validated BillOfReceivableQuery query) {
    PageInfo<BillRestReceivableAmountVO> pageInfo = billRecordBiz.findDebtList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件查询本月金额合计
   *
   * @param query 查询条件
   * @return list
   */
  @ApiOperation("公司端报表-对账单-本月金额合计")
  @PostMapping(value = "/statistic/list", name = "公司端报表-对账单-本月金额合计")
  public ResponseResult<CurrentMonthBillStatisticVO> findCurrentMonthStatementStatistic(
          @RequestBody @Validated StatementStatisticQuery query) {
    CurrentMonthBillStatisticVO resultData = billRecordBiz.findCurrentMonthStatementStatistic(query);
    return ResponseUtil.success(resultData);
  }

  /**
   * 根据条件导出应收款余额表
   *
   * @param query 查询条件
   * @return list
   */
  @ApiOperation("公司端报表-财务报表-应收款余额表导出")
  @PostMapping(value = "/debt/list/export", name = "公司端报表-财务报表-应收款余额表导出")
  public ResponseResult exportDebtList(HttpServletResponse response,
                                          @RequestBody @Validated BillOfReceivableQuery query) throws IOException {
    billRecordBiz.exportDebtList(query, response);
    return ResponseUtil.success(null);
  }
}
