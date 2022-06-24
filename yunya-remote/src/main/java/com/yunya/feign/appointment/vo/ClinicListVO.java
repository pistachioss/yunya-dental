package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/6/10
 * @description: 门诊列表
 */
@ApiModel(value = "ClinicListVO",description = "门诊列表返回参数")
@Data
public class ClinicListVO  implements Serializable {

    /** 组织ID */
    @ApiModelProperty(value = "组织ID")
    private Integer id;
    /**
     * 地址
     */
    @ApiModelProperty(value = "地址")
    private String address;
    /** 组织简称 */
    @ApiModelProperty(value = "组织简称")
    private String abbreviation;

    @ApiModelProperty(value = "距离")
    private Double distance;

    /** 营业开始时间*/
    @ApiModelProperty("营业开始时间")
    private String businessStartTime;
    /** 营业结束时间*/
    @ApiModelProperty("营业结束时间")
    private String businessEndTime;
    @ApiModelProperty("门诊图片")
    private String path;
}
