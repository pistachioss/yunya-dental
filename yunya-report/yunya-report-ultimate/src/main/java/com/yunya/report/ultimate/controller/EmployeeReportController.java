package com.yunya.report.ultimate.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.BillItemTollWorkloadQuery;
import com.yunya.feign.report.domain.query.ClinicEmployeeWorkloadQuery;
import com.yunya.feign.report.domain.vo.BillItemTollAndWorkloadVO;
import com.yunya.feign.report.domain.vo.ClinicEmployeeWorkloadOfOperationVO;
import com.yunya.feign.report.domain.vo.ClinicEmployeeWorkloadOfPersonnelVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.report.ultimate.biz.EmployeeWorkloadBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * 简介：员工报表控制层
 *
 * @author: chenlin @Description: 员工报表控制层 @Date: 2021/10/25 16:12
 * @since: 1.0.0
 */
@Api(tags = "报表-员工报表")
@RestController
public class EmployeeReportController {
  /** 员工工作量 */
  @Autowired private EmployeeWorkloadBiz employeeWorkloadBiz;

  /**
   * 根据条件查询运营报表的员工工作量列表
   *
   * @param query 查询条件
   * @return PageInfo<ClinicEmployeeWorkloadOfOperationVO>
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-员工工作量")
  @PostMapping(value = "/operation/employee/workload/list", name = "根据条件查询运营报表的员工工作量列表")
  public ResponseResult<PageInfo<ClinicEmployeeWorkloadOfOperationVO>>
      employeeWorkloadListOfOperation(@RequestBody @Validated ClinicEmployeeWorkloadQuery query)
          throws Exception {
    PageInfo<ClinicEmployeeWorkloadOfOperationVO> pageInfo =
        employeeWorkloadBiz.findEmployeeWorkloadListOfOperation(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出运营报表的员工工作量报表
   *
   * @param response http响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-员工工作量（咨询师工作量）-导出")
  @PostMapping(value = "/operation/workload/list/export", name = "根据条件导出运营报表的员工工作量（咨询师工作量）报表")
  public ResponseResult<T> exportEmployeeWorkloadListOfOperationVO(
      HttpServletResponse response, @RequestBody @Validated ClinicEmployeeWorkloadQuery query)
      throws Exception {
    employeeWorkloadBiz.exportEmployeeWorkloadListOfOperation(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询人事报表的员工工作量列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeWorkloadOfOperationVO>
   */
  @ApiOperation("公司端报表-报表统计-人事报表-员工报表-员工工作量")
  @PostMapping(value = "/personnel/employee/workload/list", name = "根据条件查询人事报表的员工工作量列表")
  public ResponseResult<PageInfo<ClinicEmployeeWorkloadOfPersonnelVO>>
      employeeWorkloadListOfPersonnel(@RequestBody @Validated ClinicEmployeeWorkloadQuery query)
          throws Exception {
    PageInfo<ClinicEmployeeWorkloadOfPersonnelVO> pageInfo =
        employeeWorkloadBiz.findEmployeeWorkloadListOfPersonnel(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出人事报表的员工工作量列表
   *
   * @param response 响应
   * @param query 查询条件
   * @return void
   */
  @ApiOperation("公司端报表-人事报表-员工工作量-导出")
  @PostMapping(value = "/personnel/employee/workload/export", name = "根据条件导出人事报表的员工工作量列表")
  public ResponseResult<T> exportEmployeeWorkloadListOfPersonnel(
      HttpServletResponse response, @RequestBody @Validated ClinicEmployeeWorkloadQuery query)
      throws Exception {
    employeeWorkloadBiz.exportEmployeeWorkloadListOfPersonnel(response, query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询项目收费工作量
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-收费项目工作量统计")
  @PostMapping(value = "/operation/tariff/pay/workload/list", name = "公司端报表-报表统计-运营报表-收费项目工作量统计")
  public ResponseResult<PageInfo<BillItemTollAndWorkloadVO>> tariffPaymentWorkloadStatistics(
      @RequestBody @Validated BillItemTollWorkloadQuery query) throws Exception {
    PageInfo<BillItemTollAndWorkloadVO> pageInfo =
        employeeWorkloadBiz.findStatisticsTariffPaymentWorkloadList(query);
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
  @PostMapping(value = "/operation/tariff/pay/workload/list/export", name = "导出项目收费金额及工作量")
  public ResponseResult exportTariffPaymentWorkloadList(
      HttpServletResponse response, @RequestBody @Validated BillItemTollWorkloadQuery query)
      throws Exception {
    employeeWorkloadBiz.exportTariffPaymentWorkloadList(response, query);
    return ResponseUtil.success(null);
  }

  @ApiOperation("咨询师业绩查询（公司端-运营报表-员工报表）")
  @PostMapping(value = "/consulter/list", name = "咨询师业绩查询")
  public ResponseResult<PageInfo<ClinicEmployeeWorkloadOfOperationVO>> findWorkloadListOfConsulter(
      @RequestBody @Validated ClinicEmployeeWorkloadQuery query) throws Exception {
    PageInfo<ClinicEmployeeWorkloadOfOperationVO> pageInfo =
        employeeWorkloadBiz.findWorkloadListOfConsulter(query);
    return ResponseUtil.success(pageInfo);
  }
}
