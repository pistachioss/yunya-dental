package com.yunya.feign.appointment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 查询设备类型参数封装
 *
 * @author yunya-lihuibin
 * @create 2020-08-03 0:23
 * @update yunya-lihuibin    2020-08-03    新建
 */
@ApiModel(value = "查询设备类型参数封装")
@Data
@ToString
public class DeviceTypeQuery implements Serializable {

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
