package com.yunya.feign.report.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介:患者报表-未复诊预约且未提醒Vo
 *
 * @author: WY
 * @date: 2020/10/27 20:36
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
public class BasePatientNotSeenVo implements Serializable {

    /** 末次就诊日期 */
    private String lastVisitDate;

    /** 患者名称 */
    private String name;

    /** 手机号 */
    private String mobile;

    /** 初复诊 */
    private String treatType;

    /** 末次接诊医生 */
    private String employeeName;
}