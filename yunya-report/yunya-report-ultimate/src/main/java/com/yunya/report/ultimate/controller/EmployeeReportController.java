package com.yunya.report.ultimate.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.ClinicEmployeeWorkloadQuery;
import com.yunya.feign.report.domain.vo.ClinicEmployeeWorkloadOfOperationVO;
import com.yunya.feign.report.domain.vo.ClinicEmployeeWorkloadOfPersonnelVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.report.ultimate.biz.EmployeeWorkloadBiz;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/10/25 16:12
 * @since: 1.0.0
 */
@RestController
public class EmployeeReportController {
    /** 员工工作量*/
    @Autowired
    private EmployeeWorkloadBiz employeeWorkloadBiz;

    /**
     * 根据条件查询运营报表的员工工作量列表
     *
     * @param query 查询条件
     * @return PageInfo<ClinicEmployeeWorkloadOfOperationVO>
     */
    @ApiOperation("公司端报表-报表统计-运营报表-员工报表-员工工作量")
    @PostMapping(value = "/operation/employee/workload/list", name = "根据条件查询运营报表的员工工作量列表")
    public ResponseResult<PageInfo<ClinicEmployeeWorkloadOfOperationVO>> employeeWorkloadListOfOperation(
            @RequestBody @Validated ClinicEmployeeWorkloadQuery query) throws Exception {
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
    @ApiOperation("公司端报表-报表统计-运营报表-员工报表-员工工作量-导出")
    @PostMapping(value = "/workload/list/export", name = "根据条件导出运营报表的员工工作量报表")
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
    public ResponseResult<PageInfo<ClinicEmployeeWorkloadOfPersonnelVO>> employeeWorkloadListOfPersonnel(
            @RequestBody @Validated ClinicEmployeeWorkloadQuery query) throws Exception {
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
    @PostMapping(value = "/employee/workload/export", name = "根据条件导出人事报表的员工工作量列表")
    public ResponseResult<T> exportEmployeeWorkloadListOfPersonnel(
            HttpServletResponse response, @RequestBody @Validated ClinicEmployeeWorkloadQuery query)
            throws Exception {
        employeeWorkloadBiz.exportEmployeeWorkloadListOfPersonnel(response, query);
        return ResponseUtil.success(null);
    }
}
