package com.yunya.feign.emr.domain.bo;

import lombok.*;

import java.time.*;

/**
 * @author xiangyang
 * @date 2020/8/17
 */
@Getter
@Setter
public class MedicalTreatmentBo {

    private Integer treatmentId;
    private String treatmentClinicName;
    private LocalDate treatmentDate;
    private Integer patientId;
    /**
     * 助理医生
     */
    private String assistantDentistName;

    public static MedicalTreatmentBo getInstance() {
        MedicalTreatmentBo bo = new MedicalTreatmentBo();
        return bo;
    }
}
