package com.yunya.feign.appointment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 预约项目参数
 *
 * @author yunya-lihuibin
 * @create 2020-07-31 11:04
 * @update yunya-lihuibin    2020-07-31    新建
 */
@ApiModel(value = "预约项目参数")
@Data
@ToString
public class AppointmentItemModel implements Serializable {

    /**
     * 预约项目类别id
     */
    @ApiModelProperty(value = "项目名称", required = true)
    @NotBlank(message = "项目分类id为空！请先选中预约分类！")
    private String appointTypeId;

    /**
     * 项目名称
     */
    @ApiModelProperty(value = "项目名称", required = true)
    @NotNull(message = "项目名称不能为空！")
    private String name;

    /**
     * 预约默认时长（分钟）
     */
    @ApiModelProperty(value = "预约默认时长（分钟）")
    private Integer duration;

    /** 备注 */
    @ApiModelProperty(value = "备注")
    private String remarks;
    /**
     * 是否有效、是否启用、是否可见
     */
    @ApiModelProperty(value = "是否有效、是否启用、是否可见")
    private Byte inservice;

}
