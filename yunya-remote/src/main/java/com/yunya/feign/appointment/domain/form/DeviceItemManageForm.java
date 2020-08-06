package com.yunya.feign.appointment.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 修改设备项目表单
 *
 * @author yunya-lihuibin
 * @create 2020-08-02 21:52
 * @update yunya-lihuibin    2020-08-02    新建
 */
@ApiModel(value = "修改设备项目表单")
@Data
@ToString
public class DeviceItemManageForm implements Serializable {
    /**
     * 设备项目id
     */
    @ApiModelProperty(value = "设备项目id", required = true)
    @NotNull(message = "设备项目id不能为空！")
    private Integer id;

    /**
     * 设备编号
     */
    @ApiModelProperty(value = "设备编号", required = true)
    @NotNull(message = "设备编号不能为空！")
    private String number;

    /**
     * 诊所ID
     */
    @ApiModelProperty(value = "诊所ID", required = true)
    @NotNull(message = "诊所id不能为空！")
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
