package com.yunya.feign.appointment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 在线预约项目设置
 * @author: LHB
 * @create: 2021-05-18 16:30
 **/
@Data
@ApiModel(value = "OnlineAppointItemSettingModel",description = "在线预约项目设置")
public class OnlineAppointItemSettingModel implements Serializable {
    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Integer id;

    /**
     * 预约项目ID
     */
    @ApiModelProperty(value = "预约项目ID",required = true)
    @NotNull(message = "预约项目不能为空")
    private Integer appointItemId;

    /**
     * 预约项目名称
     */
    @ApiModelProperty(value = "预约项目名称",required = true)
    @NotBlank(message = "预约项目名称")
    private String appointItemName;

    /**
     * 预约默认时长（分钟）
     */
    @ApiModelProperty(value = "预约默认时长（分钟）", required = true)
    @NotNull(message = "预约默认时长（分钟）")
    private Integer appointDuration;

    /**
     * 是否删除，是否有效；1-有效，0-无效
     */
    @ApiModelProperty("是否删除，是否有效；1-有效，0-无效")
    private Boolean inservice;
}
