package com.yunya.modules.system.domain.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍:</br> 新增设备model
 *
 * @author: WY
 * @date 2020/9/2 10:37
 * @description:
 * @since: 1.0.0
 */

@Data
@ToString
public class EquipmentInfoModel implements Serializable {

    /**
     * id主键
     */
    @ApiModelProperty(value = "id主键")
    private Integer id;

    /**
     * 设备ip地址
     */
    @ApiModelProperty(value = "设备ip地址",required = true)
    private String ip;

    /**
     * 设备SN号(序列号)
     */
    @ApiModelProperty(value = "设备SN号(序列号)",required = true)
    private String serialNumber;

    /**
     * 设备密码
     */
    @ApiModelProperty(value = "设备密码",required = true)
    private String pass;
}
