package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@ApiModel("会员占比VO")
@Data
@ToString
public class VipRateVo implements Serializable {
    @ApiModelProperty("会员级别")
    private String vipLogo;
    @ApiModelProperty("会员级别的数量")
    private Integer vipQty;
}
