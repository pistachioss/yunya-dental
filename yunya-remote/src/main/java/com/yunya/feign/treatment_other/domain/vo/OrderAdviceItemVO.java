package com.yunya.feign.treatment_other.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: chenlin
 * @date: 2023/9/20 10:31
 * @description: 医嘱项目信息数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("医嘱项目信息数据模型")
public class OrderAdviceItemVO implements Serializable {
    /** 平台医嘱流水号 */
    @ApiModelProperty("平台医嘱流水号")
    private String orderNo;

    /** 项目编号 */
    @ApiModelProperty("项目编号")
    private String itemNum;

    /** 医嘱名称 */
    @ApiModelProperty("医嘱名称")
    private String itemName;

    /** 单价 */
    @ApiModelProperty("单价")
    private BigDecimal price;

    /** 数量 */
    @ApiModelProperty("数量")
    private Integer quantity;

    /** 价格 */
    @ApiModelProperty("价格")
    private BigDecimal originAmount;

    /** 实收 */
    @ApiModelProperty("实收")
    private BigDecimal receivedAmount;
    
    /** 是否全程代收 */
    @ApiModelProperty("是否全程代收")
    private Boolean qcCollected;

    /** 是否执行 */
    @ApiModelProperty("是否执行")
    private Boolean isExec;

    /** 执行人id */
    @ApiModelProperty("执行人id")
    private Integer executorId;

    /** 执行人姓名 */
    @ApiModelProperty("执行人姓名")
    private String executorName;

    /** 备注 */
    @ApiModelProperty("备注")
    private String remark;

    /** 订单明细id */
    @ApiModelProperty("订单明细id")
    private Integer orderDetailId;

    /** 单位 */
    @ApiModelProperty("单位")
    private String unitDesc;

    /** 开立医嘱时间 */
    @ApiModelProperty("开立医嘱时间")
    private Date openTime;
}
