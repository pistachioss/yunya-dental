package com.yunya.report.ultimate.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.ArrearsQueryForm;
import com.yunya.feign.report.domain.query.PatientAnalysisQueryForm;
import com.yunya.feign.report.domain.query.PatientReportQueryForm;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BaseEmployee;
import com.yunya.models.report.BaseOrganization;
import com.yunya.models.report.BasePatient;
import com.yunya.report.ultimate.mapper.BaseBillMapper;
import com.yunya.report.ultimate.mapper.BaseEmployeeMapper;
import com.yunya.report.ultimate.mapper.BaseOrganizationMapper;
import com.yunya.report.ultimate.mapper.BasePatientMapper;
import com.yunya.report.ultimate.utils.DateConversion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
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
    @Resource
    private BaseEmployeeMapper baseEmployeeMapper;

    /** 患者Mapper */
    @Resource private BasePatientMapper basePatientMapper;

    /** 订单mapper */
    @Resource private BaseBillMapper baseBillMapper;

    @Resource private BaseOrganizationMapper baseOrganizationMapper;


    /**
     * 查询末诊医生列表
     * @return List<BaseEmployee>
     */
    public List<BaseEmployee> employeeList(Integer orgId) {
        List<BaseEmployee> baseEmployees = baseEmployeeMapper.selectByOrgId(orgId);
        return baseEmployees;
    }

    /**
     * 患者报表-未复诊预约且未提醒List
     * @param form 条件
     * @return List<BaseBasePatientNotSeenVo>
     */
    public PageInfo<BasePatientNotSeenVo> notSeenList(PatientReportQueryForm form) throws ParseException {
        if (StringHelper.isNotEmpty(form.getEndDate())){
            form.setEndDate(DateConversion.getEndDate(form.getEndDate()));
        }
        List<BasePatientNotSeenVo> basePatientNotSeenVoList = new ArrayList<>();
        List<Integer> patientIds = null;
        if (StringHelper.isNotEmpty(form.getCombination())){
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
     * 导出未复诊预约且未提醒记录列表
     * @param response
     * @param form
     */
    public void exportNotSeenList(HttpServletResponse response, PatientReportQueryForm form) throws ParseException, IOException {
        if (StringHelper.isNotEmpty(form.getEndDate())){
            form.setEndDate(DateConversion.getEndDate(form.getEndDate()));
        }
        List<BasePatientNotSeenVo> basePatientNotSeenVoList = new ArrayList<>();
        List<Integer> patientIds = null;
        if (StringHelper.isNotEmpty(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        if (patientIds == null || patientIds.size() > 0 ){
            basePatientNotSeenVoList = mapper.selectNotSeenList(form,patientIds);
        }
        ExcelUtil<BasePatientNotSeenVo> excelUtil = new ExcelUtil<>(BasePatientNotSeenVo.class);
        if (StringHelper.isNotNull(form.getOrgId())){
            BaseOrganization baseOrganization = new BaseOrganization();
            baseOrganization.setOrgId(form.getOrgId());
            BaseOrganization baseOrganizationv = baseOrganizationMapper.selectOne(baseOrganization);
            if (baseOrganization != null){
                excelUtil.exportExcel(response, basePatientNotSeenVoList, "未复诊预约且未提醒统计表",baseOrganizationv.getAbbreviation()+"未复诊预约且未提醒统计表");
            }
        }else {
            excelUtil.exportExcel(response, basePatientNotSeenVoList, "未复诊预约且未提醒统计表","未复诊预约且未提醒统计表");
        }
    }

    /**
     * 欠费查询
     * @param form 欠费查询form
     * @return List<ArrearsVo>
     */
    public PageInfo<ArrearsVo> arrears(ArrearsQueryForm form) throws ParseException {
        if (StringHelper.isNotEmpty(form.getEndDate())){
            form.setEndDate(DateConversion.getEndDate(form.getEndDate()));
        }
        List<Integer> patientIds = null;
        if (StringHelper.isNotEmpty(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        List<ArrearsVo> arrearsVoList = baseBillMapper.arrears(form,patientIds);
        return new PageInfo<>(arrearsVoList);
    }

    /**
     * 导出欠费查询记录列表
     * @param response
     * @param form
     */
    public void exportArrearsList(HttpServletResponse response, ArrearsQueryForm form) throws ParseException, IOException {
        if (StringHelper.isNotEmpty(form.getEndDate())){
            form.setEndDate(DateConversion.getEndDate(form.getEndDate()));
        }
        List<Integer> patientIds = null;
        if (StringHelper.isNotEmpty(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        List<ArrearsVo> arrearsVoList = baseBillMapper.arrears(form,patientIds);
        ExcelUtil<ArrearsVo> excelUtil = new ExcelUtil<>(ArrearsVo.class);
        if (StringHelper.isNotNull(form.getOrgId())){
            BaseOrganization baseOrganization = new BaseOrganization();
            baseOrganization.setOrgId(form.getOrgId());
            BaseOrganization baseOrganizationv = baseOrganizationMapper.selectOne(baseOrganization);
            if (baseOrganization != null){
                excelUtil.exportExcel(response, arrearsVoList, "账单欠费统计表",baseOrganizationv.getAbbreviation()+"账单欠费统计表");
            }
        }else {
            excelUtil.exportExcel(response, arrearsVoList, "账单欠费统计表","账单欠费统计表");
        }
    }

    /**
     * 就诊患者分析
     * @param form 就诊患者分析查询条件
     * @return
     */
    public AnalysisVo analysis(PatientAnalysisQueryForm form) throws ParseException {
        if (StringHelper.isNotEmpty(form.getEndDate())){
            form.setEndDate(DateConversion.getEndDate(form.getEndDate()));
        }
        AnalysisVo analysisVo = new AnalysisVo();
        // 来源类型比例
        Integer countOriginType =  mapper.selectCountOriginType(form);
        List<AnalysisPatientOriginVo> analysisPatientOriginVoList = mapper.analysis(form,countOriginType);
        if (StringHelper.isNotEmpty(analysisPatientOriginVoList)){
            analysisVo.setAnalysisPatientOriginVoList(analysisPatientOriginVoList);
        }
        // 男女比例
        Integer countGender = mapper.selectCountGender(form);
        List<AnalysisPatientGenderVo> analysisPatientGenderVoList = mapper.selectAnalysisPatientGender(form,countGender);
        if (StringHelper.isNotEmpty(analysisPatientGenderVoList)){
            analysisVo.setAnalysisPatientGenderVoList(analysisPatientGenderVoList);
        }

        List<AnalysisPatientAgeVo> analysisPatientAgeVoList = new ArrayList<>();
        AnalysisPatientAgeVo youngVo = new AnalysisPatientAgeVo();
        Integer count = mapper.selectCountAnalysisAge(form);
        Integer countYoungAge =  mapper.selectAnalysisAge(form,14,0,0);
        String percentageYoung = mapper.calculateAgePercentage(countYoungAge,count);
        youngVo.setAgeBracket("0-14");
        youngVo.setPercentage(percentageYoung);
        analysisPatientAgeVoList.add(youngVo);

        AnalysisPatientAgeVo wrinklyList = new AnalysisPatientAgeVo();
        Integer countWrinkly =  mapper.selectAnalysisAge(form,0,14,60);
        String percentageWrinkly = mapper.calculateAgePercentage(countWrinkly,count);
        wrinklyList.setAgeBracket("14-60");
        wrinklyList.setPercentage(percentageWrinkly);
        analysisPatientAgeVoList.add(wrinklyList);

        AnalysisPatientAgeVo oldPeopleList = new AnalysisPatientAgeVo();
        Integer countOldPeople =  mapper.selectAnalysisAge(form,0,60,999);
        String percentageOldPeople = mapper.calculateAgePercentage(countOldPeople,count);
        oldPeopleList.setAgeBracket("60-999");
        oldPeopleList.setPercentage(percentageOldPeople);
        analysisPatientAgeVoList.add(oldPeopleList);
        analysisVo.setAnalysisPatientAgeVoList(analysisPatientAgeVoList);
        return analysisVo;
    }



}