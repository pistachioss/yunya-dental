package com.yunya.report.ultimate.service;

import com.yunya.feign.report.domain.query.MemberQueryForm;
import com.yunya.feign.report.domain.vo.MemberExpendLogBizVo;
import com.yunya.feign.report.domain.vo.MemberRechargeLogBizVo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.report.BasePatientMemberOccurLog;
import com.yunya.report.ultimate.mapper.BasePatientMemberOccurLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<MemberRechargeLogBizVo> rechargeList(MemberQueryForm memberQueryForm) {
        List<Integer> patientIds = null;
        if (StringHelper.isNotNull(memberQueryForm.getCombination())){
           patientIds = mapper.selectKilePatientId(memberQueryForm.getCombination());
        }
        List<MemberRechargeLogBizVo> memberRechargeLogBizVos = mapper.selectRechargeList(memberQueryForm,patientIds);
        return memberRechargeLogBizVos;
    }

    /**
     * 会员卡消费查询
     * @param memberQueryForm 会员卡充值查询Form
     * @return
     */
    public List<MemberExpendLogBizVo> expendList(MemberQueryForm memberQueryForm) {
        List<Integer> patientIds = null;
        if (StringHelper.isNotNull(memberQueryForm.getCombination())){
            patientIds = mapper.selectKilePatientId(memberQueryForm.getCombination());
        }
        List<MemberExpendLogBizVo> memberExpendLogBizVos = mapper.selecExpendtList(memberQueryForm,patientIds);
        return memberExpendLogBizVos;
    }

    /**  */
}