package com.yunya.feign.appointment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 预约项目统一配置(公司端)
 *
 * @author yunya-lihuibin
 * @create 2020-08-04 11:17
 * @update yunya-lihuibin    2020-08-04    新建
 */
@ApiModel(value = "预约项目统一配置(公司端)")
@Data
@ToString
public class AppointItemBatchConfigModel implements Serializable {
    /**
     * 预约项目ID
     */
    @ApiModelProperty(value = "预约项目ID", required = true)
    @NotNull(message = "预约项目ID")
    private Integer appointItemId;

}
