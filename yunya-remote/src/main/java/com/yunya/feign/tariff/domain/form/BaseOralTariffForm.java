package com.yunya.feign.tariff.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 简介: 商品项目修改参数模型
 *
 * @author: chow
 * @date: 2020/8/3 15:42
 * @description:
 * @since: 1.0.0
 */
@ApiModel("商品项目修改参数模型")
@Data
@ToString
public class BaseOralTariffForm implements Serializable {
    @ApiModelProperty(value = "商品分类分类ID", required = true)
    @NotNull(message = "商品分类分类ID不能为空！")
    private Integer oralTariffCategoryId;

    /** 项目编码 */
    @NotBlank(message = "商品项目编码不能为空")
    @ApiModelProperty(value = "商品项目编码", required = true)
    @Size(min = 6, max = 6, message = "商品项目编码长度必须是6个字符")
    private String itemNumber;

    /** 项目名称 */
    @NotBlank(message = "商品项目名称不能为空")
    @ApiModelProperty(value = "项目名称", required = true)
    private String name;

    /** 项目英文名称 */
    @ApiModelProperty("商品项目英文名称")
    private String englishName;

    /** 单位 */
    @ApiModelProperty(value = "单位", required = true)
    @NotBlank(message = "商品项目单位不能为空！")
    private String unit;

    /** 价格 */
    @ApiModelProperty(value = "价格", required = true)
    @NotNull(message = "商品项目价格不能为空！")
    @Min(value = 0,message = "价格不能小于0！")
    private BigDecimal price;

    /** 是否启用 */
    @ApiModelProperty("是否启用")
    private Boolean inservice;

    /** 门诊商品项目单价列表 */
    @ApiModelProperty(value = "门诊商品项目单价列表", required = true)
    private List<ClinicItemPriceForm> clinicItemPriceForms;
}
