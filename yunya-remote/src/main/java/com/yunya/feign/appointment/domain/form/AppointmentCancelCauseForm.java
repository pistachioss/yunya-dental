package com.yunya.feign.appointment.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 取消预约原因表单
 * @author yunya-lihuibin
 * @create 2020-08-08 16:13
 * @update yunya-lihuibin    2020-08-08    新建
 */
@ApiModel(value = "AppointmentCancelCauseForm",description = "取消预约原因表单")
@Data
@ToString
public class AppointmentCancelCauseForm implements Serializable {

    /** 预约ID */
    @ApiModelProperty(value = "预约ID", required = true)
    @NotNull(message = "预约ID不能为空！")
    private Integer id;

    /** 取消预约原因 */
    @ApiModelProperty(value = "取消预约原因", required = true)
    @NotBlank(message = "取消预约原因不能为空！")
    private String cause;
}
