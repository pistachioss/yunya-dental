package com.yunya.feign.appointment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 删除预约项目
 *
 * @author yunya-lihuibin
 * @create 2020-07-31 13:56
 * @update yunya-lihuibin    2020-07-31    新建
 */
@ApiModel(value = "删除预约项目")
@Data
@ToString
public class AppointItemDelModel implements Serializable {

    /** 门诊id */
    @ApiModelProperty(value = "门诊id", required = true)
    private Integer orgId;

    /** 预约项目id */
    @ApiModelProperty(value = "预约项目id", required = true)
    private Integer appointItemId;
}
