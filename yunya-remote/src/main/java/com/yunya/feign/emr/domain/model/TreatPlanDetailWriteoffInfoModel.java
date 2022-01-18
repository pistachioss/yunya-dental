package com.yunya.feign.emr.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：治疗计划与订单项目核销明细
 *
 * @author: chenlin
 * @Description: 治疗计划与订单项目核销明细
 * @Date: 2022/1/17 16:49
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("治疗计划与订单项目核销明细")
public class TreatPlanDetailWriteoffInfoModel implements Serializable {
    /** 就诊id*/
    @ApiModelProperty(value = "就诊id", required = true)
    @NotNull(message = "就诊id不能为空")
    private Integer treatmentId;

    /** 订单详情id*/
    @ApiModelProperty(value = "订单详情id", required = true)
    @NotNull(message = "订单详情id不能为空")
    private Integer orderDetailId;

    /** 计划详情id*/
    @ApiModelProperty(value = "计划详情id", required = true)
    @NotNull(message = "计划详情id不能为空")
    private Integer planDetailId;

    /** 项目数量*/
    @ApiModelProperty(value = "项目数量", required = true)
    @NotNull(message = "项目数量不能为空")
    private Integer quantity;

    /** 创建人id*/
    @ApiModelProperty(value = "创建人id")
    private Integer crtId;
}
