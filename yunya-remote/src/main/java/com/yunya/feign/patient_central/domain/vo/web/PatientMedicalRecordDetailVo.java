package com.yunya.feign.patient_central.domain.vo.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 患者病历详情
 * @author: LHB
 * @create: 2020-11-17 11:14
 **/
@ApiModel(value = "PatientMedicalRecordDetailVo",description = "患者病历详情")
@Data
public class PatientMedicalRecordDetailVo implements Serializable {
    @ApiModelProperty(value = "")
    private Integer treatmentId;
    @ApiModelProperty(value = "就诊日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date treatmentDate;
    @ApiModelProperty(value = "主治医生名字")
    private String dentistName;
}
