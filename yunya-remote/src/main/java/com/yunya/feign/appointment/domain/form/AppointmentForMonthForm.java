package com.yunya.feign.appointment.domain.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 查询指定时间段内指定医生的每一天的预约患者数量
 * @author: LHB
 * @create: 2020-11-02 19:36
 **/
@ApiModel(value = "AppointmentForMonthForm",description = "查询指定时间段内指定医生的每一天的预约患者数量")
@Data
public class AppointmentForMonthForm implements Serializable {
    /** 医生ID */
    @ApiModelProperty(value = "医生ID")
    private Integer dentistId;
    /** 开始日期 */
    @ApiModelProperty(value = "开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date startDate;
    /** 结束日期 */
    @ApiModelProperty(value = "结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date endDate;
    @ApiModelProperty("门诊ID")
    private Integer orgId;
}
