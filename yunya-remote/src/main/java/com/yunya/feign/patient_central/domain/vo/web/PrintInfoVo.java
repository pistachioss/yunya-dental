package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @program: yunya-dental
 * @description: 打印信息模型
 * @author: LHB
 * @create: 2020-11-17 10:54
 **/
@ApiModel(value = "PrintInfoVo",description = "打印信息模型")
@Data
public class PrintInfoVo implements Serializable {
    @ApiModelProperty(value = "患者名字")
    private String patientName;
    @ApiModelProperty(value = "病历编号")
    private String medicalNumber;
    @ApiModelProperty(value = "会员类型名称")
    private String memberTypeName;
    @ApiModelProperty(value = "末诊日期")
    private Date lastTreatmentDate;
    @ApiModelProperty(value = "病历记录详情")
    private List<PatientMedicalRecordDetailVo> medicalRecordDetails;
}
