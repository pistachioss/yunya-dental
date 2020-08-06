package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 设备配置列表（公司端）
 *
 * @author yunya-lihuibin
 * @create 2020-08-04 12:38
 * @update yunya-lihuibin    2020-08-04    新建
 */
@ApiModel(value = "设备配置列表（公司端）")
@Data
@ToString
public class DeviceItemConfigListVo implements Serializable {
    /** 设备id */
    @ApiModelProperty(value = "设备id")
    private Integer id;

    /**
     * 预约设备数量
     */
    @ApiModelProperty(value = "预约设备数量")
    private Integer deviceNumber;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    private String name;
}
