package com.yunya.feign.ivy_mini.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: xy
 * @date 2022/5/13 13:15
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "商品查询参数")
public class GoodsQuery extends PageQuery {

    @ApiModelProperty(value = "商品分类id", required = true)
    @NotNull(message = "商品分类不能为空")
    private Integer productCategoryId;

    @ApiModelProperty(value = "产品类型（0-商品 1-虚拟服务）")
    private Integer productType;

    @ApiModelProperty(value = "查询关键字")
    private String keyword;
}
