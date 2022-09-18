package com.yunya.feign.appointment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 预约申请参数
 * @author: LHB
 * @create: 2021-05-18 16:20
 **/
@ApiModel(value = "OnlineAppointItemModel",description = "预约申请参数")
@Data
public class OnlineAppointItemModel {

    /**
     * 申请预约项目名称
     */
    @ApiModelProperty(value = "申请预约项目名称",required = true)
    @NotBlank(message = "申请预约项目名称不能为空")
    @NotNull(message = "申请预约项目名称不能为空")
    private String name;

    /**
     * 预约时长(单位: 分钟)
     */
    @ApiModelProperty(value = "预约时长(单位: 分钟)",required = true)
    @NotNull(message = "预约时长(单位: 分钟)不能为空")
    @Range(min = 15,max = 480,message = "预约时长(单位: 分钟)应该大于15且小于480")
    private Integer duration;

    /**
     * 是否可用 0-不可以；1-可用
     */
    @ApiModelProperty("是否可用 0-不可以；1-可用")
    private Boolean inservice = true;
}