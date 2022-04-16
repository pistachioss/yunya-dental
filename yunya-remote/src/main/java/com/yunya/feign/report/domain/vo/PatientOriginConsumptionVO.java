package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介：渠道来源患者消费数据VO
 *
 * @author: chenlin
 * @Description: 渠道来源患者消费数据VO
 * @Date: 2021/12/27 14:41
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("渠道来源患者消费数据VO")
public class PatientOriginConsumptionVO implements Serializable {
    /** 病历号 */
    @Excel(name = "病历号")
    @ApiModelProperty("病历号")
    private String medicalNumber;

    /** 患者 */
    @Excel(name = "患者姓名")
    @ApiModelProperty("患者姓名")
    private String patientName;

    /** 联系电话 */
    @Excel(name = "联系电话")
    @ApiModelProperty("联系电话")
    private String mobile;

    /** 年龄 */
    @Excel(name = "年龄")
    @ApiModelProperty("年龄")
    private Integer age;

    /** 性别：0-男, 1-女, 2-未知 */
    @Excel(name = "性别", readConverterExp = "0=男,1=女,2=未知")
    @ApiModelProperty("性别：0-男, 1-女, 2-未知")
    private Integer gender;

    /** 会员身份 */
    @Excel(name = "会员身份")
    @ApiModelProperty("会员身份")
    private String memberLevelName;

    /** 患者来源分类 */
    @Excel(name = "患者来源分类")
    @ApiModelProperty("患者来源分类")
    private String originTypeName;

    /** 患者来源 */
    @Excel(name = "患者来源")
    @ApiModelProperty("患者来源")
    private String originName;

    /** 所筛选时间段内消费累计 */
    @Excel(name = "所筛选时间段内消费累计")
    @ApiModelProperty("所筛选时间段内消费累计")
    private BigDecimal receivedAmount;

    /** 消费总额 */
    @Excel(name = "消费总额")
    @ApiModelProperty("消费总额")
    private BigDecimal cumulativeConsumption;

    /** 欠费总额 */
    @Excel(name = "欠费总额")
    @ApiModelProperty("欠费总额")
    private BigDecimal totalArrears;

    /** 初诊门诊 */
    @Excel(name = "初诊门诊")
    @ApiModelProperty("初诊门诊")
    private String abbreviation;

    /** 初诊日期 */
    @Excel(name = "初诊日期")
    @ApiModelProperty("初诊日期")
    private String firstVisitDate;

    /** 初诊医生 */
    @Excel(name = "初诊医生")
    @ApiModelProperty("初诊医生")
    private String firstVisitDoctor;

    /** 末诊日期 */
    @Excel(name = "末诊日期")
    @ApiModelProperty("末诊日期")
    private String lastVisitDate;

    /** 末诊医生 */
    @Excel(name = "末诊医生")
    @ApiModelProperty("末诊医生")
    private String lastVisitDoctor;
}
