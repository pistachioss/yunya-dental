package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/7/19
 * @description:
 */
@Data
@ApiModel(value = "小程序端待使用产品模型")
public class CardWxVO {
    @ApiModelProperty(value = "优惠券id")
    private Integer couponId;
    @ApiModelProperty(value = "优惠券名称")
    private String couponName;
    @ApiModelProperty(value = "产品类型")
    private Integer couponType;
    @ApiModelProperty(value = "产品有效期 为空代表永久有效")
    private Date activationDeadline;
    @ApiModelProperty(value = "文件名")
    private String fileName;
    @ApiModelProperty(value = "文件地址")
    private String path;

}
