package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * 设备类型视图模型
 *
 * @author yunya-lihuibin
 * @create 2020-08-03 0:16
 * @update yunya-lihuibin    2020-08-03    新建
 */
@ApiModel(value = "设备类型视图模型")
@Data
@ToString
public class DeviceTypeVo implements Serializable {
    /**
     * 设备类型id
     */
    @ApiModelProperty(value = "设备类型id")
    private Integer id;

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

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;

    /**
     * 是否启用 是否有效
     */
    @ApiModelProperty(value = "是否启用 是否有效")
    private Boolean inservice;
}
