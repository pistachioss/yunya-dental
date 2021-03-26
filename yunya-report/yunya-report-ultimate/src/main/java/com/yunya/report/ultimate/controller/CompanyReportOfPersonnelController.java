package com.yunya.report.ultimate.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.feign.treatment.domain.vo.AssistantMatchingStatisticsVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.report.ultimate.biz.BaseBillDetailBiz;
import com.yunya.report.ultimate.biz.BaseRefundBiz;
import com.yunya.report.ultimate.biz.BaseTreatmentProcessBiz;
import com.yunya.report.ultimate.biz.BaseUserPostBiz;
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
 * 简介: 公司端报表-人事报表控制层
 *
 * @author: chow
 * @date: 2020/10/29 14:45
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "公司端报表-人事报表")
@RestController
@RequestMapping("personnel")
public class CompanyReportOfPersonnelController {

  /** 账单详情 */
  @Autowired private BaseBillDetailBiz billDetailBiz;
  /** 退费 */
  @Autowired private BaseRefundBiz refundBiz;
  /** 员工 */
  @Autowired private BaseUserPostBiz baseUserPostBiz;
  /** 就诊 */
  @Autowired private BaseTreatmentProcessBiz treatmentProcessBiz;

  /**
   * 根据条件查询员工工作量报表(人事报表)
   *
   * @param query 查询条件
   * @return list
   */
  @ApiOperation("公司端报表-人事报表-员工工作量")
  @PostMapping(value = "/employee/workload/list", name = "根据条件查询员工工作量列表")
  public ResponseResult<PageInfo<EmployeeWorkloadOfPersonnelVO>> employeeWorkloadOfPersonnel(
      @RequestBody @Validated EmployeeWorkloadQuery query) {
    PageInfo<EmployeeWorkloadOfPersonnelVO> result =
        billDetailBiz.findEmployeeWorkloadListOfPersonnel(query);
    return ResponseUtil.success(result);
  }

  /**
   * 根据条件导出员工工作量列表
   *
   * @param response 响应
   * @param query 查询条件
   * @return void
   */
  @ApiOperation("公司端报表-人事报表-员工工作量-导出")
  @PostMapping(value = "/employee/workload/export", name = "根据条件导出员工工作量列表")
  public ResponseResult<T> exportEmployeeWorkloadListOfPersonnel(
      HttpServletResponse response, @RequestBody @Validated EmployeeWorkloadQuery query)
      throws IOException {
    billDetailBiz.exportEmployeeWorkloadListOfPersonnel(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询员工个人实收工作量明细列表
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-人事报表-员工工作量-实收工作量明细")
  @PostMapping(value = "/employee/workload/detail/list", name = "根据条件查询员工个人实收工作量明细列表")
  public ResponseResult<PageInfo<EmployeePersonalActualWorkloadDetailVO>>
      findEmployeePersonalActualWorkloadDetailList(
          @RequestBody @Validated EmployeePersonalWorkloadDetailQuery query) {
    PageInfo<EmployeePersonalActualWorkloadDetailVO> pageInfo =
        billDetailBiz.findEmployeePersonalActualWorkloadDetailList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出员工个人实收工作量明细列表
   *
   * @param response 响应
   * @param query 查询条件
   * @return void
   */
  @ApiOperation("公司端报表-人事报表-员工工作量-实收工作量明细-导出")
  @PostMapping(value = "/employee/workload/detail/export", name = "根据条件导出员工个人实收工作量明细列表")
  public ResponseResult<T> exportEmployeePersonalActualWorkloadDetailList(
      HttpServletResponse response,
      @RequestBody @Validated EmployeePersonalWorkloadDetailQuery query)
      throws IOException {
    billDetailBiz.exportEmployeePersonalActualWorkloadDetailList(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询员工工作量开单明细列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeOrderDetailWorkloadVO> 分页列表
   */
  @ApiOperation("公司端报表-人事报表-员工工作量-实收工作量明细-查看明细")
  @PostMapping(value = "/employee/order/detail/list", name = "公司端报表-人事报表-员工工作量-实收工作量明细-查看详情")
  public ResponseResult<PageInfo<EmployeeOrderDetailWorkloadVO>> actualOrderDetailList(
      @RequestBody @Validated EmployeeWorkloadDetailQuery query) {
    PageInfo<EmployeeOrderDetailWorkloadVO> pageInfo =
        billDetailBiz.findOrderDetailWorkloadList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件查询员工个人已收工作量明细列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeePersonalReceivedWorkloadDetailVO>
   */
  @ApiOperation("公司端报表-人事报表-员工工作量-已收工作量明细")
  @PostMapping(value = "/employee/workload/received/list", name = "根据条件查询员工个人已收工作量明细列表")
  public ResponseResult<PageInfo<EmployeePersonalReceivedWorkloadDetailVO>>
      findEmployeePersonalReceivedWorkloadDetailList(
          @RequestBody @Validated EmployeePersonalWorkloadDetailQuery query) {
    PageInfo<EmployeePersonalReceivedWorkloadDetailVO> pageInfo =
        billDetailBiz.findEmployeePersonalReceivedWorkloadDetailList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出员工个人已收工作量明细列表
   *
   * @param response 响应
   * @param query 查询条件
   * @return void
   */
  @ApiOperation("公司端报表-人事报表-员工工作量-已收工作量明细-导出")
  @PostMapping(value = "/employee/workload/received/export", name = "根据条件导出员工个人已收工作量明细列表")
  public ResponseResult<T> exportEmployeePersonalReceivedWorkloadDetailList(
      HttpServletResponse response,
      @RequestBody @Validated EmployeePersonalWorkloadDetailQuery query)
      throws IOException {
    billDetailBiz.exportEmployeePersonalReceivedWorkloadDetailList(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询员工已收工作量明细列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeOrderDetailWorkloadVO> 分页列表
   */
  @ApiOperation("公司端报表-人事报表-员工工作量-已收工作量明细-查看明细")
  @PostMapping(value = "/employee/workload/received/detail", name = "公司端报表-人事报表-员工工作量-已收工作量明细-查看详情")
  public ResponseResult<PageInfo<EmployeeReceivedDetailWorkloadVO>> receivedOrderDetailList(
      @RequestBody @Validated EmployeeWorkloadDetailQuery query) {
    PageInfo<EmployeeReceivedDetailWorkloadVO> pageInfo =
        billDetailBiz.findReceivedDetailList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件查询员工补入工作量明细
   *
   * @param query 查询条件
   * @return PageInfo<EmployeePersonalSupplyWorkloadDetailVO>
   */
  @ApiOperation("公司端报表-人事报表-员工工作量-补入工作量明细")
  @PostMapping(value = "/employee/workload/supply/list", name = "公司端报表-人事报表-员工工作量-补入工作量明细")
  public ResponseResult<PageInfo<EmployeePersonalSupplyWorkloadDetailVO>> supplyWorkloadDetailList(
      @RequestBody @Validated EmployeePersonalWorkloadDetailQuery query) {
    PageInfo<EmployeePersonalSupplyWorkloadDetailVO> pageInfo =
        billDetailBiz.findSupplyWorkloadDetailList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出员工个人补入工作量明细
   *
   * @param response 响应
   * @param query 查询条件
   * @return void
   */
  @ApiOperation("公司端报表-人事报表-员工工作量-补入工作量明细-导出")
  @PostMapping(value = "/employee/workload/supply/export", name = "根据条件导出员工个人补入工作量明细")
  public ResponseResult<T> exportEmployeePersonalSupplyWorkloadDetailList(
      HttpServletResponse response,
      @RequestBody @Validated EmployeePersonalWorkloadDetailQuery query)
      throws IOException {
    billDetailBiz.exportEmployeePersonalSupplyWorkloadDetailList(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询员工补入工作量明细列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeSupplyDetailWorkloadVO> 分页列表
   */
  @ApiOperation("公司端报表-人事报表-员工工作量-补入工作量明细-查看明细")
  @PostMapping(value = "/employee/workload/supply/detail", name = "公司端报表-人事报表-员工工作量-补入工作量明细-查看明细")
  public ResponseResult<PageInfo<EmployeeSupplyDetailWorkloadVO>> supplyOrderDetailList(
      @RequestBody @Validated EmployeeWorkloadDetailQuery query) {
    PageInfo<EmployeeSupplyDetailWorkloadVO> pageInfo = billDetailBiz.findSupplyDetailList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件查询员工退费工作量明细
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-人事报表-员工工作量-退费工作量明细")
  @PostMapping(value = "/employee/workload/refund/list", name = "根据条件查询员工退费工作量明细")
  public ResponseResult<PageInfo<EmployeePersonalRefundWorkloadDetailVO>> refundWorkloadDetailList(
      @RequestBody @Validated EmployeePersonalWorkloadDetailQuery query) {
    PageInfo<EmployeePersonalRefundWorkloadDetailVO> pageInfo =
        refundBiz.findRefundWorkloadDetailList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出员工个人退费工作量明细
   *
   * @param response 响应
   * @param query 查询条件
   * @return void
   */
  @ApiOperation("公司端报表-人事报表-员工工作量-退费工作量明细-导出")
  @PostMapping(value = "/employee/workload/refund/export", name = "根据条件导出员工个人退费工作量明细")
  public ResponseResult<T> exportEmployeePersonalRefundWorkloadDetailList(
      HttpServletResponse response,
      @RequestBody @Validated EmployeePersonalWorkloadDetailQuery query)
      throws IOException {
    refundBiz.exportEmployeePersonalRefundWorkloadDetailList(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询员工退费工作量明细列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeSupplyDetailWorkloadVO> 分页列表
   */
  @ApiOperation("公司端报表-人事报表-员工工作量-退费工作量明细-查看明细")
  @PostMapping(value = "/employee/workload/refund/detail", name = "公司端报表-人事报表-员工工作量-退费工作量明细-查看明细")
  public ResponseResult<PageInfo<EmployeeRefundDetailWorkloadVO>> refundOrderDetailList(
      @RequestBody @Validated EmployeeRefundWorkloadDetailQuery query) {
    PageInfo<EmployeeRefundDetailWorkloadVO> pageInfo = refundBiz.findRefundOrderDetailList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件查询配诊统计列表
   *
   * @param query 查询条件
   * @return PageInfo<AssistantMatchingStatisticsVO>
   */
  @ApiOperation("公司端报表-人事报表-配诊统计")
  @PostMapping(value = "/matching/statistics/list", name = "公司端报表-人事报表-配诊统计")
  public ResponseResult<PageInfo<AssistantMatchingStatisticsVO>> treatMatchingStatisticsList(
      @RequestBody @Validated EmployeeMatchingRecordQuery query) {
    PageInfo<AssistantMatchingStatisticsVO> pageInfo =
        baseUserPostBiz.findTreatMatchingStatisticsList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出助手配诊统计列表
   *
   * @param response 响应
   * @param query 查询条件
   * @return void
   */
  @ApiOperation("公司端报表-人事报表-配诊统计-导出")
  @PostMapping(value = "/matching/statistics/list/export", name = "根据条件导出助手配诊统计列表")
  public ResponseResult<T> exportEmployeeTreatMatchingStatisticsList(
      HttpServletResponse response, @RequestBody @Validated EmployeeMatchingRecordQuery query)
      throws IOException {
    baseUserPostBiz.exportEmployeeMatchingStatisticsList(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询员工配诊时长明细列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeTreatMatchingDetailVO>
   */
  @ApiOperation("公司端报表-人事报表-配诊统计-配诊时长明细")
  @PostMapping(value = "/employee/matching/detail/list", name = "公司端报表-人事报表-配诊统计-配诊时长明细")
  public ResponseResult<PageInfo<EmployeeTreatMatchingDetailVO>> assistantMatchingDetailList(
      @RequestBody @Validated AssistantMatchingDetailQuery query) {
    PageInfo<EmployeeTreatMatchingDetailVO> pageInfo =
        treatmentProcessBiz.findAssistantMatchingDetailList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件查询助手实收工作量明细列表
   *
   * @param query 查询条件
   * @return PageInfo<AssistantActualWorkloadDetailVO>
   */
  @ApiOperation("公司端报表-人事报表-配诊统计-实收工作量明细")
  @PostMapping(value = "/actual/workload/detail/list", name = "公司端报表-人事报表-配诊统计-实收工作量明细")
  public ResponseResult<PageInfo<AssistantActualWorkloadDetailVO>> assistantActualWorkloadDetail(
      @RequestBody @Validated AssistantActualWorkloadDetailQuery query) {
    PageInfo<AssistantActualWorkloadDetailVO> pageInfo =
        billDetailBiz.findAssistantActualWorkloadDetailList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件查询助手退费金额明细列表
   *
   * @param query 查询条件
   * @return PageInfo<AssistantRefundDetailVO> pageInfo
   */
  @ApiOperation("公司端报表-人事报表-配诊统计-退费金额明细")
  @PostMapping(value = "/refund/detail/list", name = "公司端报表-人事报表-配诊统计-退费金额明细")
  public ResponseResult<PageInfo<AssistantRefundDetailVO>> assistantRefundDetailList(
      @RequestBody @Validated AssistantRefundDetailQuery query) {
    PageInfo<AssistantRefundDetailVO> pageInfo = refundBiz.findAssistantRefundDetailList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件查询员工个人免单支付工作量明细列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeFreepaymentWorkloadDetailVO>
   */
  @ApiOperation("公司端报表-人事报表-员工工作量-免单支付工作量明细")
  @PostMapping(value = "/employee/workload/freepayment/list", name = "根据条件查询员工免单支付工作量明细列表")
  public ResponseResult<PageInfo<EmployeeFreepaymentWorkloadDetailVO>>
      findEmployeePersonalFreepaymentWorkloadDetailList(
          @RequestBody @Validated EmployeePersonalWorkloadDetailQuery query) {
    PageInfo<EmployeeFreepaymentWorkloadDetailVO> pageInfo =
        billDetailBiz.findEmployeeFreepaymentWorkloadDetailList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出员工个人免单支付工作量明细列表导出
   *
   * @param response 响应
   * @param query 查询条件
   * @return void
   */
  @ApiOperation("公司端报表-人事报表-员工工作量-免单支付工作量明细-导出")
  @PostMapping(value = "/employee/workload/freepayment/export", name = "根据条件导出员工个人免单支付工作量明细列表")
  public ResponseResult<T> exportEmployeeFreePaymentWorkloadDetailList(
      HttpServletResponse response,
      @RequestBody @Validated EmployeePersonalWorkloadDetailQuery query)
      throws IOException {
    billDetailBiz.exportEmployeeFreepaymentdWorkloadDetailList(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询员工免单支付工作量明细中项目列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeOrderDetailWorkloadVO> 分页列表
   */
  @ApiOperation("公司端报表-人事报表-员工工作量-免单支付工作量明细-查看项目明细")
  @PostMapping(
      value = "/employee/workload/freepayment/detail",
      name = "公司端报表-人事报表-员工工作量-免单支付工作量明细-查看项目详情")
  public ResponseResult<PageInfo<EmployeeReceivedDetailWorkloadVO>> freePaymentOrderDetailList(
      @RequestBody @Validated EmployeeFreePaymentWorkloadDetailQuery query) {
    PageInfo<EmployeeReceivedDetailWorkloadVO> pageInfo =
        billDetailBiz.findFreePaymentDetailList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件查询员工开单项目实收金额及数量列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeFreepaymentWorkloadDetailVO>
   */
  @ApiOperation("公司端报表-人事报表-开单项目实收金额及数量")
  @PostMapping(value = "/employee/billItem/list", name = "根据条件查询开单项目实收金额及数量列表")
  public ResponseResult<PageInfo<EmployeeFreepaymentWorkloadDetailVO>> findEmployeeBillItemList(
      @RequestBody @Validated EmployeeBillItemQuery query) {
    //    PageInfo<EmployeeFreepaymentWorkloadDetailVO> pageInfo =
    //            billDetailBiz.findEmployeeBillItemList(query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询个人工作量列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeWorkloadOfOperationVO>
   */
  @ApiOperation("门诊端个人中心-个人报表-个人工作量")
  @PostMapping(value = "/personal/workload/list", name = "根据条件查询个人工作量列表")
  public ResponseResult<PageInfo<PersonalWorkloadVO>> personalWorkloadList(
      @RequestBody @Validated EmployeeWorkloadQuery query) {
    PageInfo<PersonalWorkloadVO> pageInfo = billDetailBiz.personalWorkloadList(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件查询个人工作量列表导出
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeWorkloadOfOperationVO>
   */
  @ApiOperation("门诊端个人中心-个人报表-个人工作量导出")
  @PostMapping(value = "/personal/workload/export", name = "根据条件查询个人工作量列表导出")
  public ResponseResult<T> personalWorkloadExport(
      HttpServletResponse response, @RequestBody @Validated EmployeeWorkloadQuery query)
      throws IOException {
    billDetailBiz.personalWorkloadExport(query, response);
    return ResponseUtil.success(null);
  }
}
