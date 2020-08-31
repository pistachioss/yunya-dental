package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.*;
import lombok.*;

/**
 * @author xiangyang
 * @date 2020/8/27
 */
@Getter
@Setter
@ApiModel(value = "卡券二维码校验返回模型")
public class CardQrCodeVo {
    @ApiModelProperty(value = "卡券二维码状态（0：正常 1：失效 2：过期 3：核销 4:其他错误）")
    private Integer cardQrCodeType;
    @ApiModelProperty(value = "产品名称")
    private String couponName;
    @ApiModelProperty(value = "产品有效期")
    private String activationDeadline;
    @ApiModelProperty(value = "其他错误信息")
    private String errorMsg;
}
