package com.yunya.feign.appointment.domain.model;

import com.yunya.feign.appointment.domain.base.AppointmentSplitBase;
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
public class AppointmentSplitModel {

    /** 总预约时长 */
    @ApiModelProperty(value = "总预约时长", required = true)
    @NotNull(message = "总预约时长不能为空!")
    private Integer appointDuration;

    @ApiModelProperty(value = "预约分解列表", required = true)
    @NotNull(message = "预约分解不能为空！")
    private List<AppointmentSplitBase> splitList;
}
