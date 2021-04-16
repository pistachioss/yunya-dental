package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.*;
import lombok.*;

/**
 * @description:
 * @author: xy
 * @date 2021/4/15 14:31
 **/
@Data
@ApiModel("会员卡，预付款使用记录返回")
public class WxCardUseVo {
    @ApiModelProperty("时间")
    private String operatingTime;
    @ApiModelProperty("操作人")
    private String operatorName;
    @ApiModelProperty("类型")
    private String operateTypeName;
    @ApiModelProperty("金额")
    private String amount;
}
