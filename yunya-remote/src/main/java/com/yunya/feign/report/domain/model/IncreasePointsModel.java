package com.yunya.feign.report.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介:
 *
 * @author: WY
 * @date: 2021/4/25 14:42
 * @description:
 * @since: 1.0.0
 */
@ApiModel(value = "增加积分模型")
@Data
@ToString
public class IncreasePointsModel implements Serializable {

    /** 接口appKey，应用的唯一标识 */
    @ApiModelProperty(value = "接口appKey，应用的唯一标识",required = true)
    @NotNull(message = "接口appKey,不能为空")
    private String appKey;

    /** 用户标识，唯一且不可变 */
    @ApiModelProperty(value = "用户标识，唯一且不可变",required = true)
    @NotNull(message = "用户标识,不能为空")
    private String uid;

    /** 本次兑换增加的积分 */
    @ApiModelProperty(value = "本次兑换增加的积分",required = true)
    @NotNull(message = "本次兑换增加的积分,不能为空")
    private Long credits;

    /** game(游戏), sign(签到),task(pk赛), reSign(补签)。 hdtool(加积分活动)所有类型不区分大小写 */
    @ApiModelProperty(value = "获取积分类型,game(游戏), sign(签到),task(pk赛), reSign(补签)。 hdtool(加积分活动)...等等,所有类型不区分大小写 ",required = true)
    @NotNull(message = "获取积分类型,不能为空")
    private String type;

    /** 兑吧订单号(请记录到数据库中) */
    @ApiModelProperty(value = "兑吧订单号",required = true)
    @NotNull(message = "兑吧订单号,不能为空")
    private String orderNum;

    /** 1970-01-01开始的时间戳，毫秒为单位。 */
    @ApiModelProperty(value = "时间戳",required = true)
    @NotNull(message = "时间戳,不能为空")
    private String timestamp;

    /** 本次增加积分的描述(带中文，请用utf-8进行url解码) */
    @ApiModelProperty(value = "本次增加积分的描述",required = false)
    private String description;

    /** 用户ip，不保证获取到 */
    @ApiModelProperty(value = "用户ip",required = false)
    private String ip;

    /** MD5签名 */
    @ApiModelProperty(value = "MD5签名",required = true)
    @NotNull(message = "MD5签名,不能为空")
    private String sign;
}