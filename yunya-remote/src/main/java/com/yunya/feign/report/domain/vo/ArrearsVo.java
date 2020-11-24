package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介:欠费查询QueryForm
 *
 * @author: WY
 * @date: 2020/10/28 10:57
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("欠费查询Vo")
public class ArrearsVo implements Serializable {

    /** 账单id */
    @ApiModelProperty(value = "账单id")
    private Integer billId;

    /** 挂号医生 */
    @ApiModelProperty(value = "挂号医生")
    private String employeeName;

    /** 开单日期 */
    @ApiModelProperty(value = "开单日期")
    private String orderDate;

    /** 账单编号 */
    @ApiModelProperty(value = "账单编号")
    private String orderNum;

    /** 患者姓名 */
    @ApiModelProperty(value = "患者姓名")
    private String name;

    /** 手机号 */
    @ApiModelProperty(value = "手机号")
    private String mobile;

    /** 原价合计 */
    @ApiModelProperty(value = "原价合计")
    private BigDecimal originalPrice;

    /** 优惠金额 */
    @ApiModelProperty(value = "优惠金额")
    private BigDecimal privilegeAmount;

    /** 实收金额 */
    @ApiModelProperty(value = "实收金额")
    private BigDecimal actualAmount;

    /** 已收金额 */
    @ApiModelProperty(value = "已收金额")
    private BigDecimal receivedAmount;

    /** 剩余欠费金额 */
    @ApiModelProperty(value = "剩余欠费金额")
    private BigDecimal debtAmount;

}