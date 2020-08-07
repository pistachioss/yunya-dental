package com.yunya.feign.appointment.domain.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 修改时长分解参数封装
 *
 * @author yunya-lihuibin
 * @create 2020-07-30 14:19
 * @update yunya-lihuibin    2020-07-30    新建
 */
@ApiModel(value = "修改时长分解参数封装")
@Data
@ToString
public class AppointmentSplitUpdateBaseInfo extends AppointmentSplitBaseInfo implements Serializable {

    /** 时长分解id */
    @ApiModelProperty(value = "时长分解id",required = true)
    @NotNull(message = "时长分解id不能为空！")
    private Integer id;

}
