package com.yunya.feign.emr.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 简介：治疗计划记录VO
 *
 * @author: chenlin
 * @Description: 治疗计划记录VO
 * @Date: 2022/1/12 13:51
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("治疗计划记录VO")
public class TreatPlanRecordVO implements Serializable {

    /** 治疗计划id*/
    @ApiModelProperty("治疗计划id")
    private Integer planId;

    /** 患者id*/
    @ApiModelProperty("患者id")
    private Integer patientId;

    /** 门诊id*/
    @ApiModelProperty("门诊id")
    private Integer orgId;

    /** 治疗计划类型id*/
    @ApiModelProperty("治疗计划类型id")
    private Integer planTypeId;

    /** 门诊*/
    @ApiModelProperty("门诊")
    private String abbreviation;

    /** 治疗计划名称*/
    @ApiModelProperty("治疗计划名称")
    private String planName;

    /** 概述*/
    @ApiModelProperty(value = "概述")
    private String summary;

    /** 医生id*/
    @ApiModelProperty(value = "医生id")
    private Integer dentistId;

    /** 医生*/
    @ApiModelProperty(value = "医生")
    private String dentistName;

    /** 备注*/
    @ApiModelProperty(value = "备注")
    private String remark;

    /** 总数量*/
    @ApiModelProperty("总数量")
    private Integer totalQuanity = 0;

    /** 总金额*/
    @ApiModelProperty("总金额")
    private BigDecimal totalAmount = new BigDecimal("0.00");

    /** 状态：0-未确认; 1-已确认; 2-进行中; 3-全部完成; 4-提前终止*/
    @ApiModelProperty("状态：0-未确认; 1-已确认; 2-进行中; 3-全部完成; 4-提前终止")
    private Integer status;

    /** 创建人id*/
    @ApiModelProperty("创建人id")
    private Integer crtId;

    /** 创建人*/
    @ApiModelProperty("创建人")
    private String crtName;

    /** 创建时间*/
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date crtTime;

    /** 修改人*/
    @ApiModelProperty("修改人")
    private String uptName;

    /** 更新时间*/
    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date uptTime;

    /** 步骤列表*/
    @ApiModelProperty(value = "步骤列表")
    private List<TreatPlanStepVO> treatPlanSteps;
}
