package com.yunya.feign.discount.domain.form;

import io.swagger.annotations.*;
import lombok.*;

import javax.validation.constraints.*;
import java.io.*;

/**
 * @author xiangyang
 * @date 2020/8/29
 */
@Setter
@Getter
@ApiModel(value = "第三方平台卡券激活模型")
public class OtherCardActiveForm implements Serializable {
    @ApiModelProperty(value = "优惠券id", required = true)
    @NotNull
    private Integer couponId;
    @ApiModelProperty(value = "销售渠道", required = true)
    @NotNull
    private Integer saleChannelId;
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
