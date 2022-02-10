package com.yunya.feign.emr.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 简介：治疗计划步骤明细添加模型
 *
 * @author: chenlin
 * @Description: 治疗计划步骤明细添加模型
 * @Date: 2022/1/11 10:37
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("治疗计划步骤明细添加模型")
public class TreatPlanDetailModel implements Serializable {

    /** 明细id*/
    @ApiModelProperty("明细id")
    private Integer detailId;

    /** 项目类型：0-价目; 1-商品*/
    @ApiModelProperty(value = "项目类型：0-价目; 1-商品", required = true)
    @NotNull(message = "项目类型不能为空")
    private Byte type;

    /** 项目id*/
    @ApiModelProperty(value = "项目id", required = true)
    @NotNull(message = "项目id不能为空")
    private Integer billingItemId;

    /** 牙位列表*/
    @ApiModelProperty("牙位列表，如果为空请勿传null")
    private String toothBit;

    /** 项目名称*/
    @ApiModelProperty(value = "项目名称", required = true)
    @NotEmpty(message = "项目名称不能为空")
    private String billingItemName;

    /** 单位*/
    @ApiModelProperty(value = "单位")
    private String unit;

    /** 数量*/
    @ApiModelProperty(value = "数量", required = true)
    @NotNull(message = "数量不能为空")
    private Integer quantity;

    /** 单价*/
    @ApiModelProperty(value = "单价", required = true)
    @NotNull(message = "单价不能为空")
    private BigDecimal price;

    /** 备注*/
    @ApiModelProperty("备注，如果为空请勿传null")
    private String remark;

    /** 执行状态：1-未开始; 2-进行中; 3-全部完成; 4-提前终止*/
    private Integer status;

    /** 创建人id*/
    private Integer crtId;

    /** 创建时间*/
    private Date crtTime;
}
