package com.yunya.feign.appointment.domain.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 条件查询预约分解
 *
 * @author yunya-lihuibin
 * @create 2020-07-30 18:58
 * @update yunya-lihuibin    2020-07-30    新建
 */
@ApiModel("条件查询预约分解")
@Data
@ToString
public class AppointmentSplitQuery implements Serializable {

    /** 诊所id */
    @ApiModelProperty(value = "诊所id", required = true)
    @NotNull(message = "门诊id不能为空!")
    private Integer orgId;

    /** 预约id */
    @ApiModelProperty(value = "预约id")
    private Integer appointmentId;

    /** 医生id/助手id */
    @ApiModelProperty(value = "医生id/助手id")
    private Integer assistantId;

    /** 预约日期 */
    @ApiModelProperty(value = "预约日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date appointDate;


    /** 是否启用 是否有效 */
    @ApiModelProperty(value = "是否启用 是否有效")
    private Boolean inservice;

}
