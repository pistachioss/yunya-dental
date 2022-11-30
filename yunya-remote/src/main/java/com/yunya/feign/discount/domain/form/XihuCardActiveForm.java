package com.yunya.feign.discount.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/11/30
 * @description:
 */
@Data
@ApiModel(value = "西湖益联保平台卡券激活模型")
public class XihuCardActiveForm implements Serializable {

    @ApiModelProperty(value = "优惠券id", required = true)
    @NotNull
    private Integer couponId;
    @ApiModelProperty(value = "销售来源", required = true)
    private Integer saleSourceId;
    @ApiModelProperty(value = "卡号", required = true)
    @NotBlank
    private String thirdCardNumber;
    @ApiModelProperty(value = "售出对象")
    private String soldTarget;
    @ApiModelProperty(value = "售出对象手机号")
    private String soldPhoneNumber;
    @ApiModelProperty(value = "共享人", example = "1,2,3", required = true)
    private String sharerIdStr;

}
