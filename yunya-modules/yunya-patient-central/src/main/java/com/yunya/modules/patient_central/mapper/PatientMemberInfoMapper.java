package com.yunya.modules.patient_central.mapper;

import com.yunya.feign.patient_central.domain.query.MemberExpendRecordQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientMemberInfoQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientMemberRelationQueryForm;
import com.yunya.feign.patient_central.domain.query.CashReceiptOrRefundQuery;
import com.yunya.feign.patient_central.domain.vo.app.MasertMemberRechargeRecordDetailVo;
import com.yunya.feign.patient_central.domain.vo.web.*;
import com.yunya.models.patient_central.PatientMemberInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author WY
 */
@Repository
public interface PatientMemberInfoMapper extends Mapper<PatientMemberInfo> {

    List<MasertMemberRechargeRecordDetailVo> findMemberRechargeRecordInfo(MemberExpendRecordQueryForm form);
    List<MasertMemberRechargeRecordDetailVo> findMemberPrepaidRecordInfo(MemberExpendRecordQueryForm form);

    /**
     * 根据患者id查询会员基本信息(会员卡界面基本信息（非全部信息）)
     * @param id 患者id
     * @return MemberBaseInfoVo
     */
    MemberBaseInfoVo findMemberBaseInfo(@Param("id") Integer id);

    /**
     * 查询会员卡关联关系
     * @param form  患者会员卡关联关系
     * @return List<PatientMemberRelationVO>
     */
    List<PatientMemberRelationVo> findMemberBindingRelation(@Param("form") PatientMemberRelationQueryForm form);

    /**
     * 根据门诊id获取病历号后六位
     * @param orgId 门诊id
     * @return String
     */
    String generateCardNumber(@Param("orgId") Integer orgId);

    /**
     * 根据会员卡号查询
     * @param cardNumber 会员卡号
     * @return PatientMemberInfo
     */
    PatientMemberInfo selectOneByCardNumber(@Param("cardNumber") String cardNumber);


    /**
     * 通过会员卡号查询会员卡信息
     * @param memberId  会员卡号
     * @return PatientMemberInfo
     */
    PatientMemberInfo selectCardNumber(@Param("memberCard") String memberId);


    /**
     * 通过会员卡号查询患者姓名
     * @param memberId  会员卡号
     * @return PatientMemberInfo
     */
    String selectPatientNameByCardNumber2(@Param("memberCard") String memberId);

    /**
     * 通过患者id查询会员卡全部信息
     * @param patientId 患者id
     * @return PatientMemberInfo
     */
    PatientMemberInfo selectOneByPatientId(@Param("patientId") Integer patientId);

    /**
     * 查询主卡人信息
     * @param form 查询患者会员信息form
     * @return MasertMemberInfoVo
     */
    MasertMemberInfoVo selectMasertMemberInfo(@Param("form") PatientMemberInfoQueryForm form);

    /**
     * 根据患者id查询患者可用会员卡列表
     * @param patientId 患者ID
     * @return MemberBaseInfoVo
     */
    List<MemberBaseInfoVo> selectMemberRelationByMasterPatientId(@Param("patientId") Integer patientId);

    /**
     * 查询预付款、会员卡现金充值
     * @param query
     * @return BigDecimal
     */
    BigDecimal sumMemberAndPrepayRechargeCash(@Param("query") CashReceiptOrRefundQuery query);

    /**
     * 查询某支付方式预付款、会员卡现金退款金额
     *
     * @param query
     * @return BigDecimal
     */
    BigDecimal sumMemberAndPrepaidRefundCash(@Param("query") CashReceiptOrRefundQuery query);

    /**
     * 根据订单记录id查询会员卡or预付款账户的账单消费列表
     *
     * @param orderRecordId
     * @return
     */
    List<PatientDepositAccountVO> selectDepositAccountBillExpendList(@Param("orderRecordId") Integer orderRecordId);

    PatientCumulativeInfoVO selectPatientCumulativeTotalInfo(@Param("patientId") Integer patientId);

    PatientMemberInfo selectPatientBindMemberInfo(@Param("patientId") Integer patientId);

    PatientMemberInfo selectPatientReferrerMemberInfo(@Param("patientId") Integer patientId);
}