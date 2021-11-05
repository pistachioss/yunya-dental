package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介：项目分类金额VO
 *
 * @author: chenlin
 * @Description: 项目分类金额VO
 * @Date: 2021/11/3 17:13
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("门诊项目分类金额VO")
public class ClinicTariffCategoryAmountVO implements Serializable {
    /** 门诊ID */
    @ApiModelProperty("门诊ID")
    private Integer orgId;

    /** 基础分类id */
    @ApiModelProperty("基础分类id")
    private Integer categoryId;

    /** 类型（0-价目表；1-商品；） */
    @ApiModelProperty("类型（0-价目表；1-商品；）")
    private Integer type;

    @ApiModelProperty("金额")
    private BigDecimal amount;
}
