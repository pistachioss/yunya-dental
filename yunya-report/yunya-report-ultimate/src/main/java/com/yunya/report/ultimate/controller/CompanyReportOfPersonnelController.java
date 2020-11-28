package com.yunya.report.ultimate.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.EmployeeWorkloadQuery;
import com.yunya.feign.report.domain.vo.EmployeeWorkloadVO;
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
  @PostMapping(value = "/bill/receivable/export", name = "根据条件导出员工工作量列表")
  public ResponseResult<T> exportEmployeeWorkloadList(
          HttpServletResponse response, @RequestBody @Validated EmployeeWorkloadQuery query)
          throws IOException {
    billDetailBiz.exportEmployeeWorkloadList(response, query);
    return ResponseUtil.success(null);
  }
}
