package com.yunya.feign.emr.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介：治疗计划步骤明细VO
 *
 * @author: chenlin
 * @Description: 治疗计划步骤明细VO
 * @Date: 2022/1/11 10:37
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("治疗计划步骤明细VO")
public class TreatPlanDetailVO implements Serializable {

    /** 明细id*/
    @ApiModelProperty("明细id")
    private Integer detailId;

    /** 计划id*/
    @ApiModelProperty("计划id")
    private Integer planId;

    /** 步骤id*/
    @ApiModelProperty("步骤id")
    private Integer stepId;

    /** 项目类型：0-价目; 1-商品*/
    @ApiModelProperty(value = "项目类型：0-价目; 1-商品")
    private Byte type;

    /** 项目id*/
    @ApiModelProperty(value = "项目id")
    private Integer billingItemId;

    /** 牙位列表*/
    @ApiModelProperty("牙位列表")
    private String toothBit;

    /** 项目名称*/
    @ApiModelProperty(value = "项目名称")
    private String billingItemName;

    /** 单位*/
    @ApiModelProperty(value = "单位")
    private String unit;

    /** 数量*/
    @ApiModelProperty(value = "数量")
    private Integer quantity;

    /** 可用数量*/
    @ApiModelProperty("可用数量")
    private Integer enableQuantity;

    /** 执行状态：1-未开始; 2-进行中; 3-全部完成; 4-提前终止*/
    @ApiModelProperty(value = "执行状态：1-未开始; 2-进行中; 3-全部完成; 4-提前终止")
    private Byte status;

    /** 单价*/
    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    /** 备注*/
    @ApiModelProperty("备注")
    private String remark;

    /** 核销订单明细id*/
    @ApiModelProperty("")
    private Integer orderDetailId;

    /** 核销数量*/
    @ApiModelProperty("核销数量")
    private Integer writeOffQuantity;
}
