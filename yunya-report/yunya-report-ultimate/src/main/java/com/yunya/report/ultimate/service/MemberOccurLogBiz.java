package com.yunya.report.ultimate.service;

import cn.hutool.core.date.DateTime;
import com.yunya.feign.report.domain.query.MemberQueryForm;
import com.yunya.feign.report.domain.vo.MemberExpendLogBizVo;
import com.yunya.feign.report.domain.vo.MemberRechargeLogBizVo;
import com.yunya.feign.report.domain.vo.MemberReturnLogBizVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.report.BasePatientMemberOccurLog;
import com.yunya.report.ultimate.mapper.BasePatientMemberOccurLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 简介:
 *
 * @author: WY
 * @date: 2020/10/24 13:37
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MemberOccurLogBiz extends BaseBiz<BasePatientMemberOccurLogMapper, BasePatientMemberOccurLog> {

    /**
     * 会员卡充值查询
     * @param memberQueryForm 会员卡充值查询Form
     * @return List<MemberRechargeLogBizVo>
     */
    public List<MemberRechargeLogBizVo> rechargeList(MemberQueryForm memberQueryForm) throws ParseException {
        List<Integer> patientIds = null;
        if (StringHelper.isNotNull(memberQueryForm.getCombination())){
           patientIds = mapper.selectKilePatientId(memberQueryForm.getCombination());
        }
        memberQueryForm.setStartDate(getStartDateTime(memberQueryForm.getStartDate()));
        memberQueryForm.setEndDate(getEndDateTime(memberQueryForm.getEndDate()));
        List<MemberRechargeLogBizVo> memberRechargeLogBizVos = mapper.selectRechargeList(memberQueryForm,patientIds);
        return memberRechargeLogBizVos;
    }

    /**
     * 会员卡消费查询
     * @param memberQueryForm 会员卡消费查询Form
     * @return
     */
    public List<MemberExpendLogBizVo> expendList(MemberQueryForm memberQueryForm) throws ParseException {
        List<Integer> patientIds = null;
        if (StringHelper.isNotNull(memberQueryForm.getCombination())){
            patientIds = mapper.selectKilePatientId(memberQueryForm.getCombination());
        }
        memberQueryForm.setStartDate(getStartDateTime(memberQueryForm.getStartDate()));
        memberQueryForm.setEndDate(getEndDateTime(memberQueryForm.getEndDate()));
        List<MemberExpendLogBizVo> memberExpendLogBizVos = mapper.selecExpendtList(memberQueryForm,patientIds);
        return memberExpendLogBizVos;
    }

    /**
     * 会员卡退费查询
     * @param memberQueryForm 会员卡退费查询Form
     * @return List<MemberReturnLogBizVo>
     */
    public List<MemberReturnLogBizVo> returnList(MemberQueryForm memberQueryForm) throws ParseException {
        List<Integer> patientIds = null;
        if (StringHelper.isNotNull(memberQueryForm.getCombination())){
            patientIds = mapper.selectKilePatientId(memberQueryForm.getCombination());
        }
        memberQueryForm.setStartDate(getStartDateTime(memberQueryForm.getStartDate()));
        memberQueryForm.setEndDate(getEndDateTime(memberQueryForm.getEndDate()));
        List<MemberReturnLogBizVo> memberReturnLogBizVos = mapper.selectReturnList(memberQueryForm,patientIds);
        return memberReturnLogBizVos;
    }

    /**
     * 格式化时间
     * @param startDate
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
     * @param endDate
     * @return Date
     * @throws ParseException
     */
    public Date getEndDateTime(Date endDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat forMatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return forMatter.parse(formatter.format(endDate) + " 23:59:59");
    }


}