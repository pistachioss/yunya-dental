package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.MemberQueryForm;
import com.yunya.feign.report.domain.query.PrepaidQueryForm;
import com.yunya.feign.report.domain.query.StatementPatientCardRechargeDetailInfoQuery;
import com.yunya.feign.report.domain.query.StatementPatientCardRefundDetailQuery;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.models.report.BasePatientMemberOccurLog;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BasePatientMemberOccurLogMapper extends Mapper<BasePatientMemberOccurLog> {

  /**
   * 根据姓名/手机号/拼音姓名
   *
   * @param combination 查询条件
   * @return List<Integer>
   */
  List<Integer> selectKilePatientId(@Param("combination") String combination);

  /**
   * 查询会员卡充值记录列表
   *
   * @param memberQueryForm 充值查询form
   * @param patientIds 患者id集合
   * @return List<MemberRechargeLogBizVo>
   */
  List<BaseMemberRechargeLogVo> selectMemberRechargeList(
      @Param("form") MemberQueryForm memberQueryForm,
      @Param("patientIds") List<Integer> patientIds);

  /**
   * 查询会员卡消费记录列表
   *
   * @param memberQueryForm 消费查询form
   * @param patientIds 患者id集合
   * @return List<MemberExpendLogBizVo>
   */
  List<BaseMemberExpendLogVo> selectMemberExpendtList(
      @Param("form") MemberQueryForm memberQueryForm,
      @Param("patientIds") List<Integer> patientIds);

  /**
   * 查询会员卡退费记录列表
   *
   * @param memberQueryForm 退费查询form
   * @param patientIds 患者id集合
   * @return List<MemberExpendLogBizVo>
   */
  List<BaseMemberReturnLogVo> selectMemberReturnList(
      @Param("form") MemberQueryForm memberQueryForm,
      @Param("patientIds") List<Integer> patientIds);

  /**
   * 查询预付款充值记录列表
   *
   * @param prepaidQueryForm 充值查询form
   * @param patientIds 患者id集合
   * @return List<PrepaidRechargeLogBizVo>
   */
  List<BasePrepaidRechargeLogVo> selectPrepaidRechargeList(
      @Param("form") PrepaidQueryForm prepaidQueryForm,
      @Param("patientIds") List<Integer> patientIds);

  /**
   * 查询预付款消费记录列表
   *
   * @param prepaidQueryForm 消费查询form
   * @param patientIds 患者id集合
   * @return List<PrepaidExpendLogBizVo>
   */
  List<BasePrepaidExpendLogVo> selectPrepaidExpendList(
      @Param("form") PrepaidQueryForm prepaidQueryForm,
      @Param("patientIds") List<Integer> patientIds);

  /**
   * 查询会员卡退费记录列表
   *
   * @param prepaidQueryForm 退费查询form
   * @param patientIds 患者id集合
   * @return List<MemberExpendLogBizVo>
   */
  List<BasePrepaidReturnLogVo> selectPrepaidReturnList(
      @Param("form") PrepaidQueryForm prepaidQueryForm,
      @Param("patientIds") List<Integer> patientIds);

  /**
   * 查询会员余额结存信息列表
   *
   * @param form 查询条件
   * @param patientIds 患者id集合
   * @return List<BaseMemberBalanceInfoVo>
   */
  List<BaseMemberBalanceInfoVo> selectMemberBalanceList(
      @Param("form") MemberQueryForm form, @Param("patientIds") List<Integer> patientIds);

  /**
   * 根据条件查询患者储值卡（会员卡、预付款卡）充值记录详情
   *
   * @param query 查询条件
   * @return List<StatementPatientCardRechargeDetailVO>
   */
  List<StatementPatientCardRechargeDetailVO> selectPatientCardRechargeDetailList(
      @Param("query") StatementPatientCardRechargeDetailInfoQuery query);

  /**
   * 根据操作记录ID、卡类型查询
   *
   * @param rechargeRecordId 充值记录ID
   * @param cardType 卡类型 0-会员卡；1-预付款
   * @param occurType 操作类型 （1.充值 2.消费 3.退款 4.撤销收费 5.账单退费)
   * @return StatementPaymentVO
   */
  StatementPaymentVO selectStatementPaymentByOperateRecordId(
      @Param("rechargeRecordId") Integer rechargeRecordId,
      @Param("cardType") Byte cardType,
      @Param("occurType") Byte occurType);

  /**
   * 根据条件查询患者储值卡（会员卡、预付款卡）退费记录详情
   *
   * @param query 查询条件
   * @return List<StatementPatientCardRefundDetailVO>
   */
  List<StatementPatientCardRefundDetailVO> selectPatientCardRefundDetailList(
      @Param("query") StatementPatientCardRefundDetailQuery query);
}
