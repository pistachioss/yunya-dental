package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/10/18 13:18
 * @description: 会员卡的基础信息
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("会员卡的基础信息")
public class MemberTypeBaseVO implements Serializable {


    /**
     * 会员卡类型id
     */
    @ApiModelProperty("会员卡类型id")
    private Integer memberTypeId;

    /**
     * 会员卡类型名称
     */
    @ApiModelProperty(value = "会员卡类型名称")
    private String memberCardName;

    /**
     * 折扣率（价目表自动调价的折扣率）
     */
    @ApiModelProperty(value = "折扣率（价目表自动调价的折扣率）")
    private Float rate;

    /**
     * 会员卡图片
     */
    @ApiModelProperty(value = "会员卡图片")
    private String pictureCode;

    /** 是否激活 */
    @ApiModelProperty("是否激活")
    private Boolean inservice;
}
