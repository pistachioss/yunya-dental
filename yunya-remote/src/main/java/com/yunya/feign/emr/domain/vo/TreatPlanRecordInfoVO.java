package com.yunya.feign.emr.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：治疗计划信息VO
 *
 * @author: chenlin
 * @Description: 治疗计划信息VO
 * @Date: 2022/1/12 15:23
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("治疗计划信息VO")
public class TreatPlanRecordInfoVO implements Serializable {

    /** 治疗计划id*/
    @ApiModelProperty("治疗计划id")
    private Integer planId;

    /** 治疗计划名称*/
    @ApiModelProperty("治疗计划名称")
    private String planName;

    /** 创建日期*/
    @ApiModelProperty("创建日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date crtTime;

    /** 门诊*/
    @ApiModelProperty("门诊")
    private String abbreviation;

    /** 医生*/
    @ApiModelProperty("医生")
    private String dentistName;

    /** 治疗状态：0-未确认，1-已确认，2-进行中，3-已完成，4-提前终止*/
    @ApiModelProperty("治疗状态：0-未确认，1-已确认，2-进行中，3-已完成，4-提前终止")
    private Integer status;
}
