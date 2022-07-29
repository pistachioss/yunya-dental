package com.yunya.feign.treatment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/7/15
 * @description:
 */
@Data
@ApiModel("小程序消费信息")
public class AppMemberRechargePrepaidFrom {
    @ApiModelProperty("患者id")
     private Integer patientId;
    @ApiModelProperty("会员卡号")
     private String cardNumber;
    @ApiModelProperty("预付款卡号")
     private String prepaymentNumber;

}
