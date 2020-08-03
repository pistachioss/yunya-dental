package com.yunya.feign.appointment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 查询设备参数封装
 *
 * @author yunya-lihuibin
 * @create 2020-08-02 20:52
 * @update yunya-lihuibin    2020-08-02    新建
 */
@ApiModel(value = "查询设备参数封装")
@Data
@ToString
public class DeviceItemQuery implements Serializable {
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
     * 诊所ID
     */
    @ApiModelProperty(value = "诊所ID")
    private Integer orgId;

    /**
     * 设备类型名称
     */
    @ApiModelProperty(value = "设备类型名称")
    private String name;
}
