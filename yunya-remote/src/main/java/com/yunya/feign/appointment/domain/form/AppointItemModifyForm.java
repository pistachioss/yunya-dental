package com.yunya.feign.appointment.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 修改预约项目参数封装
 *
 * @author yunya-lihuibin
 * @create 2020-08-04 16:38
 * @update yunya-lihuibin    2020-08-04    新建
 */
@ApiModel(value = "修改预约项目参数封装")
@Data
@ToString
public class AppointItemModifyForm implements Serializable {

    /**
     * 预约项目id
     */
    @ApiModelProperty(value = "预约项目id", required = true)
    @NotNull(message = "预约项目id不能为空！")
    private Integer id;

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

    /** 是否可用 */
    @ApiModelProperty(value = "是否可用")
    private Boolean inservice;
}
