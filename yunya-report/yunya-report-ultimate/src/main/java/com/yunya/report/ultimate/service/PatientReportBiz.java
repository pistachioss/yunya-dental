package com.yunya.report.ultimate.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.ArrearsQueryForm;
import com.yunya.feign.report.domain.query.PatientAnalysisQueryForm;
import com.yunya.feign.report.domain.query.PatientReportQueryForm;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.report.BaseEmployee;
import com.yunya.models.report.BasePatient;
import com.yunya.report.ultimate.mapper.BaseBillMapper;
import com.yunya.report.ultimate.mapper.BaseEmployeeMapper;
import com.yunya.report.ultimate.mapper.BasePatientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    /** 订单mapper */
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
    public PageInfo<BasePatientNotSeenVo> notSeenList(PatientReportQueryForm form) {
        List<BasePatientNotSeenVo> basePatientNotSeenVoList = new ArrayList<>();
        List<Integer> patientIds = null;
        if (StringHelper.isNotNull(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        if (patientIds == null || patientIds.size() > 0 ){
            basePatientNotSeenVoList = mapper.selectNotSeenList(form,patientIds);
        }
        return new PageInfo<>(basePatientNotSeenVoList);
    }

    /**
     * 欠费查询
     * @param form 欠费查询form
     * @return List<ArrearsVo>
     */
    public PageInfo<ArrearsVo> arrears(ArrearsQueryForm form) {
        List<Integer> patientIds = null;
        if (StringHelper.isNotNull(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        List<ArrearsVo> arrearsVoList = baseBillMapper.arrears(form,patientIds);
        return new PageInfo<>(arrearsVoList);
    }

    /**
     * 就诊患者分析
     * @param patientAnalysisQueryForm 就诊患者分析查询条件
     * @return
     */
    public AnalysisVo analysis(PatientAnalysisQueryForm patientAnalysisQueryForm) {
        AnalysisVo analysisVo = new AnalysisVo();
        // 来源类型比例
        Integer countOriginType =  mapper.selectCountOriginType(patientAnalysisQueryForm);
        List<AnalysisPatientOriginVo> analysisPatientOriginVoList = mapper.analysis(patientAnalysisQueryForm,countOriginType);
        if (StringHelper.isNotEmpty(analysisPatientOriginVoList)){
            analysisVo.setAnalysisPatientOriginVoList(analysisPatientOriginVoList);
        }
        // 男女比例
        Integer countGender = mapper.selectCountGender(patientAnalysisQueryForm);
        List<AnalysisPatientGenderVo> analysisPatientGenderVoList = mapper.selectAnalysisPatientGender(patientAnalysisQueryForm,countGender);
        if (StringHelper.isNotEmpty(analysisPatientGenderVoList)){
            analysisVo.setAnalysisPatientGenderVoList(analysisPatientGenderVoList);
        }

        List<AnalysisPatientAgeVo> analysisPatientAgeVoList = new ArrayList<>();
        AnalysisPatientAgeVo youngVo = new AnalysisPatientAgeVo();
        Integer count = mapper.selectCountAnalysisAge(patientAnalysisQueryForm);
        Integer countYoungAge =  mapper.selectAnalysisAge(patientAnalysisQueryForm,14,0,0);
        String percentageYoung = mapper.calculateAgePercentage(countYoungAge,count);
        youngVo.setAgeBracket("0-14");
        youngVo.setPercentage(percentageYoung);
        analysisPatientAgeVoList.add(youngVo);

        AnalysisPatientAgeVo wrinklyList = new AnalysisPatientAgeVo();
        Integer countWrinkly =  mapper.selectAnalysisAge(patientAnalysisQueryForm,0,14,60);
        String percentageWrinkly = mapper.calculateAgePercentage(countWrinkly,count);
        wrinklyList.setAgeBracket("14-60");
        wrinklyList.setPercentage(percentageWrinkly);
        analysisPatientAgeVoList.add(wrinklyList);

        AnalysisPatientAgeVo oldPeopleList = new AnalysisPatientAgeVo();
        Integer countOldPeople =  mapper.selectAnalysisAge(patientAnalysisQueryForm,0,60,999);
        String percentageOldPeople = mapper.calculateAgePercentage(countOldPeople,count);
        oldPeopleList.setAgeBracket("60-999");
        oldPeopleList.setPercentage(percentageOldPeople);
        analysisPatientAgeVoList.add(oldPeopleList);
        analysisVo.setAnalysisPatientAgeVoList(analysisPatientAgeVoList);
        return analysisVo;
    }
}