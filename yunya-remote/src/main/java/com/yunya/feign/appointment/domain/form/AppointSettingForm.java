package com.yunya.feign.appointment.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.persistence.Column;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 预约显示设置表单
 *
 * @author yunya-lihuibin
 * @create 2020-08-03 17:58
 * @update yunya-lihuibin    2020-08-03    新建
 */
@ApiModel(value = "预约显示设置表单")
@Data
@ToString
public class AppointSettingForm implements Serializable {

    /**
     * 预约显示设置id
     */
    @ApiModelProperty(value = "预约显示设置id")
    private Integer id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id",required = true)
    @NotNull(message = "用户id不能为空！")
    private Integer userId;

    /**
     * 预约单位(分钟)
     */
    @ApiModelProperty(value = "预约单位(分钟)", required = true)
    @NotNull(message = "预约单位不能为空！")
    @Range(min = 5, max = 30)
    private Integer appointUnit;

    /**
     * 预约显示列数
     */
    @ApiModelProperty(value = "预约显示列数", required = true)
    @NotNull(message = "预约显示列数不能为空！")
    @Range(min = 1, max = 15)
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
    private Boolean inservice;
}
