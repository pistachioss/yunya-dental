package com.yunya.feign.treatment.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：挂号预约详情VO
 *
 * @author: chenlin
 * @Description: 挂号预约详情VO
 * @Date: 2022/4/13 14:39
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("挂号预约详情VO")
public class RegisteredAppointVO implements Serializable {
    /** 患者姓名 */
    @ApiModelProperty("患者姓名")
    private String patientName;

    /** 挂号医生 */
    @ApiModelProperty("挂号医生")
    private String regDentistName;

    /** 挂号时间 */
    @JsonFormat(pattern = "HH:mm",timezone = "GMT+8")
    @ApiModelProperty("挂号时间")
    private Date regTime;

    /** 预约医生 */
    @ApiModelProperty("预约医生")
    private String appointDentistName;

    /** 预约时间 */
    @ApiModelProperty("预约时间")
    private String appointTime;
}
