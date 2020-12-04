package com.yunya.feign.patient_central.factory;


import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.form.UpdPassForm;
import com.yunya.feign.patient_central.domain.model.*;
import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.patient_central.domain.query.PatientMemberInfoQueryForm;
import com.yunya.feign.patient_central.domain.query.PaymentRecordDetailQuery;
import com.yunya.feign.patient_central.domain.vo.web.MemberInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.feign.patient_central.domain.vo.web.PatientTotalInfoVo;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.models.patient_central.MemberExpendRecord;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.PatientMemberInfo;
import com.yunya.models.patient_central.PrepaidExpendRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
    public PatientBaseInfo findPatientInfo(PatientBaseInfo patientBaseInfo) {
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
    public PrepaidExpendRecord prePaidPaymentRecordDetail(PaymentRecordDetailQuery query){
        return null;
    }
}
