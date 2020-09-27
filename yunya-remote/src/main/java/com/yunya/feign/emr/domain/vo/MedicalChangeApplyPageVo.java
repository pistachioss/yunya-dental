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

    @ApiModelProperty(value = "患者id")
    private Integer patientId;

    @ApiModelProperty(value = "患者姓名")
    private String patientName;

    @ApiModelProperty(value = "病历号")
    private String medicalNum;

    @ApiModelProperty(value = "就诊门诊")
    private String treatmentClinicName;

    @ApiModelProperty(value = "就诊日期")
    private LocalDate treatmentDate;

    @ApiModelProperty(value = "申请类型（0：新增，1：修改）")
    private Integer applyType;

    @ApiModelProperty(value = "申请原因")
    private String applyReason;

    @ApiModelProperty(value = "审核状态（0：待审批  1：同意 2：拒绝）")
    private Integer approveStatus;

    @ApiModelProperty(value = "变更截止时间")
    private LocalDate changeDeadTime;

    @ApiModelProperty(value = "拒绝原因")
    private String rejectReason;

    @ApiModelProperty(value = "是否操作过（0：否  1：是）")
    private Integer whetherOperated;
}
