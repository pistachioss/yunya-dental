package com.yunya.feign.ivy_mini.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * @description:
 * @author: xy
 * @date 2022/5/13 13:15
 **/
@Data
@ApiModel(description = "移除热销产品")
public class RemoveHotForm {
    @ApiModelProperty(value = "产品id集合")
    @NotEmpty
    private Collection<Integer> productIds;
    @ApiModelProperty(value = "产品类型（0-商品 1-虚拟服务）")
    @NotNull
    private Integer type;

}
