package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/7/6
 * @description:
 */
@Data
@ApiModel("就诊人管理VO")
public class WxWechatbindAppListVO {

    @ApiModelProperty("患者ID")
    private Integer patientId;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("标识")
    private String unionId;
    @ApiModelProperty("患者关系名称 用来判断是否为本人 区分微信拥有者")
    private String dictionaryName;
    @ApiModelProperty("手机")
    private String mobile;
    @ApiModelProperty("字典Id")
    private Integer dictionaryId;
    @ApiModelProperty("性别 0-男；1-女；2-未知")
    private Integer gender;
    @ApiModelProperty("绑定时间")
    private Date crtTime;

}
