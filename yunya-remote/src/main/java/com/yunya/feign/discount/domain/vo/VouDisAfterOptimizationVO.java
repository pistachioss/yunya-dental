package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 简介:
 * <$>
 *
 * @author: 杨柳絮
 * @date: $ $
 * @description:
 * @since: 1.0.0
 * @param: $
 * @return: $
 */
@Data
public class VouDisAfterOptimizationVO {

    @ApiModelProperty("项目id或项目分类id")
    private Integer id;

    /**
     * 优惠券id
     */
    @ApiModelProperty("优惠券id")
    private Integer couponId;

    /**
     * 项目类型  0:基础价目表,1:基础商品表
     */
    @ApiModelProperty("目类型  0:基础价目表,1:基础商品表")
    private Integer type;

    /**
     * 选择范围  0:全选 1:选项目分类 2:选项目明细
     */
    @ApiModelProperty("选择范围  0:全选 1:选项目分类 2:选项目明细")
    private Byte choiceRangType;

    @ApiModelProperty("项目分类编号或项目编号")
    private String itemNum;

    @ApiModelProperty("项目分类名称或项目名称")
    private String itemName;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("单价")
    private BigDecimal UnitPrice;

}
