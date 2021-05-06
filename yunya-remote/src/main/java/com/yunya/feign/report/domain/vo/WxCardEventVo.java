package com.yunya.feign.report.domain.vo;

import lombok.Data;

/**
 * @description:
 * @author: xy
 * @date 2021/4/22 11:29
 **/
@Data
public class WxCardEventVo {
    private String cardNumber;
    private String couponName;
    private String activationDeadline;
    private String activeDate;
    private Integer patientId;
}
