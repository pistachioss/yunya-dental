package com.yunya.report.ultimate.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.query.base.DoubleDateRangeQueryForm;
import com.yunya.feign.report.domain.query.base.MultiClinicDateRangeQueryForm;
import com.yunya.feign.report.domain.vo.CampusAchievementCompareVO;
import com.yunya.feign.report.domain.vo.CardCouponUsedDetailVO;
import com.yunya.feign.report.domain.vo.ClinicAchievementVO;
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
import java.util.concurrent.ExecutionException;

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


    /**
     * 维度报表
     */
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
            @RequestBody @Validated MultiClinicDateRangeQueryForm query) {
        DynamicHeaderPageInfo<JSONObject> pageInfo = dimesionReportBiz.clinicFirstVisitSourceRatio(query);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 根据条件导出门诊初诊来源占比表
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-初诊来源数量分析导出")
    @PostMapping(
            value = "/clinic/firstVisitSource/ratio/export",
            name = "公司端报表-报表统计-运营报表-初诊来源数量分析导出")
    public ResponseResult<T> clinicFirstVisitSourceRatioExport(
            HttpServletResponse response, @RequestBody @Validated MultiClinicDateRangeQueryForm query)
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
            @RequestBody @Validated MultiClinicDateRangeQueryForm query) throws Exception {
        DynamicHeaderPageInfo<JSONObject> pageInfo = dimesionReportBiz.clinicSpecialProjectWorkloadRatio(query);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 根据条件导出门诊专科工作量占比表
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-专科占比表导出")
    @PostMapping(
            value = "/clinic/specialProjectWorkload/ratio/export",
            name = "公司端报表-报表统计-运营报表-门诊专科占比表导出")
    public ResponseResult<T> clinicSpecialProjectWorkloadRatioExport(
            HttpServletResponse response, @RequestBody @Validated MultiClinicDateRangeQueryForm query)
            throws Exception {
        dimesionReportBiz.clinicSpecialProjectWorkloadRatioExport(query, response);
        return ResponseUtil.success(null);
    }

    /**
     * 根据条件查询门诊统计表（实收工作量、初诊人数、复诊人数）
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-门诊统计表")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message =
                                    "响应格式：{\"msg\":\"success\",\"data\":{\"lastPage\":0,\"startRow\":0,\"hasNextPage\":false,\"prePage\":0,\"nextPage\":0,\"endRow\":0,\"pageSize\":0,\"list\":[{\"Wmonth\":1,\"F2021\":0,\"W2021\":0,\"R2021\":0,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":1,\"Rmonth\":1},{\"Wmonth\":2,\"F2021\":0,\"W2021\":0,\"R2021\":0,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":2,\"Rmonth\":2},{\"Wmonth\":3,\"F2021\":0,\"W2021\":0,\"R2021\":0,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":3,\"Rmonth\":3},{\"Wmonth\":4,\"F2021\":0,\"W2021\":0,\"R2021\":0,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":4,\"Rmonth\":4},{\"Wmonth\":5,\"F2021\":0,\"W2021\":0,\"R2021\":0,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":5,\"Rmonth\":5},{\"Wmonth\":6,\"F2021\":0,\"W2021\":0,\"R2021\":0,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":6,\"Rmonth\":6},{\"Wmonth\":7,\"F2021\":0,\"W2021\":0,\"R2021\":0,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":7,\"Rmonth\":7},{\"Wmonth\":8,\"F2021\":0,\"W2021\":0,\"R2021\":0,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":8,\"Rmonth\":8},{\"Wmonth\":9,\"F2021\":0,\"W2021\":0,\"R2021\":0,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":9,\"Rmonth\":9},{\"Wmonth\":10,\"F2021\":0,\"W2021\":0,\"R2021\":0,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":10,\"Rmonth\":10},{\"Wmonth\":11,\"F2021\":0,\"W2021\":0,\"R2021\":0,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":11,\"Rmonth\":11},{\"Wmonth\":12,\"F2021\":1,\"W2021\":2000,\"R2021\":2,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":12,\"Rmonth\":12},{\"Wmonth\":\"总计\",\"F2021\":1,\"W2021\":2000,\"R2021\":2,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":\"总计\",\"Rmonth\":\"总计\"}],\"pageNum\":0,\"navigatePages\":0,\"navigateFirstPage\":0,\"total\":0,\"pages\":0,\"firstPage\":0,\"size\":0,\"isLastPage\":false,\"hasPreviousPage\":false,\"navigateLastPage\":0,\"isFirstPage\":false,\"map\":{\"abbreviation\":\"门诊\",\"Wmonth\":\"月份\",\"W2021\":\"2021\",\"Fmonth\":\"月份\",\"F2021\":\"2021\",\"Rmonth\":\"月份\",\"R2021\":\"2021\"}},\"audit\":true,\"status\":0}; "
                                            + "\n W前缀-工作量； F前缀-初诊人数；R前缀-就诊人数")
            })
    @PostMapping(value = "/clinic/workloadVisit/statistics", name = "公司端报表-报表统计-运营报表-门诊统计表")
    public ResponseResult<DynamicHeaderPageInfo<JSONObject>> clinicWorkloadVisitStatistics(
            @RequestBody @Validated MultiClinicDateRangeQueryForm query) throws Exception {
        DynamicHeaderPageInfo<JSONObject> pageInfo = dimesionReportBiz.clinicWorkloadVisitStatistics(query);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 根据条件导出门诊统计表（实收工作量、初诊人数、复诊人数）
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-门诊统计表导出")
    @PostMapping(
            value = "/clinic/workloadVisit/statistics/export",
            name = "公司端报表-报表统计-运营报表-门诊统计表导出")
    public ResponseResult<T> clinicWorkloadVisitStatisticsExport(
            HttpServletResponse response, @RequestBody @Validated MultiClinicDateRangeQueryForm query)
            throws Exception {
        dimesionReportBiz.clinicWorkloadVisitStatisticsExport(query, response);
        return ResponseUtil.success(null);
    }

    /**
     * 根据条件查询医生统计表（实收工作量、初诊人数、复诊人数）
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-医生统计表")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message =
                                    "响应格式：{\"msg\":\"success\",\"data\":{\"lastPage\":0,\"startRow\":0,\"hasNextPage\":false,\"prePage\":0,\"nextPage\":0,\"endRow\":0,\"pageSize\":0,\"list\":[{\"Wmonth\":1,\"F2021\":0,\"W2021\":0,\"R2021\":0,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":1,\"Rmonth\":1},{\"Wmonth\":2,\"F2021\":0,\"W2021\":0,\"R2021\":0,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":2,\"Rmonth\":2},{\"Wmonth\":3,\"F2021\":0,\"W2021\":0,\"R2021\":0,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":3,\"Rmonth\":3},{\"Wmonth\":4,\"F2021\":0,\"W2021\":0,\"R2021\":0,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":4,\"Rmonth\":4},{\"Wmonth\":5,\"F2021\":0,\"W2021\":0,\"R2021\":0,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":5,\"Rmonth\":5},{\"Wmonth\":6,\"F2021\":0,\"W2021\":0,\"R2021\":0,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":6,\"Rmonth\":6},{\"Wmonth\":7,\"F2021\":0,\"W2021\":0,\"R2021\":0,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":7,\"Rmonth\":7},{\"Wmonth\":8,\"F2021\":0,\"W2021\":0,\"R2021\":0,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":8,\"Rmonth\":8},{\"Wmonth\":9,\"F2021\":0,\"W2021\":0,\"R2021\":0,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":9,\"Rmonth\":9},{\"Wmonth\":10,\"F2021\":0,\"W2021\":0,\"R2021\":0,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":10,\"Rmonth\":10},{\"Wmonth\":11,\"F2021\":0,\"W2021\":0,\"R2021\":0,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":11,\"Rmonth\":11},{\"Wmonth\":12,\"F2021\":1,\"W2021\":2000,\"R2021\":2,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":12,\"Rmonth\":12},{\"Wmonth\":\"总计\",\"F2021\":1,\"W2021\":2000,\"R2021\":2,\"abbreviation\":\"古墩路门诊\",\"Fmonth\":\"总计\",\"Rmonth\":\"总计\"}],\"pageNum\":0,\"navigatePages\":0,\"navigateFirstPage\":0,\"total\":0,\"pages\":0,\"firstPage\":0,\"size\":0,\"isLastPage\":false,\"hasPreviousPage\":false,\"navigateLastPage\":0,\"isFirstPage\":false,\"map\":{\"abbreviation\":\"门诊\",\"Wmonth\":\"月份\",\"W2021\":\"2021\",\"Fmonth\":\"月份\",\"F2021\":\"2021\",\"Rmonth\":\"月份\",\"R2021\":\"2021\"}},\"audit\":true,\"status\":0}; "
                                            + "\n W前缀-工作量； F前缀-初诊人数；R前缀-复诊人数")
            })
    @PostMapping(value = "/dentist/workloadVisit/statistics", name = "公司端报表-报表统计-运营报表-医生统计表")
    public ResponseResult<DynamicHeaderPageInfo<JSONObject>> dentistWorkloadVisitStatistics(
            @RequestBody @Validated EmployeeWorkStatusQueryForm query) throws Exception {
        DynamicHeaderPageInfo<JSONObject> pageInfo = dimesionReportBiz.dentistWorkloadVisitStatistics(query);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 根据条件导出医生统计表（实收工作量、初诊人数、复诊人数）
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-医生统计表导出")
    @PostMapping(
            value = "/dentist/workloadVisit/statistics/export",
            name = "公司端报表-报表统计-运营报表-医生统计表导出")
    public ResponseResult<T> dentistWorkloadVisitStatisticsExport(
            HttpServletResponse response, @RequestBody @Validated EmployeeWorkStatusQueryForm query)
            throws Exception {
        dimesionReportBiz.dentistWorkloadVisitStatisticsExport(query, response);
        return ResponseUtil.success(null);
    }


    /**
     * 根据条件查询每日业绩汇总表
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-每日业绩汇总表")
    @PostMapping(value = "/clinic/achievement/statistics", name = "公司端报表-报表统计-运营报表-每日业绩汇总表")
    public ResponseResult<DynamicHeaderPageInfo<ClinicAchievementVO>> clinicAchievementStatistics(
            @RequestBody @Validated MultiClinicDateRangeQueryForm query) throws Exception {
        DynamicHeaderPageInfo<ClinicAchievementVO> pageInfo = dimesionReportBiz.clinicAchievementStatistics(query);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 根据条件导出每日业绩汇总表
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-每日业绩汇总表导出")
    @PostMapping(value = "/clinic/achievement/statistics/export", name = "公司端报表-报表统计-运营报表-导出每日业绩汇总表")
    public ResponseResult<T> clinicAchievementStatisticsExport(
            HttpServletResponse response, @RequestBody @Validated MultiClinicDateRangeQueryForm query)
            throws Exception {
        dimesionReportBiz.clinicAchievementStatisticsExport(query, response);
        return ResponseUtil.success(null);
    }

    /**
     * 根据条件查询专科数量同比
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-专科数量同比")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message =
                                    "响应格式：{\"status\":0,\"msg\":\"success\",\"data\":{\"total\":0,\"list\":[{\"cmpDate1\":1,\"cmpNum1\":\"100%\",\"abbreviation\":\"古墩路门诊\",\"date1\":1},{\"cmpDate1\":1,\"cmpNum1\":\"100%\",\"abbreviation\":\"合计\",\"date1\":1}],\"pageNum\":0,\"pageSize\":0,\"size\":0,\"startRow\":0,\"endRow\":0,\"pages\":0,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":false,\"isLastPage\":false,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":0,\"navigatepageNums\":null,\"navigateFirstPage\":0,\"navigateLastPage\":0,\"header\":null,\"map\":{\"abbreviation\":\"门诊\",\"date1\":\"2015-2021\",\"cmpDate1\":\"2018-2021\",\"cmpNum1\":\"同比\"},\"contextMap\":{\"洁牙\":[\"date1\",\"cmpDate1\",\"cmpNum1\"]},\"lastPage\":0,\"firstPage\":0},\"audit\":true}; "
                                            + "\n date前缀-第一个日期+专科项目id； cmpDate前缀-第一个日期+专科项目id；cmpNum前缀-专科项目id的同比")
            })
    @PostMapping(value = "/clinic/specialProject/numCompare", name = "公司端报表-报表统计-运营报表-专科数量同比")
    public ResponseResult<DynamicHeaderPageInfo<JSONObject>> clinicSpecialProjectNumCompare(
            @RequestBody @Validated DoubleDateRangeQueryForm query) throws Exception {
        DynamicHeaderPageInfo<JSONObject> pageInfo = dimesionReportBiz.clinicSpecialProjectNumCompare(query);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 根据条件导出专科数量同比
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-专科数量同比导出")
    @PostMapping(value = "/clinic/specialProject/numCompare/export", name = "公司端报表-报表统计-运营报表-专科数量同比导出")
    public ResponseResult<T> clinicSpecialProjectNumCompareExport(
            HttpServletResponse response, @RequestBody @Validated DoubleDateRangeQueryForm query)
            throws Exception {
        dimesionReportBiz.clinicSpecialProjectNumCompareExport(query, response);
        return ResponseUtil.success(null);
    }

    /**
     * 根据条件查询院区业绩汇总表
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-院区业绩汇总表")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message =
                                    "响应格式：{\"status\":0,\"msg\":\"success\",\"data\":{\"total\":5,\"list\":[{\"1\":0,\"firstVisitCount\":0,\"campusName\":\"测试一\",\"treatVisitCount\":0,\"workload\":0,\"nonWorkload\":20}],\"pageNum\":1,\"pageSize\":5,\"size\":5,\"startRow\":0,\"endRow\":4,\"pages\":1,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1,\"header\":null,\"map\":{\"1\":\"检查类\",\"campusName\":\"院区\",\"workload\":\"工作量\",\"nonWorkload\":\"非业绩金额\",\"firstVisitCount\":\"初诊人数\",\"treatVisitCount\":\"就诊人数\"},\"contextMap\":null,\"firstPage\":1,\"lastPage\":1},\"audit\":true}")
            })
    @PostMapping(value = "/campus/achievement/statistics", name = "公司端报表-报表统计-运营报表-院区业绩汇总表")
    public ResponseResult<DynamicHeaderPageInfo<JSONObject>> campusAchievementStatistics(
            @RequestBody @Validated MultiClinicDateRangeQueryForm query) throws Exception {
        DynamicHeaderPageInfo<JSONObject> pageInfo = dimesionReportBiz.campusAchievementStatistics(query);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 根据条件导出院区业绩汇总表
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-院区业绩汇总表导出")
    @PostMapping(value = "/campus/achievement/statistics/export", name = "公司端报表-报表统计-运营报表-院区业绩汇总表导出")
    public ResponseResult<T> campusAchievementStatisticsExport(
            HttpServletResponse response, @RequestBody @Validated MultiClinicDateRangeQueryForm query)
            throws Exception {
        dimesionReportBiz.campusAchievementStatisticsExport(query, response);
        return ResponseUtil.success(null);
    }

    /**
     * 根据条件查询院区业绩同比表
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-院区业绩同比")
    @PostMapping(value = "/campus/achievement/compare", name = "公司端报表-报表统计-运营报表-院区业绩同比表")
    public ResponseResult<PageInfo<CampusAchievementCompareVO>> campusAchievementCompare(
            @RequestBody @Validated MultiClinicDateRangeQueryForm query) throws Exception {
        PageInfo<CampusAchievementCompareVO> pageInfo = dimesionReportBiz.campusAchievementCompare(query);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 根据条件导出院区业绩同比表
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-院区业绩同步表导出")
    @PostMapping(value = "/campus/achievement/compare/export", name = "公司端报表-报表统计-运营报表-院区业绩同步表导出")
    public ResponseResult<T> campusAchievementCompareExport(
            HttpServletResponse response, @RequestBody @Validated MultiClinicDateRangeQueryForm query)
            throws Exception {
        dimesionReportBiz.campusAchievementCompareExport(query, response);
        return ResponseUtil.success(null);
    }

    /**
     * 根据条件产品卡券使用统计
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-产品卡券使用统计")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            code = 200,
                            message =
                                    "响应格式：{\"status\":0,\"msg\":\"success\",\"data\":{\"total\":16,\"list\":[{\"S-443\":1,\"A-443\":1,\"U-443\":0,\"R-443\":0,\"T-443\":\"100.00%\",\"V-443\":\"0.00%\",\"abbreviation\":\"古墩路门诊\"},{\"S-443\":1,\"A-443\":1,\"U-443\":0,\"R-443\":0,\"T-443\":\"100.00%\",\"V-443\":\"0.00%\",\"abbreviation\":\"合计\"}],\"pageNum\":1,\"pageSize\":10,\"size\":10,\"startRow\":1,\"endRow\":10,\"pages\":2,\"prePage\":0,\"nextPage\":2,\"isFirstPage\":true,\"isLastPage\":false,\"hasPreviousPage\":false,\"hasNextPage\":true,\"navigatePages\":8,\"navigatepageNums\":[1,2],\"navigateFirstPage\":1,\"navigateLastPage\":2,\"header\":null,\"map\":{\"abbreviation\":\"门诊\",\"S-443\":\"销售\",\"A-443\":\"激活\",\"U-443\":\"未激活\",\"R-443\":\"复购\",\"T-443\":\"激活率\",\"V-443\":\"复购率\"},\"contextMap\":{\"艾牙周涂氟礼包\":[\"S-443\",\"A-443\",\"U-443\",\"R-443\",\"T-443\",\"V-443\"]},\"firstPage\":1,\"lastPage\":2},\"audit\":true}; "
                                            + "\n S前缀-销售+产品id； A前缀-激活+产品id；U前缀-未激活+产品id；R前缀-复购+产品id；T前缀-激活率+产品id；V前缀-复购率+产品id")
            })
    @PostMapping(value = "/cardCoupon/used/statistics", name = "公司端报表-报表统计-运营报表-产品卡券使用统计")
    public ResponseResult<DynamicHeaderPageInfo<JSONObject>> cardCouponUsedStatistics(
            @RequestBody @Validated CardCouponUsedQueryForm query) throws Exception {
        DynamicHeaderPageInfo<JSONObject> pageInfo = dimesionReportBiz.cardCouponUsedStatistics(query);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 根据条件导出产品卡券使用统计
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-产品卡券使用统计导出")
    @PostMapping(value = "/cardCoupon/used/statistics/export", name = "公司端报表-报表统计-运营报表-产品卡券使用统计导出")
    public ResponseResult<T> cardCouponUsedStatisticsExport(
            HttpServletResponse response, @RequestBody @Validated CardCouponUsedQueryForm query)
            throws Exception {
        dimesionReportBiz.cardCouponUsedStatisticsExport(query, response);
        return ResponseUtil.success(null);
    }

    /**
     * 根据条件查询产品卡券使用统计-激活or复购明细
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-产品卡券使用统计-激活or复购明细")
    @PostMapping(value = "/cardCoupon/used/statistics/detail", name = "公司端报表-报表统计-运营报表-产品卡券使用统计-激活or复购明细")
    public ResponseResult<PageInfo<CardCouponUsedDetailVO>> cardCouponUsedStatisticsDetail(
            @RequestBody @Validated CardCouponUsedDetailQueryForm query) throws Exception {
        PageInfo<CardCouponUsedDetailVO> pageInfo = dimesionReportBiz.cardCouponUsedStatisticsDetail(query);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 根据条件导出产品卡券使用统计-激活/复购明细
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-产品卡券使用统计-激活or复购明细导出")
    @PostMapping(value = "/cardCoupon/used/statistics/detail/export", name = "公司端报表-报表统计-运营报表-产品卡券使用统计-激活or复购明细导出")
    public ResponseResult<T> cardCouponUsedStatisticsDetailExport(
            HttpServletResponse response, @RequestBody @Validated CardCouponUsedDetailQueryForm query)
            throws Exception {
        dimesionReportBiz.cardCouponUsedStatisticsDetailExport(query, response);
        return ResponseUtil.success(null);
    }

    /**
     * 公司端报表-报表统计-365卡购买人数统计表
     *
     * @param query
     * @return
     */
    @ApiOperation("公司端报表-报表统计-365卡购买人数统计表")
    @PostMapping("/cardCoupon/repurchase/statistics")
    public ResponseResult<DynamicHeaderPageInfo<JSONObject>> cardCouponRepurchaseStatistics(
            @RequestBody @Validated ClinicPerformanceBusinessQuery query) {
        DynamicHeaderPageInfo<JSONObject> pageInfo = dimesionReportBiz.cardCouponRepurchaseStatistics(query);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 根据条件导出365卡购买人数统计表
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-365卡购买人数统计表导出")
    @PostMapping(value = "/cardCoupon/repurchase/statistics/export", name = "公司端报表-报表统计-运营报表-365卡购买人数统计导出")
    public ResponseResult<T> cardCouponRepurchaseStatisticsExport(
            HttpServletResponse response, @RequestBody @Validated ClinicPerformanceBusinessQuery query)
            throws Exception {
        dimesionReportBiz.cardCouponRepurchaseStatisticsExport(query, response);
        return ResponseUtil.success(null);
    }

    /**
     * 公司端报表-报表统计-365卡and艾芽卡and就诊人数统计表
     *
     * @param query
     * @return
     */
    @ApiOperation("公司端报表-报表统计-365卡and艾芽卡and就诊人数统计表")
    @PostMapping("/card365/aiya/treatNum")
    public ResponseResult<DynamicHeaderPageInfo<JSONObject>> card365AndAiyaAndTreatNumStatistics(
            @RequestBody @Validated ClinicPerformanceBusinessQuery query) throws Exception {
        DynamicHeaderPageInfo<JSONObject> pageInfo = dimesionReportBiz.card365AndAiyaAndTreatNumStatistics(query);
        return ResponseUtil.success(pageInfo);
    }

    /**
     * 根据条件导出365卡and艾芽卡and就诊人数统计表
     *
     * @param query 查询条件
     * @return
     */
    @ApiOperation("公司端报表-报表统计-运营报表-365卡and艾芽卡and就诊人数统计表导出")
    @PostMapping(value = "/card365/aiya/treatNum/export", name = "公司端报表-报表统计-运营报表-365卡and艾芽卡and就诊人数统计表导出")
    public ResponseResult<T> card365AndAiyaAndTreatNumStatisticsExport(
            HttpServletResponse response, @RequestBody @Validated ClinicPerformanceBusinessQuery query)
            throws Exception {
        dimesionReportBiz.card365AndAiyaAndTreatNumStatisticsExport(query, response);
        return ResponseUtil.success(null);
    }
}
