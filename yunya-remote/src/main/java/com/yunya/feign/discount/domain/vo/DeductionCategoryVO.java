package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "划扣分类VO类")
public class DeductionCategoryVO {
    @ApiModelProperty("分类id")
    private Integer id;
    @ApiModelProperty("分类名称")
    private String name;
}
