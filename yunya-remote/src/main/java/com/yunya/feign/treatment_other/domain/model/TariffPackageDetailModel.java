package com.yunya.feign.treatment_other.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/9/4 14:12
 * @description: 项目组合明细
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("项目组合明细")
public class TariffPackageDetailModel implements Serializable {

    /** 项目数量 */
    @ApiModelProperty(value = "项目数量", required = true)
    @NotNull(message = "项目数量不能为空")
    @Min(value = 1, message = "项目数量不能低于1")
    private Integer quantity;

    /** 项目类型：0-价目，1-商品 */
    @ApiModelProperty(value = "项目类型：0-价目，1-商品", required = true)
    @NotNull(message = "项目类型不能为空")
    private Byte itemType;

    /** 项目id */
    @ApiModelProperty(value = "项目id", required = true)
    @NotNull(message = "项目类型不能为空")
    private Integer itemId;

    /** 项目分类名称 */
    @ApiModelProperty(value = "项目分类名称", required = true)
    @NotEmpty(message = "项目分类名称不能为空")
    private String categoryName;
}
