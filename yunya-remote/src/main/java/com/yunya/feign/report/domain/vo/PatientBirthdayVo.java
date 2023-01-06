package com.yunya.feign.report.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description:
 * @author: xy
 * @date 2021/5/31 16:14
 **/
@Data
@ApiModel(value = "公司端-患者管理返回")
public class PatientBirthdayVo {
    @ExcelIgnore
    @ApiModelProperty(value = "患者ID")
    private Integer patientId;
    @ApiModelProperty(value = "末诊门诊")
    @ExcelProperty(value = "末诊门诊")
    private String lastVisitOutpatient;
    @ApiModelProperty(value = "患者姓名")
    @ExcelProperty(value = "患者姓名")
    private String patientName;
    @ApiModelProperty(value = "性别")
    @ExcelProperty(value = "性别")
    private String gender;
    @ApiModelProperty(value = "年龄")
    @ExcelProperty(value = "年龄")
    private Integer age;
    @ApiModelProperty(value = "手机号")
    @ExcelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "患者来源分类")
    @ExcelProperty(value = "患者来源分类")
    private String patientOrionTypeName;
    @ApiModelProperty(value = "患者来源")
    @ExcelProperty(value = "患者来源")
    private String patientOrionName;
    @ApiModelProperty(value = "末诊日期")
    @ExcelProperty(value = "末诊日期")
    private String lastVisitDate;
    @ApiModelProperty(value = "末诊医生")
    @ExcelProperty(value = "末诊医生")
    private String lastVisitDoctors;
    @ExcelIgnore
    @ApiModelProperty(value = "生日确认日期")
    private String birthdayCheck;
}
