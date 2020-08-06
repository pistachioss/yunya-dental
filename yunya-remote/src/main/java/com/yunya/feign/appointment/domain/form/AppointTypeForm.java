package com.yunya.feign.appointment.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 新增预约类型参数封装
 *
 * @author yunya-lihuibin
 * @create 2020-07-31 18:05
 * @update yunya-lihuibin    2020-07-31    新建
 */
@ApiModel(value = "新增预约类型参数封装")
@Data
@ToString
public class AppointTypeForm implements Serializable {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键", required = true)
    private Integer id;

    /**
     * 预约项目分类名称
     */
    @ApiModelProperty(value = "预约项目分类名称", required = true)
    @NotNull(message = "预约项目分类名称不能为空！")
    private String name;

    /**
     * 备注 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;

    /**
     * 是否启用 是否有效
     */
    @ApiModelProperty(value = "是否启用 是否有效")
    private Byte inservice;

}
