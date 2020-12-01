package com.yunya.report.ultimate.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.EmployeePersonalWorkloadDetailQuery;
import com.yunya.feign.report.domain.query.EmployeeWorkloadDetailQuery;
import com.yunya.feign.report.domain.query.EmployeeWorkloadQuery;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.report.ultimate.biz.BaseBillDetailBiz;
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

  @Autowired private BaseBillDetailBiz billDetailBiz;

  /**
   * 根据条件查询员工工作量报表
   *
   * @param query 查询条件
   * @return list
   */
  @ApiOperation("公司端报表-人事报表-员工工作量")
  @PostMapping(value = "/employee/workload/list", name = "根据条件查询员工工作量列表")
  public ResponseResult<PageInfo<EmployeeWorkloadVO>> employeeWorkload(
      @RequestBody @Validated EmployeeWorkloadQuery query) {
    PageInfo<EmployeeWorkloadVO> result = billDetailBiz.findEmployeeWorkloadList(query);
    return ResponseUtil.success(result);
  }

  /**
   * 根据条件导出员工工作量列表
   *
   * @param response 响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-人事报表-员工工作量-导出")
  @PostMapping(value = "/employee/workload/export", name = "根据条件导出员工工作量列表")
  public ResponseResult<T> exportEmployeeWorkloadList(
      HttpServletResponse response, @RequestBody @Validated EmployeeWorkloadQuery query)
      throws IOException {
    billDetailBiz.exportEmployeeWorkloadList(response, query);
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
  public ResponseResult<PageInfo<EmployeeOrderDetailWorkloadVO>> orderDetailList(
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
  public ResponseResult<PageInfo<EmployeeReceivedDetailWorkloadVO>> receivedDetailList(
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
  @PostMapping(value = "/employee/workload/received/export", name = "根据条件导出员工个人补入工作量明细")
  public ResponseResult<T> exportEmployeePersonalSupplyWorkloadDetailList(
          HttpServletResponse response,
          @RequestBody @Validated EmployeePersonalWorkloadDetailQuery query)
          throws IOException {
    billDetailBiz.exportEmployeePersonalSupplyWorkloadDetailList(response, query);
    return ResponseUtil.success(null);
  }
}
