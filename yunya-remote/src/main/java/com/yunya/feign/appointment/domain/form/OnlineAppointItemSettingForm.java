package com.yunya.feign.appointment.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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
public class OnlineAppointItemSettingForm implements Serializable {

    /**
     * 线上项目设置主键
     */
    @ApiModelProperty("线上项目设置主键")
    private Integer itemSettingId;

    /**
     * 门诊ID
     */
    @ApiModelProperty(value = "门诊ID",required = true)
    @NotNull(message = "门诊ID不能为空")
    private Integer orgId;

    /**
     * 医生ID
     */
    @ApiModelProperty(value = "医生ID",required = true)
    @NotNull(message = "医生ID不能为空")
    private Integer dentistId;

    /**
     * 线上预约项目
     */
    @ApiModelProperty("线上预约项目")
    private List<Integer> lists;
}
