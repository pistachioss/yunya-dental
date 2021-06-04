package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @program: yunya-dental
 * @description: 预约申请参数
 * @author: LHB
 * @create: 2021-05-18 16:20
 **/
@ApiModel(value = "OnlineAppointItemVo",description = "预约申请参数")
@Data
public class OnlineAppointItemVo {

    /** 主键 */
    @ApiModelProperty("主键ID")
    private Integer itemId;

    /**
     * 申请预约项目名称
     */
    @ApiModelProperty(value = "申请预约项目名称")
    private String name;

    /**
     * 预约时长(单位: 分钟)
     */
    @ApiModelProperty(value = "预约时长(单位: 分钟)")
    private Integer duration;
}