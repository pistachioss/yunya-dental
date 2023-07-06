package com.yunya.feign.patient_central.factory;


import com.yunya.feign.appointment.vo.AppointmentUnDonePatientInfoVO;
import com.yunya.feign.ivy_mini.domain.form.WxSaveFansForm;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.form.UpdPassForm;
import com.yunya.feign.patient_central.domain.model.*;
import com.yunya.feign.patient_central.domain.query.*;
import com.yunya.feign.patient_central.domain.vo.web.*;
import com.yunya.feign.system.vo.ClinicChargeItemVO;
import com.yunya.feign.treatment.domain.vo.TreatmentPatientInfoVO;
import com.yunya.feign.treatment.domain.vo.WaitingPatientInfoVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.models.patient_central.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 简单介绍:</br>患者服务调用降级处理
 *
 * @author: WY
 * @date 2020/8/3 15:35
 * @description:
 * @since: 1.0.0
 */
@Slf4j
@Component
public class RemotePatientCentralServiceFallBackFactory implements RemotePatientCentralServiceFeign {

    @Override
    public void syncUnionId(SyncUnionIdForm form) {

    }

    @Override
    public List<WaitingPatientInfoVO> selectIsBind(List<WaitingPatientInfoVO> form) {
        return null;
    }

    @Override
    public List<AppointmentUnDonePatientInfoVO> selectIsBind2(List<AppointmentUnDonePatientInfoVO> form) {
        return null;
    }

    @Override
    public List<TreatmentPatientInfoVO> selectIsBind3(List<TreatmentPatientInfoVO> form) {
        return null;
    }

    @Override
    public List<PatientBaseInfoVo> findPatientByNameAndMobile(PatientLikeFinleQueryForm patientBaseInfoQueryForm) {
        return null;
    }

    @Override
    public PatientBaseInfo findPatientInfoById(Integer id) {
        return null;
    }

    @Override
    public List<PatientBaseInfoVo> findPatientInfoByIds(List<Integer> ids) {
        return null;
    }

    @Override
    public List<PatientBaseInfoVo> findPatientInfoByIds(List<Integer> ids, boolean hasDied) {
        return null;
    }

    @Override
    public PatientTotalInfoVo findPatientTotalInfo(Integer id) {
        return null;
    }

    @Override
    public List<PatientTotalInfoVo> findPatientTotalInfo(List<Integer> ids) {
        return null;
    }

    @Override
    public List<PatientMemberInfo> findPatientMemberInfo(PatientMemberInfo patientMemberInfo) {
        return null;
    }

    @Override
    public void updatePatientInfo(PatientBaseInfo patientBaseInfo) {

    }

    @Override
    public List<PatientBaseInfo> findPatientInfo(PatientBaseInfo patientBaseInfo) {
        return null;
    }

    @Override
    public List<PatientBaseInfo> findPatientInfoList(PatientBaseInfo patientBaseInfo) {
        return null;
    }

    @Override
    public String findMedicalNumberByOrgId(Integer orgId) {
        return null;
    }

    @Override
    public Integer findMedicalNumberByClinNum(String clinNum) {
        return null;
    }

    @Override
    public ResponseResult recharge(MemberRechargeModel memberRechargeModel) {
        return null;
    }

    @Override
    public ResponseResult expend(MemberExpendRecordModel model) {
        return null;
    }

    @Override
    public ResponseResult recharge(PrepaidRechargeModel memberRechargeModel) {
        return null;
    }

    @Override
    public ResponseResult expend(PrepaidExpendRecordModel model) {
        return null;
    }

    @Override
    public void updPass(UpdPassForm form) {

    }

    @Override
    public MemberInfoVo findMemberInfo(PatientMemberInfoQueryForm form) {
        return null;
    }

    @Override
    public String portNumberGet() {
        return null;
    }

    @Override
    public ResponseResult billRefund(MemberBillRechargeModel memberBillRechargeModel) {
        return null;
    }

    @Override
    public ResponseResult billRefund(PrepaidBillRechargeModel prepaidBillRechargeModel) {
        return null;
    }

    @Override
    public MemberExpendRecord memberPaymentRecordDetail(PaymentRecordDetailQuery query) {
        return null;
    }
    @Override
    public ResponseResult revocationFee(MemberRevocationFeeModel model) {
        return null;
    }

    @Override
    public ResponseResult revocationFee(PrepaidRevocationFeeModel model) {
        return null;
    }

    @Override
    public boolean memberInfoCount(Integer memberTypeId) {
        return true;
    }

    @Override
    public BigDecimal sumMemberAndPrepayRechargeCash(CashReceiptOrRefundQuery query) {
        return null;
    }

    @Override
    public List<PatientTotalInfoVo> findPatientTotalInfo(PatientBaseInfoQueryForm queryForm) {
        return null;
    }

    @Override
    public BigDecimal sumMemberAndPrepaidRefundCash(CashReceiptOrRefundQuery query) {
        return null;
    }

    @Override
    public PrepaidExpendRecord prePaidPaymentRecordDetail(PaymentRecordDetailQuery query){
        return null;
    }

    @Override
    public Integer saveWx(WxFansSaveForm wxFansSaveForm) {
        return null;
    }

    @Override
    public WxFans countRegister(String openId) {
        return null;
    }

    @Override
    public PatientPublicInfoVo findPatientPublicInfoById(Integer id) {
        return null;
    }

    @Override
    public WxFans getWxFans(WxUserQuery query) {
        return null;
    }

    @Override
    public List<WxFansVo> findListByName(WxFanByNameForm wxFanByNameForm) {
        return null;
    }

    @Override
    public List<WxFansDetailVO> findDetail(WxFansDetailForm wxFansDetailForm) {
        return null;
    }

    @Override
    public WxPatientVo getWxPatientInfo(Integer patientId) {
        return null;
    }

    @Override
    public WxFans getWxfansInfo(Integer fansId) {
        return null;
    }

    @Override
    public List<WxCardUseVo> listPatientCardRecord(String cardNumber, Integer type) {
        return null;
    }

    @Override
    public MemberRelationVo findMemberBindingRelation(PatientMemberRelationQueryForm patientMemberRelationQueryForm) {
        return null;
    }

    @Override
    public WxFans getWxPushUser(Integer patientId) {
        return null;
    }

    @Override
    public List<WxFans> listWxPushUser(List<Integer> patientIds) {
        return null;
    }

    @Override
    public Integer countSelfRegistrationPatient(SelfRegistrationPatientQuery patientQuery) {
        return null;
    }

    @Override
    public void saveMiniAuth(WxSaveFansForm form) {

    }

    @Override
    public void saveOrUpdate(WxFans wxFans) {

    }

    public Date getPatientBirthdayCheck(@PathVariable("patientId") Integer patientId) {
        return null;

    }

    @Override
    public ClinicChargeItemVO findDepositAccountList(PatientDepositAccountQueryForm query) {
        return null;
    }

    @Override
    public List<PatientDepositAccountVO> findDepositAccountBillPayExpendList(Integer orderRecordId) {
        return null;
    }
}
