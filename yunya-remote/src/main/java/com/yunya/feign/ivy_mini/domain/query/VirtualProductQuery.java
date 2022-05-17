package com.yunya.feign.ivy_mini.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @author: xy
 * @date 2022/5/13 13:15
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "虚拟服务查询参数")
public class VirtualProductQuery extends PageQuery {

    @ApiModelProperty(value = "产品id")
    private Integer productCategoryId;

    @ApiModelProperty(value = "产品类型（0-商品 1-虚拟服务）")
    private Integer productType;

    @ApiModelProperty(value = "查询关键字")
    private String keyword;
}
