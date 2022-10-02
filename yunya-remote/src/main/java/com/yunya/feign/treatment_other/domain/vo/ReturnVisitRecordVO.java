package com.yunya.feign.treatment_other.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: chenlin
 * @date: 2022/9/26 16:21
 * @description: 回访记录列表
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("回访记录列表")
public class ReturnVisitRecordVO implements Serializable {

    /** 主键id */
    private Integer id;

    /** 门诊id */
    @ApiModelProperty("门诊id")
    private Integer orgId;

    /** 门诊 */
    @ApiModelProperty("门诊")
    private String abbreviation;

    /** 医生Id */
    @ApiModelProperty("医生Id")
    private Integer dentistId;

    /** 医生 */
    @ApiModelProperty("医生")
    private String dentistName;

    /** 回访日期 */
    @ApiModelProperty("回访日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date returnDate;

    /** 回访原因 */
    @ApiModelProperty("回访原因")
    private String returnReason;

    /** 回访内容 */
    @ApiModelProperty("回访内容")
    private String returnContent;

    /** 回访人 */
    @ApiModelProperty("回访人")
    private String registerName;
}
