package com.yunya.feign.treatment.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.feign.appointment.vo.AppointmentSplitVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: APP端，就诊详情-预约信息视图模型
 * @author: LHB
 * @create: 2020-12-17 20:07
 **/
@Data
@ApiModel(value = "TreatmentAppointInfo4AppVO", description = "APP端，就诊详情-预约信息视图模型")
public class TreatmentAppointInfo4AppVO implements Serializable {
    /** 预约ID */
    @ApiModelProperty("预约ID")
    private Integer appointId;
    @ApiModelProperty("预约医生ID")
    private Integer dentistId;
    @ApiModelProperty("预约医生名字")
    private String dentistName;
    @ApiModelProperty("预约门诊科室ID")
    private Integer deptRoomId;
    @ApiModelProperty("预约门诊科室名称")
    private String deptRoomName;
    @ApiModelProperty("预约设备ID")
    private Integer clinicDeviceItemId;
    @ApiModelProperty("预约设备名称")
    private String clinicDeviceItemName;
    @ApiModelProperty("预约日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date appointDate;
    @ApiModelProperty("预约时间")
    private String appointTime;
    @ApiModelProperty("预约时长")
    private Integer appointDuration;
    @ApiModelProperty("预约内容")
    private String appointContent;
    @ApiModelProperty("预约确认 0-未确认；1-确认")
    private Boolean confirmStatus;
    @ApiModelProperty("预约备注")
    private String remarks;
    @ApiModelProperty("预约类型 0-初诊预约； 1-复诊预约")
    private Byte appointType;
    @ApiModelProperty("预约分解列表")
    private List<AppointmentSplitVo> splits;
}
