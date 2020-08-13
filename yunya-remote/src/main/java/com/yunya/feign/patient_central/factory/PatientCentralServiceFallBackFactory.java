package com.yunya.feign.patient_central.factory;


import com.yunya.feign.patient_central.PatientCentralServiceFeign;
import com.yunya.feign.patient_central.domain.query.PatientLikeFinleQueryForm;
import com.yunya.feign.patient_central.domain.vo.PatientBaseInfoVo;
import com.yunya.feign.patient_central.domain.vo.PatientExtendInfoVo;
import com.yunya.feign.patient_central.domain.vo.PatientTotalInfoVo;
import com.yunya.models.patient_central.PatientBaseInfo;
import com.yunya.models.patient_central.PatientMemberInfo;
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
public class PatientCentralServiceFallBackFactory implements PatientCentralServiceFeign {

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
}
