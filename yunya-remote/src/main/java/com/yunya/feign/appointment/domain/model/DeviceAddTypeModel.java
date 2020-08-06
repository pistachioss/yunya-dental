package com.yunya.feign.appointment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 新增设备类型（公司端新增设备）
 *
 * @author yunya-lihuibin
 * @create 2020-08-04 17:00
 * @update yunya-lihuibin    2020-08-04    新建
 */
@ApiModel(value = "新增设备类型（公司端新增设备）")
@Data
@ToString
public class DeviceAddTypeModel implements Serializable {

    /**
     * 门诊组织id
     */
    @ApiModelProperty(value = "门诊组织id", required = true)
    @NotNull(message = "门诊组织id不能为空！")
    private Integer orgId;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称", required = true)
    @NotNull(message = "设备名称不能为空")
    private String name;

}
