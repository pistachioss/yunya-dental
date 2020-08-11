package com.yunya.feign.emr.domain.bo;

import com.google.common.collect.*;
import com.yunya.feign.patient_central.domain.vo.*;
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
     * 患者映射（电子病例id或就诊id）
     */
    private Map<Integer, PatientBaseInfoVo> patientInfoMap;
    /**
     * 就诊数据映射
     */

    public static ApprovePageBo getInstance() {
        ApprovePageBo bo = new ApprovePageBo();
        bo.setAuditList(Lists.newArrayList());
        bo.setPatientInfoMap(Maps.newHashMap());
        return bo;
    }

    public void assignMember(List<ApprovalRecord> auditList, Map<Integer, PatientBaseInfoVo> patientInfoMap) {
        this.auditList.addAll(auditList);
        this.patientInfoMap.putAll(patientInfoMap);
    }
}
