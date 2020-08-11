package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 设备授权model
 *
 * @author: WY
 * @date 2020/8/11 14:06
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("设备授权model")
public class PictureModel implements Serializable {

    /**
     * 人员 personGuid
     */
    @ApiModelProperty(value = "人员id personGuid",required = true)
    private String personGuid;

}
