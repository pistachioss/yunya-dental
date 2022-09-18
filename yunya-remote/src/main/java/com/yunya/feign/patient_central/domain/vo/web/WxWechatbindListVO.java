package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/20
 * @description:
 */
@Data
public class WxWechatbindListVO {
    @ApiModelProperty("患者ID")
    private Integer patientId;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("患者关系名称 用来判断是否为本人 区分微信拥有者")
    private String dictionaryName;
    @ApiModelProperty("手机")
    private String mobile;
    @ApiModelProperty("字典Id")
    private Integer dictionaryId;
}
