package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简单介绍:</br> 返回会员卡变更记录信息模型
 *
 * @author: WY
 * @date 2020/8/15 13:47
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("返回会员卡变更记录信息模型")
public class PatientMemberChangeLogVo implements Serializable {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 患者id
     */
    private Integer patientId;

    /**
     * 会员卡卡号
     */
    private String cardNumber;

    /**
     * 会员卡类型名称
     */
    private String memberCardName;

    /**
     * 会员卡类型id
     */
    private Integer memberTypeId;

    /**
     * 诊所ID
     */
    private Integer orgId;

    /**
     * 操作门诊名称
     */
    private String orgName;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 操作人id
     */
    private Integer operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 操作时间
     */
    private Date operatingTime;
    /**
     * 是否启用 是否有效
     */
    private Boolean inservice;

}
