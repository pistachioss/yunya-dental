package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 预约设置视图模型
 *
 * @author yunya-lihuibin
 * @create 2020-08-03 18:46
 * @update yunya-lihuibin    2020-08-03    新建
 */
@ApiModel(value = "预约设置视图模型")
@Data
@ToString
public class AppointSettingVo implements Serializable {
    /**
     * 预约显示设置id
     */
    @ApiModelProperty(value = "预约显示设置id")
    private Integer id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
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
    @ApiModelProperty(value = "备注")
    private String remarks;

    /**
     * 是否启用 是否有效
     */
    @ApiModelProperty(value = "是否启用 是否有效")
    private Integer inservice;
}
