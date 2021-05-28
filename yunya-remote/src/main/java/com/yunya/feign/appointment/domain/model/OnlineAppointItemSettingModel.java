package com.yunya.feign.appointment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
public class OnlineAppointItemSettingModel implements Serializable {
    /**
     * 主键
     */
    @ApiModelProperty("主键")
    private Integer itemSettingId;

    /**
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID",required = true)
    @NotNull(message = "医生ID不能为空")
    private Integer patientId;

    /**
     * 门诊ID
     */
    @ApiModelProperty(value = "门诊ID",required = true)
    @NotBlank(message = "门诊ID不能为空")
    private Integer orgId;

    /**
     * 是否删除，是否有效；1-有效，0-无效
     */
    @ApiModelProperty(value = "是否删除，是否有效；1-有效，0-无效",required = true)
    private Boolean inservice = true;

    /**
     * 线上可预约项目
     */
    @ApiModelProperty("线上可预约项目")
    private List<OnlineAppointItemDentistModel> lists;

}
