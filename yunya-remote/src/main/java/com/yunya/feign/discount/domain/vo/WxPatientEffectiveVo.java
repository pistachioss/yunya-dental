package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.*;
import lombok.*;

/**
 * @author xiangyang
 * @date 2021/4/12
 */
@Data
public class WxPatientEffectiveVo {
    @ApiModelProperty(value = "卡券id")
    private Integer cardId;
    @ApiModelProperty(value = "优惠券id")
    private Integer couponId;
    @ApiModelProperty(value = "优惠券名称")
    private String couponName;
    @ApiModelProperty(value = "卡号")
    private String cardNumber;
    @ApiModelProperty(value = "产品类型")
    private String couponType;
    @ApiModelProperty(value = "有效时间")
    private String effectiveDate;
    @ApiModelProperty(value = "文件类型（0-图片 1-文档）")
    private Integer fileType;
    @ApiModelProperty(value = "文件地址")
    private String path;
}
