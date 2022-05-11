package com.yunya.feign.emr.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：治疗计划类型明细VO
 *
 * @author: chenlin
 * @Description: 治疗计划类型明细VO
 * @Date: 2022/4/22 13:51
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("治疗计划类型明细VO")
public class TreatPlanTypeDetailVO implements Serializable {
    /** 治疗计划id*/
    @ApiModelProperty("治疗计划id")
    private Integer planId;

    /** 患者id */
    @ApiModelProperty("患者id")
    private Integer patientId;

    /** 患者*/
    @Excel(name = "患者")
    @ApiModelProperty("患者")
    private String patientName;

    /** 手机号"*/
    @Excel(name = "手机号")
    @ApiModelProperty("手机号")
    private String mobile;

    /** 治疗计划名称*/
    @Excel(name = "治疗计划")
    @ApiModelProperty("治疗计划名称")
    private String planName;

    /** 创建日期*/
    @Excel(name = "创建日期", dateFormat = "yyyy-MM-dd")
    @ApiModelProperty("创建日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date crtTime;

    /** 医生id*/
    @ApiModelProperty(value = "医生id")
    private Integer dentistId;

    /** 医生*/
    @Excel(name = "医生")
    @ApiModelProperty(value = "医生")
    private String dentistName;

    /** 治疗状态：0-未确认; 1-已确认; 2-进行中; 3-全部完成; 4-提前终止*/
    @Excel(name = "治疗状态")
    @ApiModelProperty("治疗状态")
    private String status;
}
