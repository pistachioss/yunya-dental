package com.yunya.feign.appointment.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 修改设备类型参数封装
 *
 * @author yunya-lihuibin
 * @create 2020-08-03 0:07
 * @update yunya-lihuibin    2020-08-03    新建
 */
@ApiModel(value = "修改设备类型参数封装")
@Data
@ToString
public class DeviceTypeForm implements Serializable {

    /**
     * 设备类型id
     */
    @ApiModelProperty(value = "设备类型id", required = true)
    @NotNull(message = "设备类型id不能为空！")
    @Min(message = "设备类型id为正整数!", value = 1)
    private Integer id;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称", required = true)
    @NotNull(message = "设备名称不能为空！")
    private String name;

    /**
     * 诊所ID
     */
    @ApiModelProperty(value = "诊所ID", required = true)
    @NotNull(message = "诊所ID不能为空！")
    @Min(message = "诊所ID为正整数!", value = 1)
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
    private Byte inservice;
}
