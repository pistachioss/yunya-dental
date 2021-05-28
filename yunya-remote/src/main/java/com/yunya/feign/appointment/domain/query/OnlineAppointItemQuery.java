package com.yunya.feign.appointment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @program: yunya-dental
 * @description: 预约申请参数
 * @author: LHB
 * @create: 2021-05-18 16:20
 **/
@ApiModel(value = "OnlineAppointItemQuery",description = "预约申请参数")
@Data
public class OnlineAppointItemQuery {

    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /**
     * 申请预约项目名称
     */
    @ApiModelProperty(value = "申请预约项目名称")
    private String name;

    /**
     * 是否可用 0-不可以；1-可用
     */
    @ApiModelProperty("是否可用 0-不可以；1-可用")
    private Boolean inservice;
}