package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介：账单执行项目VO
 *
 * @author: chenlin
 * @Description: 账单执行项目VO
 * @Date: 2021/12/15 11:31
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("账单执行项目VO")
public class BillExecutorItemVO implements Serializable {
    /** 门诊id*/
    @ApiModelProperty("门诊id")
    private Integer orgId;
    /** 账单日期*/
    @ApiModelProperty("账单日期")
    private Integer billDate;
    /** 账单id*/
    @ApiModelProperty("账单id")
    private Integer billId;
    /** 项目执行人id*/
    @ApiModelProperty("项目执行人id")
    private Integer executorId;
    /** 项目类型：0-价目，1商品*/
    @ApiModelProperty("项目类型：0-价目，1商品")
    private Byte itemType;
    /** 项目id*/
    @ApiModelProperty("项目id")
    private Integer itemId;
    /** 项目数量*/
    @ApiModelProperty("项目数量")
    private Integer quantity = 0;
    /** 项目应收工作量*/
    @ApiModelProperty("项目应收工作量")
    private BigDecimal receivableWorkload = new BigDecimal("0.00");
    /** 项目补入工作量*/
    @ApiModelProperty("项目补入工作量")
    private BigDecimal couponWorkload = new BigDecimal("0.00");
    /** 账单总应收工作量*/
    @ApiModelProperty("账单总应收工作量")
    private BigDecimal actualWorkload = new BigDecimal("0.00");
    /** 项目实收工作量*/
    @ApiModelProperty("项目实收工作量")
    private BigDecimal receivedWorkload = new BigDecimal("0.00");
    /** 项目免单工作量*/
    @ApiModelProperty("项目免单工作量")
    private BigDecimal freePaymentWorkload = new BigDecimal("0.00");
    /** 项目退费工作量*/
    @ApiModelProperty("项目退费工作量")
    private BigDecimal refundWorkload = new BigDecimal("0.00");
    /** 项目实收工作量*/
    @ApiModelProperty("项目总实收工作量")
    private BigDecimal totalReceivedWorkload = new BigDecimal("0.00");
}
