package com.yunya.report.ultimate.service;

import com.github.pagehelper.PageHelper;
import com.yunya.feign.report.domain.query.ArrearsQueryForm;
import com.yunya.feign.report.domain.query.PatientAnalysisQueryForm;
import com.yunya.feign.report.domain.query.PatientReportQueryForm;
import com.yunya.feign.report.domain.vo.ArrearsVo;
import com.yunya.feign.report.domain.vo.BasePatientNotSeenVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.report.BaseBill;
import com.yunya.models.report.BaseEmployee;
import com.yunya.models.report.BasePatient;
import com.yunya.report.ultimate.mapper.BaseBillMapper;
import com.yunya.report.ultimate.mapper.BaseEmployeeMapper;
import com.yunya.report.ultimate.mapper.BasePatientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 简介:患者报表业务层
 *
 * @author: WY
 * @date: 2020/10/27 20:13
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PatientReportBiz extends BaseBiz<BasePatientMapper, BasePatient> {

    /** 员工Mapper */
    @Autowired private BaseEmployeeMapper baseEmployeeMapper;

    /** 患者Mapper */
    @Autowired private BasePatientMapper basePatientMapper;

    @Autowired private BaseBillMapper baseBillMapper;


    /**
     * 查询末诊医生列表
     * @return List<BaseEmployee>
     */
    public List<BaseEmployee> employeeList() {
        List<BaseEmployee> baseEmployees = baseEmployeeMapper.selectAll();
        return baseEmployees;
    }

    /**
     * 患者报表-未复诊预约且未提醒List
     * @param form 条件
     * @return List<BaseBasePatientNotSeenVo>
     */
    public List<BasePatientNotSeenVo> notSeenList(PatientReportQueryForm form) {
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        List<Integer> patientIds = null;
        if (StringHelper.isNotNull(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        List<BasePatientNotSeenVo> basePatientNotSeenVoList = mapper.selectNotSeenList(form,patientIds);
        return basePatientNotSeenVoList;
    }

    /**
     * 欠费查询
     * @param form 欠费查询form
     * @return List<ArrearsVo>
     */
    public List<ArrearsVo> arrears(ArrearsQueryForm form) {
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        List<Integer> patientIds = null;
        if (StringHelper.isNotNull(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        List<ArrearsVo> arrearsVoList = baseBillMapper.arrears(form,patientIds);
        return arrearsVoList;
    }

    /**
     * 就诊患者分析
     * @param patientAnalysisQueryForm 就诊患者分析查询条件
     * @return
     */
    public List<ArrearsVo> analysis(PatientAnalysisQueryForm patientAnalysisQueryForm) {
        return null;
    }
}