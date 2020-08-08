package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import java.io.Serializable;

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
public class DeviceItemVo implements Serializable {

    /** 设备id */
    @ApiModelProperty(value = "设备id")
    private Integer id;

    /**
     * 诊所ID
     */
    @ApiModelProperty(value = "诊所ID")
    private Integer orgId;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    private String name;

    /**
     * 设备编号
     */
    @ApiModelProperty(value = "设备编号")
    private String number;

    /**
     * 是否启用 是否有效
     */
    @ApiModelProperty(value = "是否启用 是否有效")
    private Boolean inservice;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;

    /**
     * 设备数量
     */
    @ApiModelProperty(value = "设备数量")
    private Integer deviceNum;
}
