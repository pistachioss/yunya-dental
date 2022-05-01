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

    /** 门诊id*/
    @ApiModelProperty("门诊id")
    private Integer orgId;

    /** 门诊*/
    @Excel(name = "门诊")
    @ApiModelProperty("门诊")
    private String abbreviation;

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
