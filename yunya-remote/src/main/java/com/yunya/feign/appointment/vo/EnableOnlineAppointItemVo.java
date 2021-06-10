package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: 线上可预约项目参数
 * @author: LHB
 * @create: 2021-05-28 16:17
 **/
@Data
@ApiModel(value = "EnableOnlineAppointItemVo",description = "线上可预约项目参数")
public class EnableOnlineAppointItemVo implements Serializable {
    /** 线上可预约项目配置记录ID */
    @ApiModelProperty("线上可预约项目配置记录ID")
    private Integer itemSettingId;
    /** 医生ID */
    @ApiModelProperty("医生ID")
    private Integer dentistId;
    /** 门诊ID */
    @ApiModelProperty("门诊ID ")
    private Integer orgId;
    /** 可预约项目 */
    @ApiModelProperty("可预约项目")
    private List<Integer> lists;
}
