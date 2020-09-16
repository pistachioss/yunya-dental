package com.yunya.feign.appointment.domain.model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.feign.appointment.domain.base.AppointmentSplitBaseInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 患者预约基础参数列表封装
 *
 * @author yunya-lihuibin
 * @create 2020-07-28 16:26
 * @update yunya-lihuibin    2020-07-28    新建
 */

@ApiModel(value = "患者预约基础参数列表封装")
@Data
@ToString
public class AppointmentBaseModel implements Serializable {

    /** 预约id */
    @ApiModelProperty(value = "预约id")
    private Integer id;

    /** 患者id */
    @ApiModelProperty(value = "患者id",required = true)
    @NotNull(message = "患者id为空！")
    private Integer patientId;

    /** 门诊id */
    @ApiModelProperty(value = "门诊id", required = true)
    @NotNull(message = "门诊id不能为空！")
    private Integer orgId;

    /** 患者姓名 */
    @ApiModelProperty(value = "患者姓名",hidden = true)
    private String patientName;

    /** 预约日期 */
    @ApiModelProperty(value = "预约日期",required = true)
    @NotNull(message = "预约日期为空！")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date appointDate;

    /** 预约时间（默认当前系统时间） */
    @ApiModelProperty(value = "预约时间（默认当前系统时间）",required = true)
    @NotNull(message = "预约时间为空！")
    private String appointTime;

    /** 预约时长（默认15分钟） */
    @ApiModelProperty(value = "预约时长（默认15分钟）",required = true)
    @NotNull(message = "预约时长为空！")
    private Integer appointDuration;

    /**预约状态  未确认-false;确认-true*/
    @ApiModelProperty(value = "预约状态 0-预约未到，1-履约，2，取消预约，3-失约")
    private Byte appointState;

    /** 医生id */
    @ApiModelProperty(value = "医生id", required = true)
    @NotNull(message = "医生不能为空！")
    private Integer dentistId;

    /**助手id*/
    @ApiModelProperty(value = "助手id")
    private Integer assistantId;

    /** 预约科室id */
    @ApiModelProperty(value = "预约科室id")
    private Integer deptRoomId;

    /** 预约设备id */
    @ApiModelProperty(value = "预约设备id ")
    private Integer clinicDeviceItemId;

    /** 牙位 */
    @ApiModelProperty(value = "牙位")
    private String toothBit;

    /** 预约确认 0-未确认；1-确认 */
    @ApiModelProperty(value = "预约确认 0-未确认；1-确认")
    private Boolean confirmStatus;

    /** 预约项目明细条目id */
    @ApiModelProperty(value = "预约项目明细条目id")
    private Integer clinicAppointItemId;

    /** 预约内容 */
    @ApiModelProperty(value = "预约内容")
    private String appointContent;

    /** 备注 */
    @ApiModelProperty(value = "备注")
    private String remarks;

    /** 时长分解列表 */
    @ApiModelProperty(value = "时长分解列表")
    private List<AppointmentSplitBaseInfo> splitList;
}
