package com.yunya.feign.sms.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/12/16 13:42
 * @since: 1.0.0
 */
@ApiModel("短信统计响应模型")
@Data
@ToString
public class SmsOrgStatisticsVO implements Serializable {

    /**
     * 主键id
     */
    @ApiModelProperty("主键id")
    private Integer id;

    /**
     * 组织id（门诊或公司）
     */
    @ApiModelProperty("组织id（门诊或公司）")
    private Integer orgId;

    /**
     * 充值总数
     */
    @ApiModelProperty("充值总数")
    private Integer chargeNum;

    /**
     * 充值总金额
     */
    @ApiModelProperty("充值总金额")
    private BigDecimal chargeMoney;

    /**
     * 可用总条数
     */
    @ApiModelProperty("可用总条数")
    private Integer surplusNum;
}
