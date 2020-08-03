package com.yunya.feign.appointment.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 预约分解删除参数封装
 *
 * @author yunya-lihuibin
 * @create 2020-07-30 16:52
 * @update yunya-lihuibin    2020-07-30    新建
 */
@ApiModel("预约分解删除参数封装")
@Data
@ToString
public class AppointmentSplitDelForm implements Serializable {

    /** 预约分解id */
    @ApiModelProperty(value = "预约分解id", required = true)
    @NotNull(message = "分解id不能为空！")
    private Integer[] splitIds;
}
