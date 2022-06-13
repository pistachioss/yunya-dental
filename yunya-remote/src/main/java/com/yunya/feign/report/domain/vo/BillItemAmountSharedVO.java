package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介：账单项目金额分摊VO
 *
 * @author: chenlin
 * @Description: 账单项目金额分摊VO
 * @Date: 2022/6/9 11:31
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("账单项目金额分摊VO")
public class BillItemAmountSharedVO implements Serializable {
    /** 门诊id*/
    private Integer orgId;
    /** 账单id*/
    private Integer billId;
    /** 执行人id*/
    private Integer executorId;
    /** 项目应收 */
    private BigDecimal itemActualAmount;
    /** 账单应收 */
    private BigDecimal billActualAmount;
    /** 账单实收 */
    private BigDecimal billReceivedAmount;
    /** 项目类型：0-价目，1-商品*/
    private Byte itemType;
    /** 项目id*/
    private Integer itemId;

    /** 项目已收占比*/
    private BigDecimal itemReceivedRatio;
    /** 价目or商品项目免单已收占比*/
    private BigDecimal itemFreePaymentRatio;
}
