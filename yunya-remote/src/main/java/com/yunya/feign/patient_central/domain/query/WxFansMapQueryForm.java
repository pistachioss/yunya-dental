package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/5/20
 * @description:
 */
@Data
@ApiModel(value = "WxFansWechatQueryForm",description = "微信用户-启用禁用")
public class WxFansMapQueryForm {

    /**
     * 经度
     */
    @ApiModelProperty("经度")
    @NotNull(message = "经度不能为空!")
    private Double longitude;

    /**
     * 纬度
     */
    @ApiModelProperty("纬度")
    @NotNull(message = "纬度不能为空!")
    private Double latitude;

    @ApiModelProperty("半径(米)")
    @NotNull(message = "半径不能为空!")
    private Integer distance;

    @ApiModelProperty("绑定状态0:已绑定患者 1:未绑定患者 不传为全部")
    private Integer isBind;

}
