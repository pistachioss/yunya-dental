package com.yunya.report.ultimate.service;

import com.github.pagehelper.PageHelper;
import com.yunya.feign.report.domain.query.MemberQueryForm;
import com.yunya.feign.report.domain.query.PrepaidQueryForm;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.report.BaseOrganization;
import com.yunya.models.report.BasePatientMemberOccurLog;
import com.yunya.report.ultimate.mapper.BaseOrganizationMapper;
import com.yunya.report.ultimate.mapper.BasePatientMapper;
import com.yunya.report.ultimate.mapper.BasePatientMemberOccurLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 简介:会员卡/预付款概况控制层
 *
 * @author: WY
 * @date: 2020/10/24 13:37
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MemberOccurLogBiz extends BaseBiz<BasePatientMemberOccurLogMapper, BasePatientMemberOccurLog> {

    /** 会员/预付款操作日志Mapper */
    @Autowired private BaseOrganizationMapper baseOrganizationMapper;

    /** 患者Mapper */
    @Autowired private BasePatientMapper basePatientMapper;


    /**
     * 预付款充值查询
     * @param memberQueryForm 预付款充值查询Form
     * @return List<MemberRechargeLogBizVo>
     */
    public List<BaseMemberRechargeLogVo> memberRechargeList(MemberQueryForm form) throws ParseException {
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        List<Integer> patientIds = null;
        if (StringHelper.isNotNull(form.getCombination())){
           patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        List<BaseMemberRechargeLogVo> memberRechargeLogBizVos = mapper.selectMemberRechargeList(form,patientIds);
        return memberRechargeLogBizVos;
    }

    /**
     * 预付款消费查询
     * @param memberQueryForm 预付款消费查询Form
     * @return
     */
    public List<BaseMemberExpendLogVo> memberExpendList(MemberQueryForm form) throws ParseException {
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        List<Integer> patientIds = null;
        if (StringHelper.isNotNull(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        List<BaseMemberExpendLogVo> baseMemberExpendLogVos = mapper.selectMemberExpendtList(form,patientIds);
        return baseMemberExpendLogVos;
    }

    /**
     * 预付款退费查询
     * @param memberQueryForm 预付款退费查询Form
     * @return List<MemberReturnLogBizVo>
     */
    public List<BaseMemberReturnLogVo> memberReturnList(MemberQueryForm form) throws ParseException {
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        List<Integer> patientIds = null;
        if (StringHelper.isNotNull(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        List<BaseMemberReturnLogVo> baseMemberReturnLogVos = mapper.selectMemberReturnList(form,patientIds);
        return baseMemberReturnLogVos;
    }


    /**
     * 预付款充值查询
     * @param memberQueryForm 预付款充值form
     * @return List<PrepaidRechargeLogBizVo>
     */
    public List<BasePrepaidRechargeLogVo> prepaidRechargeList(PrepaidQueryForm form) throws ParseException {
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        List<Integer> patientIds = null;
        if (StringHelper.isNotNull(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        List<BasePrepaidRechargeLogVo> basePrepaidRechargeLogVoList = mapper.selectPrepaidRechargeList(form,patientIds);
        return basePrepaidRechargeLogVoList;
    }

    /**
     * 预付款消费查询
     * @param memberQueryForm 预付款消费form
     * @return List<PrepaidExpendLogBizVo>
     */
    public List<BasePrepaidExpendLogVo> prepaidExpendList(PrepaidQueryForm form) throws ParseException {
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }

        List<Integer> patientIds = null;
        if (StringHelper.isNotNull(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        List<BasePrepaidExpendLogVo> basePrepaidExpendLogVoList = mapper.selectPrepaidExpendList(form,patientIds);
        return basePrepaidExpendLogVoList;
    }


    /**
     * 预付款退款查询
     * @param memberQueryForm 预付款退款form
     * @return List<PrepaidReturnLogBizVo>
     */
    public List<BasePrepaidReturnLogVo> prepaidReturnList(PrepaidQueryForm form) throws ParseException {
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        List<Integer> patientIds = null;
        if (StringHelper.isNotNull(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        List<BasePrepaidReturnLogVo> basePrepaidReturnLogVoList = mapper.selectPrepaidReturnList(form,patientIds);
        return basePrepaidReturnLogVoList;
    }

    /**
     * 查询所有门诊信息
     * @return List<BaseOrganization>
     */
    public List<BaseOrganization> orgList() {
        List<BaseOrganization> baseOrganizations = baseOrganizationMapper.selectAll();
        return baseOrganizations;
    }

    /**
     * 查询会员余额结存表
     * @param memberQueryForm 会员余额结存条件
     * @return List<BaseMemberBalanceInfoVo>
     */
    public List<BaseMemberBalanceInfoVo> memberBalanceList(MemberQueryForm form) {
        if (form.getWhetherPage()) {
            PageHelper.startPage(form.getPageNum(), form.getPageSize());
        }
        List<Integer> patientIds = null;
        if (StringHelper.isNotNull(form.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(form.getCombination());
        }
        List<BaseMemberBalanceInfoVo> basePrepaidReturnLogVoList = mapper.selectMemberBalanceList(form,patientIds);
        return basePrepaidReturnLogVoList;
    }
}