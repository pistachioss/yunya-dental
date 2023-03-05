package com.yunya.feign.emr.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class BjSyncItemVO {
    private String prescriptionId;
    /**
     * 门诊病历ID
     */
    private String medicalRecordId;
    /**
     * 医疗机构ID （全诊医学提供）
     */
    private String organizationCode;
    /**
     * 处方开具日期 yyyy-MM-dd HH:mm:ss
     */
    private String prescriptionTime;
    /**
     * 处方分类中文名称
     */
    private String prescriptionCategory;

    /**
     * 处方下的药品信息
     */
    private List<BjSyncItemDetailVO> drugList;
}
