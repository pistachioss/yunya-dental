package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 简单介绍:</br> 返回卡主信息参数模型 主卡信息
 *
 * @author: WY
 * @date 2020/9/11 16:21
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("返回卡主信息参数模型")
public class MasertMemberInfoVo extends MemberTypeBaseVO {

    /**
     *会员卡信息表主键id
     */
    @ApiModelProperty(value = "会员卡信息表主键id")
    private Integer id;

    /**
     *  积分
     */
    @ApiModelProperty(value = "积分")
    private Integer point;

    /**
     * 主卡人id
     */
    @ApiModelProperty(value = "主卡人id")
    private Integer masterCardId;

    /**
     * 主卡人会员卡号
     */
    @ApiModelProperty(value = "主卡人会员卡号")
    private String masterCardNumber;

    /**
     * 主卡人姓名
     */
    @ApiModelProperty(value = "主卡人姓名")
    private String masterName;

    @ApiModelProperty(value = "开卡日期")
    private Date crtTime;
}
