/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: OrdersType
 * Author:   Perter_Chou
 * Date:     2019/8/1 23:38
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * Perter_Chou         23:38           Since 1.0         版权信息
 */
package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 〈一句话功能简述〉<br> 
 * 〈公司端预约项目数据接收类〉
 *
 * @author Perter_Chou
 * @create 2019/8/1
 * @since 1.0.0
 */
@Data
@ToString
@ApiModel("可预约项目类型封装")
public class AppointmentItemVo implements Serializable {
    @ApiModelProperty("预约项目ID")
    private Integer id;

    @ApiModelProperty("预约项目名称")
    private String name;

    @ApiModelProperty("预约项目分类")
    private String typeName;
    @ApiModelProperty("预约项目分类ID")
    private Integer appointTypeId;

    @ApiModelProperty("默认时长,单位分钟")
    private Integer duration;

    @ApiModelProperty("是否启用 是否有效")
    private Boolean inservice;

    @ApiModelProperty("备注")
    private String remarks;
}