package com.yunya.feign.patient_central.domain.vo.app;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 患者信息档案vo
 *
 * @author: WY
 * @date: 2020/9/22 15:43
 * @description: 患者信息档案vo
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel(value = "患者档案vo")
public class AppPatientArchivesVo implements Serializable {

    /** 患者id */
    private Integer id;

    /** 患者姓名 */
    private String name;

    /** 患者手机号 */
    private String mobile;

    /** 患者年龄 */
    private Integer age;

    /** 患者性别 */
    private Byte gender;

    /** 患者病历号 */
    private String medicalNumber;

    /** 患者性别头像类型:012345 */
    private Integer patientKind;

    /** 会员卡类型id */
    private Integer memberTypeId;

    /** 会员卡类型名称 */
    private String memberCardName;

    /** 会员卡本金 */
    private BigDecimal principalAmount;

    /** 赠金 */
    private BigDecimal bonusAmount;
}