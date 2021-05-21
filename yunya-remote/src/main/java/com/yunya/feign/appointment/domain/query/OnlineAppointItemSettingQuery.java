package com.yunya.feign.appointment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
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
public class OnlineAppointItemSettingQuery implements Serializable {

    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Integer id;

    /**
     * 预约项目ID
     */
    @ApiModelProperty(value = "预约项目ID")
    private Integer appointItemId;

    /**
     * 预约项目名称
     */
    @ApiModelProperty("预约项目名称")
    private String appointItemName;

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
