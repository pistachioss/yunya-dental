package com.yunya.feign.appointment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 配置项目适用门诊列表
 *
 * @author yunya-lihuibin
 * @create 2020-07-31 11:53
 * @update yunya-lihuibin    2020-07-31    新建
 */
@ApiModel(value = "配置项目适用门诊列表")
@Data
@ToString
public class AppointItemConfigQuery implements Serializable {

    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /** 预约项目id */
    @ApiModelProperty(value = "预约项目id")
    private Integer appointItemId;

    @ApiModelProperty(value = "是否启用，默认为true;1-启用，0-不启用")
    private Byte inservice=1;
}
