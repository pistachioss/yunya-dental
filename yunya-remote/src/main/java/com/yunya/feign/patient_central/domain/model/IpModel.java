package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br>
 *
 * @author: WY
 * @date 2020/9/1 17:08
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
public class IpModel implements Serializable {

    /**
     * 硬件ip
     */
    @ApiModelProperty(value = "硬件ip")
    private String ip;

    /**
     * 硬件密码
     */
    @ApiModelProperty(value = "硬件密码")
    private String pass;
}
