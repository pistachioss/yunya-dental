package com.yunya.feign.appointment.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 门诊设备修改（公司端）
 *
 * @author yunya-lihuibin
 * @create 2020-08-04 13:44
 * @update yunya-lihuibin    2020-08-04    新建
 */
@ApiModel(value = "门诊设备修改（公司端）")
@Data
@ToString
public class DeviceEditForm implements Serializable {

    /** 设备id */
    @ApiModelProperty(value = "设备id", required = true)
    @NotNull(message = "设备id不能为空！")
    private Integer deviceId;

    /** 设备名称 */
    @ApiModelProperty(value = "设备名称", required = true)
    @NotNull(message = "设备名称不能为空！")
    private String name;
}
