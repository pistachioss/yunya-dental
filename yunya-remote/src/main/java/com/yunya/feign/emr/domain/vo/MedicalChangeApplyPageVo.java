package com.yunya.feign.emr.domain.vo;

import io.swagger.annotations.*;
import lombok.*;

import java.time.*;

/**
 * @author xiangyang
 * @date 2020/8/7
 */
@Getter
@Setter
@ApiModel(value = "病例变更申请分页模型")
public class MedicalChangeApplyPageVo {

    @ApiModelProperty(value = "审核id")
    private Integer id;

    @ApiModelProperty(value = "电子病例Id或就诊Id")
    private Integer eventId;

    @ApiModelProperty(value = "患者姓名")
    private String patientName;

    @ApiModelProperty(value = "病历号")
    private String medicalNum;

    @ApiModelProperty(value = "就诊门诊")
    private String treatmentClinicName;

    @ApiModelProperty(value = "就诊日期")
    private LocalDate treatmentDate;

    @ApiModelProperty(value = "申请类型")
    private String applyTypeName;

    @ApiModelProperty(value = "申请原因")
    private String applyReason;

    @ApiModelProperty(value = "审核状态")
    private String approveStatus;

    @ApiModelProperty(value = "变更截止时间")
    private LocalDate changeDeadTime;

    @ApiModelProperty(value = "拒绝原因")
    private String rejectReason;
}
