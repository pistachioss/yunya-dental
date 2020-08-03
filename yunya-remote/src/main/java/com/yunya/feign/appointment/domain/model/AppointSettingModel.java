package com.yunya.feign.appointment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 新增预约设置参数模型
 *
 * @author yunya-lihuibin
 * @create 2020-08-03 19:43
 * @update yunya-lihuibin    2020-08-03    新建
 */
@ApiModel(value = "新增预约设置参数模型")
@Data
@ToString
public class AppointSettingModel implements Serializable {
    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    @NotNull(message = "用户id不能为空！")
    private Integer userId;

    /**
     * 预约单位(分钟)
     */
    @ApiModelProperty(value = "预约单位(分钟)")
    private Integer appointUnit;

    /**
     * 预约显示列数
     */
    @ApiModelProperty(value = "预约显示列数")
    private Integer columns;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remarks;

    /**
     * 是否启用 是否有效
     */
    @ApiModelProperty(value = "是否启用 是否有效")
    private Byte inservice;
}
