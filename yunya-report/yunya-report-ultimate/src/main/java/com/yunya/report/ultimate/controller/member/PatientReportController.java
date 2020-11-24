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
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    @PostMapping("/employee/list")
    public ResponseResult<List<BaseEmployee>> employeeList() {
        return ResponseUtil.success(this.patientReportBiz.employeeList());
    }


    /**
     * 未复诊预约且未提醒
     * @param patientReportQueryForm 未复诊预约且未提醒form
     * @return List<BasePatientNotSeenVo>
     */
    @ApiOperation("未复诊预约且未提醒")
    @PostMapping("/notSeen/List")
    public ResponseResult<PageInfo<BasePatientNotSeenVo>> notSeenList(@RequestBody PatientReportQueryForm patientReportQueryForm){
        PageInfo<BasePatientNotSeenVo> basePatientNotSeenVoList = patientReportBiz.notSeenList(patientReportQueryForm);
        if (StringHelper.isNotNull(basePatientNotSeenVoList)){
            return ResponseUtil.success(basePatientNotSeenVoList);
        }
        return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL,"暂无相关数据",basePatientNotSeenVoList);
    }

    /**
     * 欠费查询
     * @param arrearsQueryForm 欠费查询form
     * @return List<ArrearsVo>
     */
    @ApiOperation("欠费查询")
    @PostMapping("/arrears")
    public ResponseResult<PageInfo<ArrearsVo>> arrears(@RequestBody ArrearsQueryForm arrearsQueryForm){
        PageInfo<ArrearsVo> arrears = patientReportBiz.arrears(arrearsQueryForm);
        if (StringHelper.isNotNull(arrears)){
            return ResponseUtil.success(arrears);
        }
        return ResponseUtil.fail(OperationCodeConstants.RETURN_VALUE_ISNULL,"暂无相关数据",arrears);
    }

    /**
     * 就诊患者分析
     * @param patientAnalysisQueryForm 就诊患者分析form
     * @return List<ArrearsVo>
     */

    @ApiOperation("就诊患者分析")
    @PostMapping("/analysis")
    public ResponseResult<AnalysisVo> analysis(@RequestBody PatientAnalysisQueryForm patientAnalysisQueryForm){
        return ResponseUtil.success(patientReportBiz.analysis(patientAnalysisQueryForm));
    }




}