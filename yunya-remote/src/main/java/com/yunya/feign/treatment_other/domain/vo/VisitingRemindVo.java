package com.yunya.feign.treatment_other.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 随访提醒视图
 * @author: LHB
 * @create: 2020-08-21 20:25
 **/
@ApiModel(value = "随访提醒视图")
@Data
@ToString
public class VisitingRemindVo implements Serializable {
    /**
     * 随访提醒记录ID
     */
    @ApiModelProperty(value = "随访提醒记录ID")
    private Integer id;

    /**
     * 诊所ID
     */
    @ApiModelProperty(value = "诊所ID")
    private Integer orgId;

    /**
     * 患者就诊ID
     */
    @ApiModelProperty(value = "患者就诊ID")
    private Integer treatmentId;

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者ID")
    private Integer patientId;

    /**
     * 医生ID 默认为末诊医生
     */
    @ApiModelProperty(value = "医生ID 默认为末诊医生")
    private Integer dentistId;

    /**
     * 提醒日期
     */
    @ApiModelProperty(value = "提醒日期")
    private Date remindDate;

    /**
     * 提醒时间
     */
    @ApiModelProperty(value = "提醒时间")
    private String remindTime;

    /**
     * 提醒内容
     */
    @ApiModelProperty(value = "提醒内容")
    private String remindContent;

    /**
     * 是否启用 0-不启用；1-启用
     */
    @ApiModelProperty(value = "是否启用 0-不启用；1-启用")
    private Boolean inservice;

    /**
     * 创建人姓名
     */
    @ApiModelProperty(value = "创建人姓名")
    private String crtName;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date crtTime;

    /** 医生名字 */
    @ApiModelProperty(value = "医生名字")
    private String dentistName;

    /** 提醒状态 0-待提醒；1-提醒完成 */
    @ApiModelProperty(value = "提醒状态 0-待提醒；1-提醒完成")
    private Boolean status;

    /******************************* 患者信息 ********************************/
    /** 患者姓名 */
    @ApiModelProperty(value = "患者姓名")
    private String patientName;

    /** 拼音名字 */
    @ApiModelProperty(value = "拼音名字")
    private String pinyinName;

    /** 患者病历号 */
    @ApiModelProperty(value = "患者病历号")
    private String medicalNumber;

    /** 患者手机号 */
    @ApiModelProperty(value = "患者手机号")
    private String mobile;

    /** 患者出生日期 */
    @ApiModelProperty(value = "患者出生日期")
    private String birthday;

    /** 性别 */
    @ApiModelProperty(value = "性别")
    private Byte gender;

    /** 年龄 */
    @ApiModelProperty(value = "年龄")
    private Integer age;


    /** 会员图标 */
    @ApiModelProperty(value = "会员图标")
    private Byte memberIcon;

    /** 患者过敏原 */
    @ApiModelProperty(value = "患者过敏原")
    private String allergen;

    /** 欠费总额 */
    @ApiModelProperty(value = "欠费总额")
    private BigDecimal arrears;

    /** 档案备注 */
    @ApiModelProperty(value = "档案备注")
    private String patientRemark;

}
