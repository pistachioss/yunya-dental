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
public class PatientManageVo {
    @ExcelIgnore
    @ApiModelProperty(value = "患者ID")
    private Integer patientId;
    @ApiModelProperty(value = "病历编号")
    @ExcelProperty(value = "病历编号")
    private String medicalNumber;
    @ApiModelProperty(value = "患者姓名")
    @ExcelProperty(value = "患者姓名")
    private String patientName;
    @ApiModelProperty(value = "年龄")
    @ExcelProperty(value = "年龄")
    private Integer age;
    @ApiModelProperty(value = "性别")
    @ExcelProperty(value = "性别")
    private String gender;
    @ApiModelProperty(value = "手机号")
    @ExcelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "累计消费")
    @ExcelProperty(value = "累计消费")
    private BigDecimal totalConsumeAmount;
    @ApiModelProperty(value = "欠费总额")
    @ExcelProperty(value = "欠费总额")
    private BigDecimal totalOweAmount;
    @ApiModelProperty(value = "会员卡")
    @ExcelProperty(value = "会员卡")
    private String memberTypeName;
    /** 患者分组*/
    @ApiModelProperty("患者分组")
    @ExcelProperty(value = "患者分组")
    private String patientGroupName;
    @ApiModelProperty(value = "患者类型")
    @ExcelProperty(value = "患者类型")
    private String patientType;
    @ApiModelProperty(value = "会员卡余额")
    @ExcelProperty(value = "会员卡余额")
    private BigDecimal memberBalance;
    @ApiModelProperty(value = "预付款余额")
    @ExcelProperty(value = "预付款余额")
    private BigDecimal principalBalance;
    @ApiModelProperty(value = "患者来源分类")
    @ExcelProperty(value = "患者来源分类")
    private String patientOrionTypeName;
    @ApiModelProperty(value = "患者来源")
    @ExcelProperty(value = "患者来源")
    private String patientOrionName;
    @ApiModelProperty(value = "初诊日期")
    @ExcelProperty(value = "初诊日期")
    private String firstVisitDate;
    @ApiModelProperty(value = "初诊门诊")
    @ExcelProperty(value = "初诊门诊")
    private String firstVisitOutpatient;
    @ApiModelProperty(value = "初诊医生")
    @ExcelProperty(value = "初诊医生")
    private String firstVisitDoctors;
    @ApiModelProperty(value = "末诊日期")
    @ExcelProperty(value = "末诊日期")
    private String lastVisitDate;
    @ApiModelProperty(value = "末诊医生")
    @ExcelProperty(value = "末诊医生")
    private String lastVisitDoctors;
    @ApiModelProperty(value = "就诊次数")
    @ExcelProperty(value = "就诊次数")
    private Integer treatQuantity;
}
