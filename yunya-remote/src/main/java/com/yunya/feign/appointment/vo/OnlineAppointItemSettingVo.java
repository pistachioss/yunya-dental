package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: 在线预约项目设置
 * @author: LHB
 * @create: 2021-05-18 16:30
 **/
@Data
@ApiModel(value = "OnlineAppointItemSettingModel",description = "在线预约项目设置")
public class OnlineAppointItemSettingVo implements Serializable {

    /** 线上预约项目配置主键 */
    @ApiModelProperty("线上预约项目配置主键")
    private Integer itemSettingId;

    /**
     * 医生ID
     */
    @ApiModelProperty("医生ID")
    private Integer dentistId;

    /**
     * 医生名字
     */
    @ApiModelProperty("医生名字")
    private String dentistName;

    /**
     * 是否可以线上预约
     */
    @ApiModelProperty("是否可以线上预约")
    private Boolean enableOnlineAppoint;

    /**
     * 线上预约项目信息列表
     */
    @ApiModelProperty("线上预约项目信息列表")
    private List<OnlineAppointItemVo> lists;
}
