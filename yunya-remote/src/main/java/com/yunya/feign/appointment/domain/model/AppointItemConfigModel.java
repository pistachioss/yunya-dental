package com.yunya.feign.appointment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 预约项目适用门诊配置
 *
 * @author yunya-lihuibin
 * @create 2020-08-04 9:48
 * @update yunya-lihuibin    2020-08-04    新建
 */
@ApiModel(value = "预约项目适用门诊配置")
@Data
@ToString
public class AppointItemConfigModel implements Serializable {
    /**
     * 诊所ID
     */
    @ApiModelProperty(value = "诊所ID" , required = true)
    @NotNull(message = "诊所ID不能为空！")
    private Integer orgId;

    /**
     * 预约项目ID
     */
    @ApiModelProperty(value = "预约项目ID", required = true)
    @NotNull(message = "预约项目ID")
    private Integer appointItemId;

    /**
     * 是否启用 是否有效
     */
    @ApiModelProperty(value = "是否启用 是否有效", required = true)
    @NotNull(message = "是否启用 是否有效 不能为空！")
    private Boolean inservice;

}
