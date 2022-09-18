package com.yunya.feign.appointment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 预约意向登记渠道来源申请参数
 **/
@ApiModel(value = "ReservationResourceModel",description = "预约意向登记渠道来源申请参数")
@Data
public class ReservationResourceModel implements Serializable {
    /**
     * 预约意向渠道ID
     */
    @ApiModelProperty(value = "预约意向渠道来源名称",required = true)
    @NotNull(message = "预约意向渠道来源名称不能为空")
    @NotBlank(message = "预约意向渠道来源名称不能为空")
    private String sourceName;
}
