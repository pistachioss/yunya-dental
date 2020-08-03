package com.yunya.feign.appointment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 预约设置查询参数
 *
 * @author yunya-lihuibin
 * @create 2020-08-03 19:33
 * @update yunya-lihuibin    2020-08-03    新建
 */
@ApiModel(value = "预约设置查询参数")
@Data
@ToString
public class AppointSettingQuery implements Serializable {

    /** 用户id */
    @ApiModelProperty(value = "用户id")
    private Integer userId;
}
