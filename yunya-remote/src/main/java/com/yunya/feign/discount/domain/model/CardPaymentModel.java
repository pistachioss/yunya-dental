package com.yunya.feign.discount.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel("其他入账方式信息参数模型")
@Data
@ToString
public class CardPaymentModel implements Serializable {

    /**
     * 入账方式ID
     */
    @ApiModelProperty(value = "入账方式ID", required = true)
    @NotNull(message = "入账方式ID不能为空！")
    private Integer accountItemId;

    @ApiModelProperty(value = "入账名称", required = true)
    @NotNull(message = "入账方式Name不能为空！")
    private String accountItemName;

    @ApiModelProperty(value = "入账金额", required = true)
    @NotNull(message = "入账金额不能为空！")
    @Min(value = 0, message = "输入金额不能小于0！")
    private BigDecimal amount;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;
}
