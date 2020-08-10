package com.yunya.feign.appointment.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 修改预约操作记录参数封装
 *
 * @author yunya-lihuibin
 * @create 2020-08-10 13:49
 * @update yunya-lihuibin    2020-08-10    新建
 */
@ApiModel(value = "修改预约操作记录参数封装")
@Data
@ToString
public class AppointOperationForm implements Serializable {

    /** 预约操作记录id */
    @ApiModelProperty(value = "预约操作记录id", required = true)
    @NotNull(message = "预约操作记录id不能为空！")
    private Integer id;

    /**
     * 诊所ID
     */
    @ApiModelProperty(value = "诊所ID", required = true)
    @NotNull(message = "诊所ID不能为空！")
    private Integer orgId;

    /**
     * 预约ID
     */
    @ApiModelProperty(value = "预约ID", required = true)
    @NotNull(message = "预约ID不能为空！")
    private Integer appointmentId;

    /**
     * 操作类型 操作记录(0-新建预约；1-修改预约；2-取消预约；3-确认预约；4；取消确认)
     */
    @ApiModelProperty(value = "操作类型 操作记录(0-新建预约；1-修改预约；2-取消预约；3-确认预约；4；取消确认)", required = true)
    @NotNull(message = " 操作类型不能为空！")
    private Byte operateType;

    /** 预约修改之前的内容 */
    @ApiModelProperty(value = "预约修改之前的内容")
    private String beforeOperation;

    /** 预约修改之后的内容 */
    @ApiModelProperty(value = "预约修改之后的内容")
    private String afterOperation;

    /** 备注 */
    @ApiModelProperty(value = "备注")
    private String remarks;
}
