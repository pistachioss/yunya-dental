package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;

/**
 * 设备VO
 *
 * @author yunya-lihuibin
 * @create 2020-08-01 10:23
 * @update yunya-lihuibin    2020-08-01    新建
 */
@Data
@ToString
@ApiModel(value = "设备VO类")
public class DeviceVo {

    /** 设备id */
    @ApiModelProperty(value = "设备id")
    private Integer id;

    /**
     * 预约设备类型ID
     */
    @ApiModelProperty(value = "预约设备类型ID")
    private Integer deviceId;

    /**
     * 设备编号
     */
    @ApiModelProperty(value = "设备编号")
    private String number;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    private String name;

    /**
     * 诊所ID
     */
    @ApiModelProperty(value = "诊所ID")
    private Integer orgId;
}
