package com.yunya.feign.ivy_mini.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "二维码")
public class QrCodeVO {
    @ApiModelProperty(value = "卡券二维码状态（0：正常 1：失效 2：过期 3：核销 4:其他错误）")
    private Integer cardQrCodeType;
    @ApiModelProperty(value = "产品名称")
    private String couponName;
    @ApiModelProperty(value = "产品有效期")
    private String activationDeadline;
    @ApiModelProperty(value = "二维码code")
    private String qrCode;
    @ApiModelProperty(value = "其他错误信息")
    private String errorMsg;
}