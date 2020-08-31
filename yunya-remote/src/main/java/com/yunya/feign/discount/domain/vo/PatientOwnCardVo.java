package com.yunya.feign.discount.domain.vo;

import io.swagger.annotations.*;
import lombok.*;

/**
 * @author xiangyang
 * @date 2020/8/31
 */
@Getter
@Setter
@ApiModel(value = "患者自有产品分页模型")
public class PatientOwnCardVo extends PatientCardBaseVo{
    @ApiModelProperty(value = "销售渠道")
    private String saleChannelName;
    @ApiModelProperty(value = "是否可以配置共享人（0：否 1：是）")
    private Integer share;
}
