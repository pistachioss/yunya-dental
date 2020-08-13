package com.yunya.feign.patient_central.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简单介绍:</br> 患者来访信息Vo
 *
 * @author: WY
 * @date 2020/8/11 20:51
 * @description: 患者来访信息Vo
 * @since: 1.0.0
 */
@Data
@ToString
public class PatientVisitInfoVo implements Serializable {

    /**
     * 患者id
     */
    private Integer patientId;

    /**
     * 患者头像url
     */
    private String faceUrl;

    /**
     * 患者姓名 字符串，长度64
     */
    private String name;

    /**
     * 性别 0-男；1-女；2-未知
     */
    private Byte gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 手机号码 长度14
     */
    private String mobile;

    /**
     * 病历编号
     */
    private String medicalNumber;

    /**
     * 初诊日期
     */

    /**
     * 末诊日期
     */

    /**
     * 初诊医生
     */

    /**
     * 末诊医生
     */

    /**
     * 初诊门诊
     */

    /**
     * 末诊门诊
     */

    /**
     * 会员卡类型id
     */
    private Integer memberTypeId;

    /**
     * 会员卡类型名称
     */
    private String memberCardName;

    /**
     * 患者类型
     */
    private Integer patientKind;

    /**
     * 消费总额
     */
    private BigDecimal expenseSum;

    /** 患者标签 */
    private String labels;


}
