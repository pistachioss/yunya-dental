package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 简单介绍:</br> 返回患者公告信息模型
 *
 * @author: WY
 * @date 2020/7/27 16:19
 * @description: 患者公用字段信息
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("返回患者公告信息模型")
public class PatientPublicInfoVo implements Serializable {
    /**
     * 患者姓名 字符串，长度64
     */
    @ApiModelProperty(value = "患者姓名 字符串，长度64")
    private String name;

    /**
     * 性别 0-男；1-女；2-未知
     */
    @ApiModelProperty(value = "性别 0-男；1-女；2-未知")
    private Byte gender;

    /**
     * 年龄
     */
    @ApiModelProperty(value = "年龄")
    private Integer age;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 手机号码 长度14
     */
    @ApiModelProperty(value = "手机号码 长度14")
    private String mobile;

    /**
     * 患者头像url
     */
    @ApiModelProperty(value = "患者头像url")
    private String faceUrl;

    /**
     * 病历号 患者第一次就诊时生成
     */
    @ApiModelProperty(value = "病历号 患者第一次就诊时生成")
    private String medicalNumber;

    /**
     * 会员卡类型名称
     */
    @ApiModelProperty(value = "会员卡类型名称")
    private String memberCardName;

    /**
     * 未激活时的会员卡名称
     */
    @ApiModelProperty(value = "未激活时的会员卡名称")
    private String memberCardOldName;

    /**
     * 会员本卡总余额（本金+赠金）
     */
    @ApiModelProperty(value = "会员本卡总余额（本金+赠金）")
    private BigDecimal memberCardMoneySum;

    /**
     * 预付款总余额（本金+赠金）
     */
    @ApiModelProperty(value = "预付款总余额（本金+赠金）")
    private BigDecimal prepaymentsMoneySum;

    /**
     * 会员卡本金 充值金额
     */
    @ApiModelProperty(value = "会员卡本金 充值金额")
    private BigDecimal principalAmount;

    /**
     * 会员卡赠金 充值赠送金额
     */
    @ApiModelProperty(value = "会员卡赠金 充值赠送金额")
    private BigDecimal bonusAmount;

    /**
     * 预付款本金
     */
    @ApiModelProperty(value = "预付款本金")
    private BigDecimal prepaymentPrincipal;

    /**
     * 预付款赠金
     */
    @ApiModelProperty(value = "预付款赠金")
    private BigDecimal prepaymentBonus;

    /**
     * 会员卡类型id
     */
    @ApiModelProperty(value = "会员卡类型id")
    private Integer memberTypeId;

    /**
     * 会员卡卡号
     */
    @ApiModelProperty(value = "会员卡卡号")
    private String cardNumber;

    /**
     * 预付款卡号
     */
    @ApiModelProperty(value = "预付款卡号")
    private String prepaymentNumber;

    @ApiModelProperty(value = "积分")
    private Long point = 0L;

    /**
     * 图标
     */
    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "会员卡图片获取码")
    private String pictureCode;
    @ApiModelProperty(value = "开卡时间")
    private Date crtTime;

    @ApiModelProperty(value = "会员卡是否激活、是否退卡")
    private Boolean inservice;

}
