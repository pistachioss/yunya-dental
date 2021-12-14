package com.yunya.report.ultimate.controller;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.report.domain.query.ClinicEmployeeWorkloadQuery;
import com.yunya.feign.report.domain.query.PatientDimensionQueryForm;
import com.yunya.feign.report.domain.query.base.MultiClinicDateRangetQueryForm;
import com.yunya.feign.report.domain.vo.DynamicHeaderPageInfo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.report.ultimate.biz.DimensionReportBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(tags = "公司端报表-报表统计-运营报表-维度报表")
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
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message =
                                    "响应格式：{\"msg\":\"success\",\"data\":{\"lastPage\":0,\"startRow\":0,\"hasNextPage\":false,\"prePage\":0,\"nextPage\":0,\"endRow\":0,\"pageSize\":50,\"list\":[{\"patientName\":\"张小勇\",\"nextAppointDate\":\"\",\"nextRemindDate\":\"\",\"orionTypeName\":\"\",\"totalArrear\":7047.42,\"lastVisitDate\":\"2020-11-07\",\"memberTypeName\":\"艾维会员\",\"totalConsume\":31747.58,\"treatNum\":13,\"firstVisitDate\":\"2015-12-05\",\"age\":44,\"S1\":2,\"S2\":1}],\"pageNum\":1,\"navigatePages\":0,\"navigateFirstPage\":0,\"total\":1,\"pages\":0,\"firstPage\":0,\"size\":0,\"isLastPage\":false,\"hasPreviousPage\":false,\"navigateLastPage\":0,\"isFirstPage\":false,\"map\":{\"patientName\":\"患者\",\"age\":\"年龄\",\"orionTypeName\":\"患者类型\",\"memberTypeName\":\"会员等级\",\"treatNum\":\"就诊次数\",\"totalConsume\":\"累计消费\",\"totalArrear\":\"欠费总额\",\"firstVisitDate\":\"初诊日期\",\"lastVisitDate\":\"末次就诊日期\",\"nextAppointDate\":\"下次预约\",\"nextRemindDate\":\"下次提醒\",\"S1\":\"洁牙\",\"S2\":\"牙周治疗\"}},\"audit\":true,\"status\":0}")
            })
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
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message =
                                    "响应格式：{\"msg\":\"success\",\"data\":{\"lastPage\":1,\"navigatepageNums\":[1],\"startRow\":0,\"hasNextPage\":false,\"prePage\":0,\"nextPage\":0,\"endRow\":0,\"pageSize\":1,\"list\":[{\"firstVisitCount\":42,\"debtAmount\":65517.5,\"employeeName\":\"陈俊\",\"reFirstVisitCount\":274,\"S1\":5,\"S2\":7,\"hasntAppointAndRemind\":149,\"treatTimes\":377,\"T2\":24,\"T3\":0,\"reVisitCount\":190,\"workload\":1131935.75}],\"pageNum\":1,\"navigatePages\":8,\"navigateFirstPage\":1,\"total\":1,\"pages\":1,\"firstPage\":1,\"size\":1,\"isLastPage\":true,\"hasPreviousPage\":false,\"header\":[\"医生\",\"工作量\",\"初诊人数\",\"复诊人数\",\"就诊人次\",\"本月初诊且复诊\",\"患者来源\",\"\",\"\",\"\",\"\",\"\",\"\",\"欠费总额\",\"无下次预约或提醒客户\",\"专科数量\"],\"navigateLastPage\":1,\"contextMap\":{\"originTypeNames\":[\"患者转介绍\",\"线下活动\"],\"specialProjectNames\":[\"洁牙\",\"牙周治疗\"]},\"isFirstPage\":true,\"map\":{\"employeeName\":\"医生\",\"workload\":\"工作量\",\"firstVisitCount\":\"初诊人数\",\"reVisitCount\":\"复诊人数\",\"treatTimes\":\"就诊人次\",\"reFirstVisitCount\":\"本月初诊且复诊\",\"T2\":\"患者转介绍\",\"T3\":\"线下活动\",\"debtAmount\":\"欠费总额\",\"hasntAppointAndRemind\":\"无下次预约或提醒客户\",\"S1\":\"洁牙\",\"S2\":\"牙周治疗\"}},\"audit\":true,\"status\":0}")
            })
    @PostMapping(value = "/dentistDimension", name = "根据条件查询医生维度统计表")
    public ResponseResult<DynamicHeaderPageInfo<JSONObject>> dentistDimensionStatistics(
            @RequestBody @Validated ClinicEmployeeWorkloadQuery query) throws Exception {
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
            HttpServletResponse response, @RequestBody @Validated ClinicEmployeeWorkloadQuery query)
            throws Exception {
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
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message =
                                    "响应格式：{\"msg\":\"success\",\"data\":{\"lastPage\":1,\"navigatepageNums\":[1],\"startRow\":0,\"hasNextPage\":false,\"prePage\":0,\"nextPage\":0,\"endRow\":0,\"pageSize\":1,\"list\":[{\"firstVisitCount\":42,\"debtAmount\":65517.5,\"employeeName\":\"陈俊\",\"reFirstVisitCount\":274,\"S1\":5,\"S2\":7,\"hasntAppointAndRemind\":149,\"treatTimes\":377,\"T2\":24,\"T3\":0,\"reVisitCount\":190,\"workload\":1131935.75,\"abbreviation\":\"古墩路门诊\"}],\"pageNum\":1,\"navigatePages\":8,\"navigateFirstPage\":1,\"total\":1,\"pages\":1,\"firstPage\":1,\"size\":1,\"isLastPage\":true,\"hasPreviousPage\":false,\"header\":[\"门诊\",\"医生\",\"工作量\",\"初诊人数\",\"复诊人数\",\"就诊人次\",\"本月初诊且复诊\",\"患者来源\",\"\",\"\",\"\",\"\",\"\",\"\",\"欠费总额\",\"无下次预约或提醒客户\",\"专科数量\"],\"navigateLastPage\":1,\"contextMap\":{\"originTypeNames\":[\"患者转介绍\",\"线下活动\"],\"specialProjectNames\":[\"洁牙\",\"牙周治疗\"]},\"isFirstPage\":true,\"map\":{\"abbreviation\":\"门诊\",\"employeeName\":\"医生\",\"workload\":\"工作量\",\"firstVisitCount\":\"初诊人数\",\"reVisitCount\":\"复诊人数\",\"treatTimes\":\"就诊人次\",\"reFirstVisitCount\":\"本月初诊且复诊\",\"T2\":\"患者转介绍\",\"T3\":\"线下活动\",\"debtAmount\":\"欠费总额\",\"hasntAppointAndRemind\":\"无下次预约或提醒客户\",\"S1\":\"洁牙\",\"S2\":\"牙周治疗\"}},\"audit\":true,\"status\":0}")
            })
    @PostMapping(value = "/clinicDimension", name = "根据条件查询门诊维度统计表")
    public ResponseResult<DynamicHeaderPageInfo<JSONObject>> clinicDimensionStatistics(
            @RequestBody @Validated ClinicEmployeeWorkloadQuery query) throws Exception {
        DynamicHeaderPageInfo<JSONObject> pageInfo = dimesionReportBiz.clinicDimensionStatistics(query, true);
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
            throws Exception {
        dimesionReportBiz.clinicDimensionStatisticsExport(query, response);
        return ResponseUtil.success(null);
    }

    /**
     * 根据条件查询门诊初诊来源占比表
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-初诊来源占比表")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message =
                                    "响应格式：{\"msg\":\"success\",\"data\":{\"lastPage\":0,\"startRow\":0,\"hasNextPage\":false,\"prePage\":0,\"nextPage\":0,\"endRow\":0,\"pageSize\":0,\"list\":[{\"1\":91,\"2\":74,\"date\":\"2021\",\"firstVisitCount\":500,\"abbreviation\":\"古墩路门诊\"}],\"pageNum\":0,\"navigatePages\":0,\"navigateFirstPage\":0,\"total\":0,\"pages\":0,\"firstPage\":0,\"size\":0,\"isLastPage\":false,\"hasPreviousPage\":false,\"navigateLastPage\":0,\"isFirstPage\":false,\"map\":{\"1\":\"员工转介绍\",\"2\":\"患者转介绍\",\"abbreviation\":\"门诊\",\"date\":\"日期\",\"firstVisitCount\":\"初诊人数\"}},\"audit\":true,\"status\":0}")
            })
    @PostMapping(value = "/clinic/firstVisitSource/ratio", name = "公司端报表-报表统计-运营报表-门诊初诊来源占比表")
    public ResponseResult<DynamicHeaderPageInfo<JSONObject>> clinicFirstVisitSourceRatio(
            @RequestBody @Validated MultiClinicDateRangetQueryForm query) {
        DynamicHeaderPageInfo<JSONObject> pageInfo = dimesionReportBiz.clinicFirstVisitSourceRatio(query);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 根据条件导出门诊初诊来源占比表
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-门诊业绩&业务报表-初诊来源数量分析导出")
    @PostMapping(
            value = "/clinic/firstVisitSource/ratio/export",
            name = "公司端报表-报表统计-运营报表-门诊业绩&业务报表-初诊来源数量分析导出")
    public ResponseResult<T> clinicFirstVisitSourceRatioExport(
            HttpServletResponse response, @RequestBody @Validated MultiClinicDateRangetQueryForm query)
            throws IOException {
        dimesionReportBiz.clinicFirstVisitSourceRatioExport(query, response);
        return ResponseUtil.success(null);
    }

    /**
     * 根据条件查询门诊专科工作量占比表
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-专科占比表")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message =
                                    "响应格式：{\"msg\":\"success\",\"data\":{\"lastPage\":0,\"startRow\":0,\"hasNextPage\":false,\"prePage\":0,\"nextPage\":0,\"endRow\":0,\"pageSize\":0,\"list\":[{\"1\":91,\"2\":74,\"date\":\"2021\",\"firstVisitCount\":500,\"abbreviation\":\"古墩路门诊\"}],\"pageNum\":0,\"navigatePages\":0,\"navigateFirstPage\":0,\"total\":0,\"pages\":0,\"firstPage\":0,\"size\":0,\"isLastPage\":false,\"hasPreviousPage\":false,\"navigateLastPage\":0,\"isFirstPage\":false,\"map\":{\"1\":\"员工转介绍\",\"2\":\"患者转介绍\",\"abbreviation\":\"门诊\",\"date\":\"日期\",\"firstVisitCount\":\"初诊人数\"}},\"audit\":true,\"status\":0}")
            })
    @PostMapping(value = "/clinic/specialProjectWorkload/ratio", name = "公司端报表-报表统计-运营报表-门诊专科占比表")
    public ResponseResult<DynamicHeaderPageInfo<JSONObject>> clinicSpecialProjectWorkloadRatio(
            @RequestBody @Validated MultiClinicDateRangetQueryForm query) throws Exception {
        DynamicHeaderPageInfo<JSONObject> pageInfo = dimesionReportBiz.clinicSpecialProjectWorkloadRatio(query);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 根据条件导出门诊专科工作量占比表
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-门诊业绩&业务报表-专科占比表导出")
    @PostMapping(
            value = "/clinic/specialProjectWorkload/ratio/export",
            name = "公司端报表-报表统计-运营报表-门诊业绩&业务报表-门诊专科占比表导出")
    public ResponseResult<T> clinicSpecialProjectWorkloadRatioExport(
            HttpServletResponse response, @RequestBody @Validated MultiClinicDateRangetQueryForm query)
            throws Exception {
        dimesionReportBiz.clinicSpecialProjectWorkloadRatioExport(query, response);
        return ResponseUtil.success(null);
    }
}
