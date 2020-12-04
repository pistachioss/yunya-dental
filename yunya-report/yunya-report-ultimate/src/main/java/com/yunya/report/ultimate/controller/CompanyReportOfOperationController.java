package com.yunya.report.ultimate.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.EmployeeWorkloadQuery;
import com.yunya.feign.report.domain.vo.EmployeeWorkloadOfOperationVO;
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
 * 简介: 公司端报表-报表统计-运营报表
 *
 * @author: chow
 * @date: 2020/12/4 14:25
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "公司端报表-报表统计-运营报表")
@RestController
@RequestMapping("operation")
public class CompanyReportOfOperationController {

  /** 账单明细 */
  @Autowired private BaseBillDetailBiz billDetailBiz;

  /**
   * 根据条件查询员工工作量列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeWorkloadOfOperationVO>
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-员工工作量")
  @PostMapping(value = "/employee/workload/list", name = "根据条件查询员工工作量列表")
  public ResponseResult<PageInfo<EmployeeWorkloadOfOperationVO>> employeeWorkloadListOfOperationVO(
      @RequestBody @Validated EmployeeWorkloadQuery query) {
    PageInfo<EmployeeWorkloadOfOperationVO> pageInfo =
        billDetailBiz.findEmployeeWorkloadListOfOperation(query);
    return ResponseUtil.success(pageInfo);
  }

  /**
   * 根据条件导出员工工作量报表
   *
   * @param response http响应
   * @param query 查询条件
   * @return
   */
  @ApiOperation("公司端报表-报表统计-运营报表-员工报表-员工工作量-导出")
  @PostMapping(value = "/billDetailBiz", name = "billDetailBiz")
  public ResponseResult<T> exportEmployeeWorkloadListOfOperationVO(
      HttpServletResponse response, @RequestBody @Validated EmployeeWorkloadQuery query)
      throws IOException {
    billDetailBiz.exportEmployeeWorkloadListOfOperation(response, query);
    return ResponseUtil.success(null);
  }
}
