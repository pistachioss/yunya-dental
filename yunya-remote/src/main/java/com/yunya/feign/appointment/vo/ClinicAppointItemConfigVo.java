package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 预约项目适配视图模型
 *
 * @author yunya-lihuibin
 * @create 2020-08-04 11:31
 * @update yunya-lihuibin    2020-08-04    新建
 */
@ApiModel(value = "预约项目适配视图模型")
@Data
@ToString
public class ClinicAppointItemConfigVo implements Serializable {

    /** 组织全名 */
    @ApiModelProperty(value = "组织全名")
    private String name;

    /** 是否启用 */
    @ApiModelProperty(value = "是否启用")
    private Boolean inservice;
}
