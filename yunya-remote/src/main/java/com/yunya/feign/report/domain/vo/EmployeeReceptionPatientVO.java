package com.yunya.feign.report.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: chenlin
 * @date: 2022/10/25 10:47
 * @description: 个人接诊患者数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("个人接诊患者数据模型")
public class EmployeeReceptionPatientVO implements Serializable {

    /** 序号 */
    @Excel(name = "序号")
    @ApiModelProperty("序号")
    private Integer seqNum;

    /** 患者姓名 */
    @Excel(name = "患者姓名")
    @ApiModelProperty("患者姓名")
    private String patientName;

    /** 患者手机号 */
    @Excel(name = "患者手机号")
    @ApiModelProperty("患者手机号")
    private String mobile;

    /** 患者病历号 */
    @Excel(name = "患者病历号")
    @ApiModelProperty("患者病历号")
    private String medicalNumber;

    /** 患者出生日期 */
    @Excel(name = "患者出生日期", dateFormat = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty("患者出生日期")
    private Date birthday;

    /** 就诊门诊 */
    @Excel(name = "就诊门诊")
    @ApiModelProperty("就诊门诊")
    private String abbreviation;

    /** 就诊挂号医生 */
    @Excel(name = "就诊挂号医生")
    @ApiModelProperty("就诊挂号医生")
    private String dentistName;

    /** 患者末次就诊日期 */
    @Excel(name = "患者末次就诊日期", dateFormat = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty("患者末次就诊日期")
    private Date lastVisitDate;
}
