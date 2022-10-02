package com.yunya.feign.appointment.domain.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 预约登记查询参数
 **/
@ApiModel(value = "ReservationCodeQuery",description = "预约登记号码查询参数")
@Data
public class ReservationCodeQuery implements Serializable {

    /**
     * 预约项目ID
     */
    @ApiModelProperty(value = "预约登记号码")
    private String code;
}
