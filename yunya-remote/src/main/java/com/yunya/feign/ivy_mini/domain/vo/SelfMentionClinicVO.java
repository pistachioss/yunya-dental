package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/16
 * @description:
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "可自提门诊管理")
public class SelfMentionClinicVO {


    private Integer id;

    /**
     * 门诊id
     */
    @ApiModelProperty(value = "门诊id")
    private Integer clinicId;

    @ApiModelProperty(value = "门诊名称")
    private String clinicName;

    @ApiModelProperty(value = "门诊地址")
    private String clinicAddress;

    @ApiModelProperty("门诊图片")
    private String path;
    @ApiModelProperty(value = "距离")
    private Double distance;
    /**
     * 考勤地址经度
     */
    @ApiModelProperty(value = "考勤地址经度")
    private String longitude;

    /**
     * 考勤地址纬度
     */
    @ApiModelProperty(value = "考勤地址纬度")
    private String latitude;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date crtTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updTime;
}
