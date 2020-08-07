package com.yunya.feign.appointment.domain.form;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 患者预约基础参数列表封装
 *
 * @author yunya-lihuibin
 * @create 2020-07-28 16:26
 * @update yunya-lihuibin    2020-07-28    新建
 */

@ApiModel("患者预约基础参数列表封装")
@Data
@ToString
public class AppointmentBaseForm implements Serializable {

    /** 预约id */
    @ApiModelProperty(value = "预约id", required = true)
    @NotNull(message = "预约id不能为空！")
    private Integer id;

    /** 门诊id */
    @ApiModelProperty(value = "门诊id", required = true)
    @NotNull(message = "门诊id不能为空！")
    private Integer orgId;

    /** 患者id */
    @ApiModelProperty(value = "患者id",required = true)
    @NotNull(message = "患者id为空！")
    private Integer patientId;

    /** 患者姓名 */
    @ApiModelProperty(value = "患者姓名",required = true)
    @NotNull(message = "患者姓名为空！")
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
    @ApiModelProperty(value = "预约状态  未确认-false;确认-true")
    private Boolean appointState;

    /** 医生id */
    @ApiModelProperty(value = "医生id", required = true)
    @NotNull(message = "医生不能为空！")
    private Integer dentistId;

    /**助手id*/
    @ApiModelProperty("助手id")
    private Integer assistantId;

    /** 预约科室id */
    @ApiModelProperty("预约科室id")
    private Integer clinicDeptRoomId;

    /** 预约设备id */
    @ApiModelProperty("预约设备id ")
    private Integer clinicDeviceItemId;

    /** 牙位 */
    @ApiModelProperty("牙位")
    private String toothBit;

    /** 预约确认 0-未确认；1-确认 */
    @ApiModelProperty("预约确认 0-未确认；1-确认")
    private Boolean confirmStatus;

    /** 预约项目明细条目id */
    @ApiModelProperty("预约项目明细条目id")
    private Integer clinicAppointItemId;

    /** 预约内容 */
    @ApiModelProperty("预约内容")
    private String appointContent;

    /** 备注 */
    @ApiModelProperty("备注")
    private String remark;

    /** 是否启用 是否有效 */
    @ApiModelProperty(value = "是否启用 是否有效")
    private Boolean inservice;
}
