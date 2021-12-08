package com.yunya.report.ultimate.controller;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.report.domain.query.ClinicEmployeeWorkloadQuery;
import com.yunya.feign.report.domain.query.DentistDimensionQueryForm;
import com.yunya.feign.report.domain.query.PatientDimensionQueryForm;
import com.yunya.feign.report.domain.vo.DynamicHeaderPageInfo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.report.ultimate.biz.DimensionReportBiz;
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
 * 简介：维度报表控制层
 *
 * @author: chenlin
 * @Description: 维度报表控制层
 * @Date: 2021/12/7 13:40
 * @since: 1.0.0
 */
@RestController
@RequestMapping("/dimesionReport")
public class DimensionReportController {


    /** 维度报表*/
    @Autowired
    private DimensionReportBiz dimesionReportBiz;

    /**
     * 根据条件查询患者维度统计表
     *
     * @param query 查询条件
     * @return PageInfo<PatientDimensionStatisticsVO>
     */
    @ApiOperation("公司端报表-报表统计-运营报表-维度报表-患者维度")
    @PostMapping(value = "/patientDimension", name = "根据条件查询患者维度统计表")
    public ResponseResult<DynamicHeaderPageInfo<JSONObject>> patientDimensionStatistics(
            @RequestBody @Validated PatientDimensionQueryForm query) throws Exception {
        DynamicHeaderPageInfo<JSONObject> pageInfo = dimesionReportBiz.patientDimensionStatistics(query);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 根据条件导出患者维度统计表
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-维度报表-患者维度导出")
    @PostMapping(value = "/patientDimension/export", name = "根据条件导出患者维度统计表")
    public ResponseResult<T> patientDimensionStatisticsExport(
            HttpServletResponse response, @RequestBody @Validated PatientDimensionQueryForm query)
            throws Exception {
        dimesionReportBiz.patientDimensionStatisticsExport(query, response);
        return ResponseUtil.success(null);
    }

    /**
     * 根据条件查询医生维度统计表
     *
     * @param query 查询条件
     * @return DynamicHeaderPageInfo<JSONObject>
     */
    @ApiOperation("公司端报表-报表统计-运营报表-维度报表-医生维度")
    @PostMapping(value = "/dentistDimension", name = "根据条件查询医生维度统计表")
    public ResponseResult<DynamicHeaderPageInfo<JSONObject>> dentistDimensionStatistics(
            @RequestBody @Validated DentistDimensionQueryForm query) {
        DynamicHeaderPageInfo<JSONObject> pageInfo = dimesionReportBiz.dentistDimensionStatistics(query);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 根据条件导出医生维度统计表
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-维度报表-医生维度导出")
    @PostMapping(value = "/dentistDimension/export", name = "根据条件导出医生维度统计表")
    public ResponseResult<T> dentistDimensionStatisticsExport(
            HttpServletResponse response, @RequestBody @Validated DentistDimensionQueryForm query)
            throws IOException {
        dimesionReportBiz.dentistDimensionStatisticsExport(query, response);
        return ResponseUtil.success(null);
    }

    /**
     * 根据条件查询门诊维度统计表
     *
     * @param query 查询条件
     * @return PageInfo<BillItemStatisticsInfoVO>
     */
    @ApiOperation("公司端报表-报表统计-运营报表-维度报表-门诊维度")
    @PostMapping(value = "/clinicDimension", name = "根据条件查询门诊维度统计表")
    public ResponseResult<DynamicHeaderPageInfo<JSONObject>> clinicDimensionStatistics(
            @RequestBody @Validated ClinicEmployeeWorkloadQuery query) {
        DynamicHeaderPageInfo<JSONObject> pageInfo = dimesionReportBiz.clinicDimensionStatistics(query);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 根据条件导出门诊维度统计表
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-维度报表-门诊维度导出")
    @PostMapping(value = "/clinicDimension/export", name = "根据条件导出门诊维度统计表")
    public ResponseResult<T> clinicDimensionStatisticsExport(
            HttpServletResponse response, @RequestBody @Validated ClinicEmployeeWorkloadQuery query)
            throws IOException {
        dimesionReportBiz.clinicDimensionStatisticsExport(query, response);
        return ResponseUtil.success(null);
    }
}
