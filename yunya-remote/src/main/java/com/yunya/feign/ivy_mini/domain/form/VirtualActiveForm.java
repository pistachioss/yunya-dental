package com.yunya.feign.ivy_mini.domain.form;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/11
 * @description:
 */
@Data
public class VirtualActiveForm {
    private Integer cardId;
    private Integer patientId;
    private String orderSn;
    private String patientMobile;
    private LocalDateTime activeDate;
    private Integer activeUserId;
}
