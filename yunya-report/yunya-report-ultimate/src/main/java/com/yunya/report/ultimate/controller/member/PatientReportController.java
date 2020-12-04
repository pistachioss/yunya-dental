package com.yunya.report.ultimate.controller.member;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.ArrearsQueryForm;
import com.yunya.feign.report.domain.query.PatientAnalysisQueryForm;
import com.yunya.feign.report.domain.query.PatientReportQueryForm;
import com.yunya.feign.report.domain.vo.AnalysisVo;
import com.yunya.feign.report.domain.vo.ArrearsVo;
import com.yunya.feign.report.domain.vo.BasePatientNotSeenVo;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.report.BaseEmployee;
import com.yunya.report.ultimate.service.PatientReportBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * 简介:患者报表控制层
 *
 * @author: WY
 * @date: 2020/10/27 20:08
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "公司端-运营报表-患者报表")
@RestController
@RequestMapping("patient")
public class PatientReportController {

    /** 注入服务 */
    @Autowired private PatientReportBiz patientReportBiz;


    /**
     * 末次接诊医生
     * @return List<BaseOrganization>
     */
    @ApiOperation("末次接诊医生")
    @GetMapping("/employee/list/{orgId}")
    public ResponseResult<List<BaseEmployee>> employeeList(@PathVariable Integer orgId) {
        return ResponseUtil.success(this.patientReportBiz.employeeList(orgId));
    }


    /**
     * 未复诊预约且未提醒
     * @param patientReportQueryForm 未复诊预约且未提醒form
     * @return List<BasePatientNotSeenVo>
     */
    @ApiOperation("未复诊预约且未提醒")
    @PostMapping("/notSeen/List")
    public ResponseResult<PageInfo<BasePatientNotSeenVo>> notSeenList(@RequestBody PatientReportQueryForm patientReportQueryForm) throws ParseException {
        PageInfo<BasePatientNotSeenVo> basePatientNotSeenVoList = patientReportBiz.notSeenList(patientReportQueryForm);
        if (StringHelper.isNotNull(basePatientNotSeenVoList)){
            return ResponseUtil.success(basePatientNotSeenVoList);
        }
        return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL,"暂无相关数据",basePatientNotSeenVoList);
    }

    /**
     * 导出未复诊预约且未提醒记录列表
     *
     * @param response 响应
     * @param patientReportQueryForm 查询条件
     * @return
     */
    @ApiOperation("导出未复诊预约且未提醒记录列表")
    @PostMapping(value = "/notSeen/export", name = "公司端-运营报表-患者报表-导出")
    public ResponseResult<T> exportNotSeenList(HttpServletResponse response, @RequestBody @Validated PatientReportQueryForm patientReportQueryForm) throws IOException, ParseException {
        patientReportBiz.exportNotSeenList(response,patientReportQueryForm);
        return ResponseUtil.success(null);
    }


    /**
     * 欠费查询
     * @param arrearsQueryForm 欠费查询form
     * @return List<ArrearsVo>
     */
    @ApiOperation("欠费查询")
    @PostMapping("/arrears")
    public ResponseResult<PageInfo<ArrearsVo>> arrears(@RequestBody ArrearsQueryForm arrearsQueryForm) throws ParseException {
        PageInfo<ArrearsVo> arrears = patientReportBiz.arrears(arrearsQueryForm);
        if (StringHelper.isNotNull(arrears)){
            return ResponseUtil.success(arrears);
        }
        return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL,"暂无相关数据",arrears);
    }

    /**
     * 欠费合计
     * @param arrearsQueryForm 欠费合计查询form
     * @return List<ArrearsVo>
     */
    @ApiOperation("欠费合计")
    @PostMapping("/arrearsStatistics")
    public ResponseResult<Map<Object, Object>> arrearsStatistics(@RequestBody ArrearsQueryForm arrearsQueryForm) throws ParseException {
        Map<Object, Object> arrearsStatisticsMap = patientReportBiz.arrearsStatistics(arrearsQueryForm);
        if (StringHelper.isNotNull(arrearsStatisticsMap)){
            return ResponseUtil.success(arrearsStatisticsMap);
        }
        return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL,"暂无相关数据",arrearsStatisticsMap);
    }

    /**
     * 导出欠费查询记录列表
     *
     * @param response 响应
     * @param arrearsQueryForm 查询条件
     * @return
     */
    @ApiOperation("导出欠费查询记录列表")
    @PostMapping(value = "/arrears/export", name = "公司端-运营报表-患者报表-导出")
    public ResponseResult<T> exportArrearsList(HttpServletResponse response, @RequestBody @Validated ArrearsQueryForm arrearsQueryForm) throws IOException, ParseException {
        patientReportBiz.exportArrearsList(response,arrearsQueryForm);
        return ResponseUtil.success(null);
    }


    /**
     * 就诊患者分析
     * @param patientAnalysisQueryForm 就诊患者分析form
     * @return List<ArrearsVo>
     */

    @ApiOperation("就诊患者分析")
    @PostMapping("/analysis")
    public ResponseResult<AnalysisVo> analysis(@RequestBody PatientAnalysisQueryForm patientAnalysisQueryForm) throws ParseException {
        return ResponseUtil.success(patientReportBiz.analysis(patientAnalysisQueryForm));
    }




}