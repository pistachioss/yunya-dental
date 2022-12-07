package com.yunya.feign.appointment.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 预约意向登记申请参数
 **/
@ApiModel(value = "ReservationModel",description = "预约意向登记申请参数")
@Data
public class ReservationModel implements Serializable {

    /**
     * 主键
     */
    @ApiModelProperty("预约申请ID")
    private Integer id;

    /**
     * 预约意向渠道ID
     */
    @ApiModelProperty(value = "预约意向渠道ID",required = true)
    @NotNull(message = "预约意向渠道ID不能为空")
    private Integer reservationSourceId;

    /**
     * 预约意向渠道ID
     */
    @ApiModelProperty(value = "预约登记流量ID",required = true)
    @NotNull(message = "预约登记流量ID不能为空")
    private Integer reservationLimitId;

    /**
     * 预约项目
     */
    @ApiModelProperty(value = "预约项目",required = true)
    @NotNull(message = "预约项目不能为空")
    @NotBlank(message = "预约项目不能为空")
    private String appointItemName;

    /**
     * 预约意向门诊ID
     */
    @ApiModelProperty(value = "预约意向门诊ID")
    private Integer orgId;

    /**
     * 预约意向门诊Name
     */
    @ApiModelProperty(value = "预约意向门诊Name")
    private String orgName;

    /**
     * 预约意向code
     */
    @ApiModelProperty(value = "预约意向code")
    private String code;

    /**
     * 就诊人名字
     */
    @ApiModelProperty(value = "就诊人名字",required = true)
    @NotNull(message = "就诊人名字不能为空")
    @NotBlank(message = "就诊人名字不能为空")
    private String patientName;

    /**
     * 就诊人手机号
     */
    @ApiModelProperty(value = "就诊人手机号名字",required = true)
    @NotNull(message = "就诊人手机号不能为空")
    @NotBlank(message = "就诊人手机号不能为空")
    private String patientPhone;

    /**
     * 就诊人性别
     */
    @ApiModelProperty(value = "就诊人性别",required = true)
    private Byte patientGender;

    /**
     * 预约登记状态 0-新建；1-已预约；2-已挂号
     */
    @ApiModelProperty("预约登记状态 0-新建；1-已预约；2-已挂号")
    private Byte status;

    /**
     * 是否有效，是否删除(默认有效) 1-有效；0删除
     */
    @ApiModelProperty(value = "是否有效，是否删除(默认有效) 1-有效；0删除")
    private Boolean inservice;

    /**
     * 就诊人手机号
     */
    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "预约意向日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date reservationDate;
}
