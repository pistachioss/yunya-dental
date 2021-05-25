package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 在线预约项目设置
 * @author: LHB
 * @create: 2021-05-18 16:30
 **/
@Data
@ApiModel(value = "OnlineAppointItemSettingModel",description = "在线预约项目设置")
public class OnlineAppointItemSettingModelVo implements Serializable {
    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Integer id;

    /**
     * 预约项目ID
     */
    @ApiModelProperty("预约项目ID")
    private Integer appointItemId;

    /**
     * 预约默认时长（分钟）
     */
    @ApiModelProperty("预约默认时长（分钟）")
    private Integer appointDuration;

    /**
     * 是否删除，是否有效；1-有效，0-无效
     */
    @ApiModelProperty("是否删除，是否有效；1-有效，0-无效")
    private Boolean inservice;
}
