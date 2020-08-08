package com.yunya.feign.emr.domain.vo;

import io.swagger.annotations.*;
import lombok.*;

import java.time.*;

/**
 * @author xiangyang
 * @date 2020/8/8
 */
@Getter
@Setter
@ApiModel(value = "草稿病例审核分页模型")
public class MedicalApprovePageVo {

    @ApiModelProperty(value = "审核id")
    private Integer id;

    @ApiModelProperty(value = "电子病例Id")
    private Integer eventId;

    @ApiModelProperty(value = "患者姓名")
    private String patientName;

    @ApiModelProperty(value = "病历号")
    private String medicalNum;

    @ApiModelProperty(value = "就诊门诊")
    private String treatmentClinicName;

    @ApiModelProperty(value = "助理医生")
    private String assistantDentistName;

    @ApiModelProperty(value = "就诊日期")
    private LocalDate treatmentDate;

    @ApiModelProperty(value = "草稿病例提交时间", example = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime;

    @ApiModelProperty(value = "审核状态")
    private String approveStatus;

    @ApiModelProperty(value = "拒绝原因")
    private String rejectReason;
}
