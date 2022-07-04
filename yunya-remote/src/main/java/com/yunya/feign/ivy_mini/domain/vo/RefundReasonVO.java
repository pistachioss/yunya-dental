package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 退货原因表
 * </p>
 *
 * @author xiangyang
 * @since 2022-07-04
 */
@Data
@ApiModel(description = "退款原因返回")
public class RefundReasonVO {
    @ApiModelProperty(value = "id")
    private Integer id;
    @ApiModelProperty(value = "退货原因")
    private String name;
}
