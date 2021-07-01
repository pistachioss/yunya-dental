package com.yunya.feign.appointment.vo;

import com.yunya.framework.common.utils.StringHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 门诊营业时间Vo
 * @author: LHB
 * @create: 2021-06-23 10:11
 **/
@ApiModel(value = "ClinicBusinessHoursVo",description = "门诊营业时间Vo")
@Data
public class ClinicBusinessHoursVo implements Serializable {
    @ApiModelProperty(value = "businessStartTime",name = "门诊开始营业时间",example = "08:45")
    private String businessStartTime = "08:45";
    @ApiModelProperty(value = "businessEndTime",name = "门诊结束营业时间",example = "17:45")
    private String businessEndTime = "17:45";
}
