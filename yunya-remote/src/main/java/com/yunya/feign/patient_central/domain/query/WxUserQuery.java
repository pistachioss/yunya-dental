package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@ApiModel("微信用户查询模型")
public class WxUserQuery {
    /** 用户标识码*/
    @ApiModelProperty("用户标识码")
    private String openId;

    /** 用户标识码*/
    @ApiModelProperty("（新）用户标识码")
    private String unionId;

    /** 患者ID*/
    @ApiModelProperty("患者ID")
    private Integer patientId;
}
