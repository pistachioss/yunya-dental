package com.yunya.feign.patient_central.domain.vo;

import lombok.Data;

@Data
public class WxFansBindTagVO {


    /**
     * 患者id
     */
    private Integer patientId;

    /**
     * union_id
     */
    private String unionId;

}