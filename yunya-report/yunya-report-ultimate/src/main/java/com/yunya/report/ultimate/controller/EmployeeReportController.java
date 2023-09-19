package com.yunya.report.ultimate.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.BillItemInfoQuery;
import com.yunya.feign.report.domain.query.BillItemTollWorkloadQuery;
import com.yunya.feign.report.domain.query.ClinicEmployeeWorkloadQuery;
import com.yunya.feign.report.domain.vo.BillItemDeductionAndWorkloadVO;
import com.yunya.feign.report.domain.vo.BillItemTollAndWorkloadVO;
import com.yunya.feign.report.domain.vo.ClinicEmployeeWorkloadOfOperationVO;
import com.yunya.feign.report.domain.vo.ClinicEmployeeWorkloadOfPersonnelVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.report.ultimate.biz.BaseBillDetailBiz;
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
  @Autowired private BaseBillDetailBiz billDetailBiz;
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

    @ApiOperation("公司端报表-报表统计-运营报表-员工报表-划扣收费项目工作量统计")
    @PostMapping(value = "/operation/tariff/pay/deduction/workload/list", name = "公司端报表-报表统计-运营报表-员工报表-划扣收费项目工作量统计")
    public ResponseResult<PageInfo<BillItemDeductionAndWorkloadVO>> tariffDeductionWorkloadStatistics(
            @RequestBody @Validated BillItemTollWorkloadQuery query) throws Exception {
        PageInfo<BillItemDeductionAndWorkloadVO> pageInfo =
                employeeWorkloadBiz.tariffDeductionWorkloadStatistics(query);
        return ResponseUtil.success(pageInfo);
    }

    @ApiOperation("公司端报表-报表统计-运营报表-员工报表-划扣收费项目工作量统计-导出统计明细")
    @PostMapping(value = "/operation/tariff/pay/deduction/workload/list/allExport", name = "公司端报表-报表统计-运营报表-员工报表-划扣收费项目工作量统计-导出统计明细")
    public ResponseResult<Boolean> allExporttariffDeductionWorkloadStatistics(
            HttpServletResponse response,  @RequestBody @Validated BillItemTollWorkloadQuery query) throws Exception {
        employeeWorkloadBiz.allExporttariffDeductionWorkloadStatistics(response,query);
        return ResponseUtil.success();
    }


  /**
   * 根据条件导出统计明细
   *
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-收费项目工作量统计-导出统计明细")
  @PostMapping(value = "/operation/tariff/pay/workload/list/allExport", name = "公司端报表-报表统计-运营报表-收费项目工作量统计-导出统计明细")
  public ResponseResult allExporttariffPaymentWorkloadStatistics(
          HttpServletResponse response,  @RequestBody @Validated BillItemTollWorkloadQuery query) throws Exception {
            employeeWorkloadBiz.allExporttariffPaymentWorkloadStatistics(response,query);
    return ResponseUtil.success(null);
  }

  /**
   * 根据条件查询收费数量&金额表导出明细一体表
   *
   * @param query 查询条件
   * @return PageInfo<BillingItemDetailVO>
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-收费项目工作量统计-导出明细一体表")
  @PostMapping(value = "/billItem/statistics/detail/allExport", name = "开单数量及金额导出明细一体表")
  public ResponseResult<T> billItemStatisticsDetailIntegrationAllExport(
          HttpServletResponse response, @RequestBody @Validated BillItemInfoQuery query)
          throws Exception {
    billDetailBiz.billItemStatisticsDetailIntegrationAllExport(query, response);
    return ResponseUtil.success(null);
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

  /**
   * 根据条件导出项目收费及工作量列表
   *
   * @param response http响应
   * @param query 查询参数
   * @return
   */
  @ApiOperation("咨询师业绩明细导出（公司端-运营报表-员工报表）")
  @PostMapping(value = "/consulter/list/detail/export", name = "咨询师业绩明细导出")
  public ResponseResult exportListDetailsOfConsulter(
          HttpServletResponse response, @RequestBody @Validated ClinicEmployeeWorkloadQuery query)
          throws Exception {
    employeeWorkloadBiz.exportListDetailsOfConsulter(response, query);
    return ResponseUtil.success(null);
  }
}
