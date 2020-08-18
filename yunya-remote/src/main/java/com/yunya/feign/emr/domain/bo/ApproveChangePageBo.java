package com.yunya.feign.emr.domain.bo;

import com.google.common.collect.*;
import com.yunya.feign.patient_central.domain.vo.*;
import lombok.*;

import java.util.*;

/**
 * @author xiangyang
 * @date 2020/8/11
 */
@Getter
@Setter
public class ApproveChangePageBo extends ApprovePageBo{

    /**
     * 就诊记录-患者映射 <treatmentId，PatientBaseInfoVo>
     */
    private Map<Integer, PatientBaseInfoVo> trePatientInfoMap;
    /**
     * 就诊记录-就诊映射 <treatmentId，MedicalTreatmentBo>
     */
    private Map<Integer, MedicalTreatmentBo> treTreatmentBoMap;

    public static ApproveChangePageBo getInstance() {
        ApproveChangePageBo bo = new ApproveChangePageBo();
        bo.setAuditList(Lists.newArrayList());
        bo.setPatientInfoMap(Maps.newHashMap());
        bo.setTreatmentBoMap(Maps.newHashMap());
        bo.setTrePatientInfoMap(Maps.newHashMap());
        bo.setTreTreatmentBoMap(Maps.newHashMap());
        return bo;
    }

    @Override
    public void assignMember(Map<Integer, PatientBaseInfoVo> patientInfoMap,
                             Map<Integer, MedicalTreatmentBo> treatmentBoMap) {
        super.assignMember(patientInfoMap, treatmentBoMap);
    }

}
