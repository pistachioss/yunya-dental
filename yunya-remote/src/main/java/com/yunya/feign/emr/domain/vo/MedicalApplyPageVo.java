package com.yunya.feign.emr.domain.vo;

import com.fasterxml.jackson.annotation.*;
import io.swagger.annotations.*;
import lombok.*;

import java.time.*;

/**
 * @author xiangyang
 * @date 2020/8/7
 */
@Getter
@Setter
@ApiModel(value = "草稿病例申请分页模型")
public class MedicalApplyPageVo {

    @ApiModelProperty(value = "审核id")
    private Integer id;

    @ApiModelProperty(value = "电子病例Id")
    private Integer eventId;

    @ApiModelProperty(value = "患者id")
    private Integer patientId;

    @ApiModelProperty(value = "患者姓名")
    private String patientName;

    @ApiModelProperty(value = "病历号")
    private String medicalNum;

    @ApiModelProperty(value = "就诊门诊")
    private String treatmentClinicName;

    @ApiModelProperty(value = "主治医生")
    private String majorDentistName;

    @ApiModelProperty(value = "就诊日期")
    private LocalDate treatmentDate;

    @ApiModelProperty(value = "草稿病例提交时间", example = "yyyy-MM-dd HH:mm")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private LocalDateTime submitTime;

    @ApiModelProperty(value = "审核状态（0：待审批  1：同意 2：拒绝）")
    private Integer approveStatus;

    @ApiModelProperty(value = "拒绝原因")
    private String rejectReason;

    @ApiModelProperty(value = "修改截止时间，页面不显示，只做按钮判断用")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime modifyDeadTime;
}
