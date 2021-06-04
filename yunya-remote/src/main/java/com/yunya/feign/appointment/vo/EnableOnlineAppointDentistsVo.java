package com.yunya.feign.appointment.vo;

import com.yunya.framework.common.utils.StringHelper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 线上可预约医生信息
 * @author: LHB
 * @create: 2021-05-28 13:26
 **/
@Data
@ApiModel(value = "EnableOnlineAppointDentistsVo",description = "线上可预约医生信息")
public class EnableOnlineAppointDentistsVo implements Serializable {
    @ApiModelProperty("医生ID")
    private Integer dentistId;
    @ApiModelProperty("医生名字")
    private String dentistName;
}
