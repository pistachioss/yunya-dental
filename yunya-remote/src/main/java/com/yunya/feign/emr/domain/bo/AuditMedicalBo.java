package com.yunya.feign.emr.domain.bo;

import com.google.common.collect.*;
import lombok.*;

import java.util.*;

/**
 * @author xiangyang
 * @date 2020/8/17
 */
@Getter
@Setter
public class AuditMedicalBo {
    /**
     * Map<medicalId, patientId>
     */
    private Map<Integer, Integer> patientMap;
    /**
     * Map<medicalId, treatmentId>
     */
    private Map<Integer, Integer> treatmentMap;
    private List<Integer> patientIds;
    private List<Integer> treatmentIds;

    public static AuditMedicalBo getInstance() {
        AuditMedicalBo bo = new AuditMedicalBo();
        bo.setPatientMap(Maps.newHashMap());
        bo.setTreatmentMap(Maps.newHashMap());
        bo.setPatientIds(Lists.newArrayList());
        bo.setTreatmentIds(Lists.newArrayList());
        return bo;
    }

}
