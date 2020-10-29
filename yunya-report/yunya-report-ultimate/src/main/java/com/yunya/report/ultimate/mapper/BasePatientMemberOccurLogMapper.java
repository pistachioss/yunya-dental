package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.MemberOverviewQueryForm;
import com.yunya.feign.report.domain.query.MemberQueryForm;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.models.report.BasePatientMemberOccurLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PostMapping;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BasePatientMemberOccurLogMapper extends Mapper<BasePatientMemberOccurLog> {

    /**
     * 根据姓名/手机号/拼音姓名
     * @param combination 查询条件
     * @return List<Integer>
     */
    List<Integer> selectKilePatientId(@Param("combination") String combination);

    /**
     * 查询会员卡充值记录列表
     * @param memberQueryForm 充值查询form
     * @param patientIds 患者id集合
     * @return List<MemberRechargeLogBizVo>
     */
    List<BaseMemberRechargeLogVo> selectMemberRechargeList(@Param("form") MemberQueryForm memberQueryForm, @Param("patientIds") List<Integer> patientIds);

    /**
     * 查询会员卡消费记录列表
     * @param memberQueryForm 消费查询form
     * @param patientIds 患者id集合
     * @return List<MemberExpendLogBizVo>
     */
    List<BaseMemberExpendLogVo> selectMemberExpendtList(@Param("form") MemberQueryForm memberQueryForm, @Param("patientIds") List<Integer> patientIds);

    /**
     * 查询会员卡退费记录列表
     * @param memberQueryForm 退费查询form
     * @param patientIds 患者id集合
     * @return List<MemberExpendLogBizVo>
     */
    List<BaseMemberReturnLogVo> selectMemberReturnList(@Param("form") MemberQueryForm memberQueryForm, @Param("patientIds") List<Integer> patientIds);

    /**
     * 查询预付款充值记录列表
     * @param memberQueryForm 充值查询form
     * @param patientIds 患者id集合
     * @return List<PrepaidRechargeLogBizVo>
     */
    List<BasePrepaidRechargeLogVo> selectPrepaidRechargeList(@Param("form") MemberQueryForm memberQueryForm, @Param("patientIds") List<Integer> patientIds);

    /**
     * 查询预付款消费记录列表
     * @param memberQueryForm 消费查询form
     * @param patientIds 患者id集合
     * @return List<PrepaidExpendLogBizVo>
     */
    List<BasePrepaidExpendLogVo> selectPrepaidExpendList(@Param("form") MemberQueryForm memberQueryForm, @Param("patientIds") List<Integer> patientIds);

    /**
     * 查询会员卡退费记录列表
     * @param memberQueryForm 退费查询form
     * @param patientIds 患者id集合
     * @return List<MemberExpendLogBizVo>
     */
    List<BasePrepaidReturnLogVo> selectPrepaidReturnList(@Param("form") MemberQueryForm memberQueryForm, @Param("patientIds") List<Integer> patientIds);

    /**
     * 会员卡概况
     * @param form 会员卡概况查询条件
     * @param patientIds 患者id
     * @return List<MemberOverviewVo>
     */
    List<BasePatientMemberOverviewVo> selectMemberOverviewList(@Param("form") MemberOverviewQueryForm form, @Param("patientIds") List<Integer> patientIds);
}