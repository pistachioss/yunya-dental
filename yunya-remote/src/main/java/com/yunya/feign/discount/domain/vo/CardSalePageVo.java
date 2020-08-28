package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.*;
import lombok.*;

import java.io.*;

/**
 * @author xiangyang
 * @date 2020/8/25
 */
@Getter
@Setter
@ApiModel(value = "产品售卖-查看配给分页模型")
public class CardSalePageVo implements Serializable {
    @ApiModelProperty(value = "卡券id")
    private Integer id;
    @ApiModelProperty(value = "卡号")
    private String cardNumber;
    @ApiModelProperty(value = "卡密")
    private String cardPassword;
    @ApiModelProperty(value = "售出对象")
    private String soldTarget;
    @ApiModelProperty(value = "售出对象手机号")
    private String soldPhoneNumber;
    @ApiModelProperty(value = "售出类型")
    private String soldTypeName;
    @ApiModelProperty(value = "链接")
    private String link;
    @ApiModelProperty(value = "售卖状态")
    private String soldStatusName;
    @ApiModelProperty(value = "是否已收费")
    private String payStatus;
    @ApiModelProperty(value = "线上/线下")
    private String soldWayName;
}
