package com.yunya.report.ultimate.service;

import com.github.pagehelper.PageHelper;
import com.yunya.feign.report.domain.query.MemberQueryForm;
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
    public List<BaseMemberRechargeLogVo> memberRechargeList(MemberQueryForm memberQueryForm) throws ParseException {
        if (memberQueryForm.getWhetherPage()) {
            PageHelper.startPage(memberQueryForm.getPageNum(), memberQueryForm.getPageSize());
        }
        List<Integer> patientIds = null;
        if (StringHelper.isNotNull(memberQueryForm.getCombination())){
           patientIds = basePatientMapper.selectKilePatientId(memberQueryForm.getCombination());
        }
        memberQueryForm.setStartDate(getStartDateTime(memberQueryForm.getStartDate()));
        memberQueryForm.setEndDate(getEndDateTime(memberQueryForm.getEndDate()));
        List<BaseMemberRechargeLogVo> memberRechargeLogBizVos = mapper.selectMemberRechargeList(memberQueryForm,patientIds);
        return memberRechargeLogBizVos;
    }

    /**
     * 预付款消费查询
     * @param memberQueryForm 预付款消费查询Form
     * @return
     */
    public List<BaseMemberExpendLogVo> memberExpendList(MemberQueryForm memberQueryForm) throws ParseException {
        if (memberQueryForm.getWhetherPage()) {
            PageHelper.startPage(memberQueryForm.getPageNum(), memberQueryForm.getPageSize());
        }
        List<Integer> patientIds = null;
        if (StringHelper.isNotNull(memberQueryForm.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(memberQueryForm.getCombination());
        }
        memberQueryForm.setStartDate(getStartDateTime(memberQueryForm.getStartDate()));
        memberQueryForm.setEndDate(getEndDateTime(memberQueryForm.getEndDate()));
        List<BaseMemberExpendLogVo> baseMemberExpendLogVos = mapper.selectMemberExpendtList(memberQueryForm,patientIds);
        return baseMemberExpendLogVos;
    }

    /**
     * 预付款退费查询
     * @param memberQueryForm 预付款退费查询Form
     * @return List<MemberReturnLogBizVo>
     */
    public List<BaseMemberReturnLogVo> memberReturnList(MemberQueryForm memberQueryForm) throws ParseException {
        if (memberQueryForm.getWhetherPage()) {
            PageHelper.startPage(memberQueryForm.getPageNum(), memberQueryForm.getPageSize());
        }
        List<Integer> patientIds = null;
        if (StringHelper.isNotNull(memberQueryForm.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(memberQueryForm.getCombination());
        }
        memberQueryForm.setStartDate(getStartDateTime(memberQueryForm.getStartDate()));
        memberQueryForm.setEndDate(getEndDateTime(memberQueryForm.getEndDate()));
        List<BaseMemberReturnLogVo> baseMemberReturnLogVos = mapper.selectMemberReturnList(memberQueryForm,patientIds);
        return baseMemberReturnLogVos;
    }

    /**
     * 格式化时间
     * @param startDate 开始时间
     * @return Date
     * @throws ParseException
     */
   public Date getStartDateTime(Date startDate) throws ParseException {
       SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
       SimpleDateFormat forMatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       return forMatter.parse(formatter.format(startDate) + " 00:00:00");
   }

    /**
     * 格式化时间
     * @param endDate 结束时间
     * @return Date
     * @throws ParseException
     */
    public Date getEndDateTime(Date endDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat forMatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return forMatter.parse(formatter.format(endDate) + " 23:59:59");
    }


    /**
     * 预付款充值查询
     * @param memberQueryForm 预付款充值form
     * @return List<PrepaidRechargeLogBizVo>
     */
    public List<BasePrepaidRechargeLogVo> prepaidRechargeList(MemberQueryForm memberQueryForm) throws ParseException {
        if (memberQueryForm.getWhetherPage()) {
            PageHelper.startPage(memberQueryForm.getPageNum(), memberQueryForm.getPageSize());
        }
        List<Integer> patientIds = null;
        if (StringHelper.isNotNull(memberQueryForm.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(memberQueryForm.getCombination());
        }
        memberQueryForm.setStartDate(getStartDateTime(memberQueryForm.getStartDate()));
        memberQueryForm.setEndDate(getEndDateTime(memberQueryForm.getEndDate()));
        List<BasePrepaidRechargeLogVo> basePrepaidRechargeLogVoList = mapper.selectPrepaidRechargeList(memberQueryForm,patientIds);
        return basePrepaidRechargeLogVoList;
    }

    /**
     * 预付款消费查询
     * @param memberQueryForm 预付款消费form
     * @return List<PrepaidExpendLogBizVo>
     */
    public List<BasePrepaidExpendLogVo> prepaidExpendList(MemberQueryForm memberQueryForm) throws ParseException {
        if (memberQueryForm.getWhetherPage()) {
            PageHelper.startPage(memberQueryForm.getPageNum(), memberQueryForm.getPageSize());
        }

        List<Integer> patientIds = null;
        if (StringHelper.isNotNull(memberQueryForm.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(memberQueryForm.getCombination());
        }
        memberQueryForm.setStartDate(getStartDateTime(memberQueryForm.getStartDate()));
        memberQueryForm.setEndDate(getEndDateTime(memberQueryForm.getEndDate()));
        List<BasePrepaidExpendLogVo> basePrepaidExpendLogVoList = mapper.selectPrepaidExpendList(memberQueryForm,patientIds);
        return basePrepaidExpendLogVoList;
    }


    /**
     * 预付款退款查询
     * @param memberQueryForm 预付款退款form
     * @return List<PrepaidReturnLogBizVo>
     */
    public List<BasePrepaidReturnLogVo> prepaidReturnList(MemberQueryForm memberQueryForm) throws ParseException {
        if (memberQueryForm.getWhetherPage()) {
            PageHelper.startPage(memberQueryForm.getPageNum(), memberQueryForm.getPageSize());
        }
        List<Integer> patientIds = null;
        if (StringHelper.isNotNull(memberQueryForm.getCombination())){
            patientIds = basePatientMapper.selectKilePatientId(memberQueryForm.getCombination());
        }
        memberQueryForm.setStartDate(getStartDateTime(memberQueryForm.getStartDate()));
        memberQueryForm.setEndDate(getEndDateTime(memberQueryForm.getEndDate()));
        List<BasePrepaidReturnLogVo> basePrepaidReturnLogVoList = mapper.selectPrepaidReturnList(memberQueryForm,patientIds);
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

}