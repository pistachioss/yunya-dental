package com.yunya.feign.emr.domain.bo;

import com.google.common.collect.*;
import com.yunya.feign.patient_central.domain.vo.web.PatientBaseInfoVo;
import com.yunya.models.emr.*;
import lombok.*;

import java.util.*;

/**
 * @author xiangyang
 * @date 2020/8/11
 */
@Getter
@Setter
public class ApprovePageBo {

    /**
     * 查询审批结果
     */
    private List<ApprovalRecord> auditList;
    /**
     * 患者映射 <medicalId，PatientBaseInfoVo>
     */
    private Map<Integer, PatientBaseInfoVo> patientInfoMap;
    /**
     * 就诊映射 <medicalId，MedicalTreatmentBo>
     */
    private Map<Integer, MedicalTreatmentBo> treatmentBoMap;

    private long total;

    private int pageNum;

    public static ApprovePageBo getInstance() {
        ApprovePageBo bo = new ApprovePageBo();
        bo.setAuditList(Lists.newArrayList());
        bo.setPatientInfoMap(Maps.newHashMap());
        bo.setTreatmentBoMap(Maps.newHashMap());
        return bo;
    }

    public void assignMember(Map<Integer, PatientBaseInfoVo> patientInfoMap,
                             Map<Integer, MedicalTreatmentBo> treatmentBoMap) {
        this.patientInfoMap.putAll(patientInfoMap);
        this.treatmentBoMap.putAll(treatmentBoMap);
    }
}
