package com.yunya.feign.wechat.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: xy
 * @date 2021/4/27 11:22
 **/
@Data
public class WxAppointConfirmPushVo {
    private Integer patientId;
    private String patientName;
    private LocalDateTime appointDate;
    private Integer appointDuration;
    private Integer orgId;
    private String orgName;
    private String orgAddress;
    private String linkMobile;
    private Integer appointId;
}
