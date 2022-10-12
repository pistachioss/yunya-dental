package com.yunya.feign.emr.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class QztSyncItemVO {
    private String entid;
    /**
     * 门诊病历ID
     */
    private String medicalId;
    /**
     * 患者id
     */
    private String uid;
    /**
     * 患者姓名
     */
    private String userName;
    /**
     * 医疗机构ID （全诊医学提供）
     */
    private String institutionId;
    /**
     * 项目明细列表
     */
    private List<QztSyncItemDetailVO> treatmentItem;
}
