package com.yunya.feign.emr.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2022/1/18 14:46
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者知情同意书头部VO")
public class PatientInformedConsentVO implements Serializable {
    /** 患者姓名*/
    @ApiModelProperty("患者姓名")
    private String patientName;

    /** 性别 0-男；1-女；2-未知 */
    @ApiModelProperty("性别 0-男；1-女；2-未知")
    private Byte gender;

    /** 患者出生日期 */
    @ApiModelProperty("患者生日")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date birthday;

    /** 病历号 患者第一次就诊时生成 */
    @ApiModelProperty("病历号")
    private String medicalNumber;

    /** 就诊日期*/
    @ApiModelProperty(value = "就诊日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date treatmentDate;

    /** 医生*/
    @ApiModelProperty(value = "医生")
    private String dentistName;

    /** 日期*/
    @ApiModelProperty(value = "日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date date;
}
