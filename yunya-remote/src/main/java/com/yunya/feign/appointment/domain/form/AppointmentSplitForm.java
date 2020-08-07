package com.yunya.feign.appointment.domain.form;

import com.yunya.feign.appointment.domain.base.AppointmentSplitBase;
import com.yunya.feign.appointment.domain.base.AppointmentSplitUpdateBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 预约分解参数封装
 *
 * @author yunya-lihuibin
 * @create 2020-07-30 14:41
 * @update yunya-lihuibin    2020-07-30    新建
 */
@ApiModel("预约分解参数封装")
@Data
public class AppointmentSplitForm {

    /** 总预约时长 */
    @ApiModelProperty(value = "总预约时长", required = true)
    @NotNull(message = "总预约时长不能为空!")
    private Integer appointDuration;

    /** 组织id */
    @ApiModelProperty(value = "组织id", hidden = true)
    private Integer orgId;

    /** 预约id */
    @ApiModelProperty(value = "预约id", required = true)
    @NotNull(message = "预约id不能为空！")
    private Integer appointmentId;

    @ApiModelProperty(value = "预约分解列表")
    private List<AppointmentSplitUpdateBase> splitList;
}
