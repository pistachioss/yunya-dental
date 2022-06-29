package com.yunya.modules.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：门店店长活码访问VO
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/6/27 16:38
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("门店店长活码访问VO")
public class ClinicLiveCodeVisitVO implements Serializable {
    /** 微信用户id */
    @ApiModelProperty("微信用户id")
    private String openId;

    /** 微信昵称 */
    @ApiModelProperty("微信昵称")
    private String nickName;

    /** 访问时间*/
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @ApiModelProperty("访问时间")
    private Date visitTime;

    /** 访客类型 */
    @ApiModelProperty("访客类型")
    private String visitType;

    /** 所在城市 */
    @ApiModelProperty("所在城市")
    private String city;

    /** IP地址 */
    @ApiModelProperty("IP地址")
    private String ip;

    /** 浏览器 */
    @ApiModelProperty("浏览器")
    private String visitDevice;

    /** 访问时长 */
    @ApiModelProperty("访问时长")
    private String visitDuration;

    /** 意向门诊 */
    @ApiModelProperty("意向门诊")
    private String abbreviation;

    /** 经度 */
    @ApiModelProperty("经度")
    private String longitude;

    /** 纬度 */
    @ApiModelProperty("纬度")
    private String latitude;

    /** 是否首次访问 */
    @ApiModelProperty("是否首次访问")
    private Boolean isFirstVisit;
}
