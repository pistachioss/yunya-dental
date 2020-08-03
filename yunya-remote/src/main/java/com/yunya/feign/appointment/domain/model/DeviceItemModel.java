package com.yunya.feign.appointment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 添加设备表单
 *
 * @author yunya-lihuibin
 * @create 2020-08-02 19:24
 * @update yunya-lihuibin    2020-08-02    新建
 */
@ApiModel(value = "添加设备表单")
@Data
@ToString
public class DeviceItemModel implements Serializable {
    /**门诊id*/
    @ApiModelProperty(value = "门诊id", required = true)
    @NotBlank(message = "门诊id为空！")
    private Integer orgId;

    /**公司端设备id*/
    @ApiModelProperty(value = "公司端设备id", required = true)
    @NotBlank(message = "设备id为空！")
    private Integer deviceId;

    /** 设备编号 */
    @ApiModelProperty(value = "设备编号", required = true)
    @NotBlank(message = "设备编号不能为空！")
    private String number;
}
