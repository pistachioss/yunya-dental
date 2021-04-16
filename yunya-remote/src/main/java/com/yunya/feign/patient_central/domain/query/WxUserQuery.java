package com.yunya.feign.patient_central.domain.query;

import lombok.*;

@Data
public class WxUserQuery {
    private String openId;
    private Integer patientId;
}
