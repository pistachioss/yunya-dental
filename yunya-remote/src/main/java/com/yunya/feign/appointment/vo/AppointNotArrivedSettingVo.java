package com.yunya.feign.appointment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 预约未到列表视图模型
 *
 * @author yunya-lihuibin
 * @create 2020-08-11 11:05
 * @update yunya-lihuibin    2020-08-11    新建
 */
@ApiModel(value = "预约未到列表视图模型")
@Data
@ToString
public class AppointNotArrivedSettingVo implements Serializable {

    /**
     * 列表设置id
     */
    @ApiModelProperty(value = "列表设置id")
    private Integer id;

    /**
     * 用户ID 用户id
     */
    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    /**
     * 患者列 是否显示：0-不显示；1-显示
     */
    @ApiModelProperty(value = "患者列 是否显示：0-不显示；1-显示")
    private Boolean patientColumn;

    /**
     * 初复诊列 是否显示：0-不显示；1-显示
     */
    @ApiModelProperty(value = "初复诊列 是否显示：0-不显示；1-显示")
    private Boolean appointTypeColumn;

    /**
     * 医生列 是否显示：0-不显示；1-显示
     */
    @ApiModelProperty(value = "医生列 是否显示：0-不显示；1-显示")
    private Boolean dentistColumn;

    /**
     * 病历编号列 是否显示：0-不显示；1-显示
     */
    @ApiModelProperty(value = "病历编号列 是否显示：0-不显示；1-显示")
    private Boolean medicalRecordNumberColumn;

    /**
     * 手机号列 是否显示：0-不显示；1-显示
     */
    @ApiModelProperty(value = "手机号列 是否显示：0-不显示；1-显示")
    private Boolean telephoneColumn;

    /**
     * 预约时间列 是否显示：0-不显示；1-显示
     */
    @ApiModelProperty(value = "预约时间列 是否显示：0-不显示；1-显示")
    private Boolean appointTimeColumn;

    /**
     * 预约时长列 是否显示：0-不显示；1-显示
     */
    @ApiModelProperty(value = "预约时长列 是否显示：0-不显示；1-显示")
    private Boolean appointDurationColumn;

    /**
     * 预约事项列 是否显示：0-不显示；1-显示
     */
    @ApiModelProperty(value = "预约事项列 是否显示：0-不显示；1-显示")
    private Boolean appointItemTypeColumn;

    /**
     * 确认状态列 是否显示：0-不显示；1-显示
     */
    @ApiModelProperty(value = "确认状态列 是否显示：0-不显示；1-显示")
    private Boolean confirmStatusColumn;

    /**
     * 预约备注列 是否显示：0-不显示；1-显示
     */
    @ApiModelProperty(value = "预约备注列 是否显示：0-不显示；1-显示")
    private Boolean appointRemarksColumn;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;

    /**
     * 是否启用 是否有效
     */
    @ApiModelProperty(value = "是否启用 是否有效")
    private Boolean inservice;
}
