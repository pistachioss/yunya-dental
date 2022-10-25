package com.yunya.feign.patient_central.domain.vo.web;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private String lastTreatmentDate;
    @ApiModelProperty(value = "病历记录详情")
    private List<PatientMedicalRecordDetailVo> medicalRecordDetails;


    @ApiModelProperty(value = "性别 0-男；1-女；2-未知")
    private Byte gender;
    @ApiModelProperty(value = "年龄")
    private Integer age;
    @ApiModelProperty(value = "电话")
    private String mobile;
    @ApiModelProperty("家庭详细地址")
    private String address;
    @ApiModelProperty("省")
    private String province;
    @ApiModelProperty("市")
    private String city;
    @ApiModelProperty("区")
    private String country;
}
