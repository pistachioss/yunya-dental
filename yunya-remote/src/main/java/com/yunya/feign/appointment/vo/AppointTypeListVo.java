package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 预约分类列表
 *
 * @author yunya-lihuibin
 * @create 2020-08-04 15:58
 * @update yunya-lihuibin    2020-08-04    新建
 */
@ApiModel(value = "预约分类列表")
@Data
@ToString
public class AppointTypeListVo implements Serializable {

    /**
     * 预约分类id
     */
    @ApiModelProperty(value = "预约分类id")
    private Integer id;

    /**
     * 预约项目类型名称
     */
    @ApiModelProperty(value = "预约项目类型名称")
    private String name;

    /**
     * 是否启用
     */
    @ApiModelProperty(value = "是否启用")
    private Byte inservice;

}
