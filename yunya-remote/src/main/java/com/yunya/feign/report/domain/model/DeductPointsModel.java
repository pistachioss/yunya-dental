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
 * @date: 2021/4/25 14:49
 * @description:
 * @since: 1.0.0
 */
@ApiModel(value = "扣除积分Model")
@Data
@ToString
public class DeductPointsModel implements Serializable {
    /** 用户唯一性标识，唯一且不可变 */
    @ApiModelProperty(value = "用户唯一性标识，唯一且不可变",required = true)
    @NotNull(message = "用户唯一性标识,不能为空")
    private String uid;

    /** 本次兑换扣除的积分 */
    @ApiModelProperty(value = "本次兑换扣除的积分",required = true)
    @NotNull(message = "本次兑换扣除的积分,不能为空")
    private Long credits;

    /** 自有商品商品编码(非必须字段) */
    @ApiModelProperty(value = "自有商品商品编码(非必须字段)",required = false)
    private String itemCode;

    /** 接口appKey，应用的唯一标识 */
    @ApiModelProperty(value = "接口appKey，应用的唯一标识",required = true)
    @NotNull(message = "接口appKey，应用的唯一标识,不能为空")
    private String appKey;

    /** 1970-01-01开始的时间戳，毫秒为单位。 */
    @ApiModelProperty(value = "时间戳 毫秒为单位",required = true)
    @NotNull(message = "时间戳,不能为空")
    private String timestamp;

    /** 本次积分消耗的描述(带中文，请用utf-8进行url解码) */
    @ApiModelProperty(value = "本次积分消耗的描述",required = true)
    @NotNull(message = "本次积分消耗的描述,不能为空")
    private String description;

    /** 兑吧订单号(请记录到数据库中) */
    @ApiModelProperty(value = "兑吧订单号",required = true)
    @NotNull(message = "兑吧订单号,不能为空")
    private String orderNum;

    /** 兑换类型：alipay(支付宝), qb(Q币), coupon(优惠券), object(实物), phonebill(话费), phoneflow(流量), virtual(虚拟商品),game(游戏), hdtool(活动抽奖),sign(签到)所有类型不区分大小写 */
    @ApiModelProperty(value = "兑换类型:alipay(支付宝), qb(Q币), coupon(优惠券), object(实物)...等等,所有类型不区分大小写",required = true)
    @NotNull(message = "兑换类型,不能为空")
    private String type;

    /** 兑换商品的市场价值，单位是分，请自行转换单位 */
    @ApiModelProperty(value = "兑换商品的市场价值，单位是分",required = false)
    private Integer facePrice;

    /** 此次兑换实际扣除开发者账户费用，单位为分 */
    @ApiModelProperty(value = "此次兑换实际扣除开发者账户费用，单位为分",required = true)
    @NotNull(message = "此次兑换实际扣除开发者账户费用,不能为空")
    private Integer actualPrice;

    /** 用户ip，不保证获取到 */
    @ApiModelProperty(value = "用户ip",required = false)
    private String ip;

    /** 直冲商品Q币商品，QQ号码回传参数，其他商品不传该参数 */
    @ApiModelProperty(value = "直冲商品Q币商品，QQ号码回传参数，其他商品不传该参数",required = false)
    private String qq;

    /** 直冲类话费商品手机号回传参数，非话费商品不传该参数 */
    @ApiModelProperty(value = "直冲类话费商品手机号回传参数，非话费商品不传该参数",required = false)
    private String phone;

    /** 支付宝充值商品支付宝账号参数回传，非支付宝商品不传该参数 */
    @ApiModelProperty(value = "支付宝充值商品支付宝账号参数回传，非支付宝商品不传该参数",required = false)
    private String alipay;

    /** 是否需要审核(如需在自身系统进行审核处理，请记录下此信息) */
    @ApiModelProperty(value = "是否需要审核",required = false)
    private Boolean waitAudit;

    /** 详情参数，不同的类型，请求时传不同的内容，中间用英文冒号分隔。(支付宝类型带中文，请用utf-8进行解码) 实物商品：返回收货信息(姓名:手机号:省份:城市:区域:街道:详细地址)、支付宝：返回账号信息(支付宝账号:实名)、话费：返回手机号、QB：返回QQ号 */
    @ApiModelProperty(value = "详情参数，不同的类型，请求时传不同的内容，中间用英文冒号分隔。(支付宝类型带中文，请用utf-8进行解码) 实物商品：返回收货信息(姓名:手机号:省份:城市:区域:街道:详细地址)、支付宝：返回账号信息(支付宝账号:实名)、话费：返回手机号、QB：返回QQ号",required = false)
    private String params;

    /** MD5签名 */
    @ApiModelProperty(value = "MD5签名",required = true)
    @NotNull(message = "MD5签名,不能为空")
    private String sign;
}