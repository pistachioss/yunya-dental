package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author xiangyang
 * @date 2020/9/3
 */
@Getter
@Setter
public class PatientMemberCardVo implements Serializable {
    @ApiModelProperty(value = "会员卡id")
    private Integer memberCardId;
    @ApiModelProperty(value = "会员卡名称")
    private String memberCardName;
    @ApiModelProperty(value = "会员卡卡号")
    private String memberCardNumber;
    @ApiModelProperty(value = "会员卡折扣率")
    private BigDecimal memberCardRate;
    @ApiModelProperty(value = "卡主")
    private String owner;
    @ApiModelProperty(value = "图片路径")
    private String path;


}
