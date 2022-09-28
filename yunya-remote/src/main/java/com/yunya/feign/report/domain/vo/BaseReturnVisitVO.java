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
 * @date: 2022/9/26 13:21
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("回访列表数据模型")
public class BaseReturnVisitVO implements Serializable {
    /** 就诊id */
    @ApiModelProperty("就诊id")
    private Integer treatmentId;

    /** 账单id */
    @ApiModelProperty("账单id")
    private Integer billId;

    /** 患者id */
    @ApiModelProperty("患者id")
    private Integer patientId;

    /** 门诊id */
    @ApiModelProperty("门诊id")
    private Integer orgId;

    /** 患者姓名 */
    @Excel(name = "患者姓名")
    @ApiModelProperty("患者姓名")
    private String patientName;

    /** 手机号 */
    @Excel(name = "手机号")
    @ApiModelProperty("手机号")
    private String mobile;

    /** 病历编号 */
    @Excel(name = "病历编号")
    @ApiModelProperty("病历编号")
    private String medicalNumber;

    /** 项目名称列表 */
    @Excel(name = "项目名称")
    @ApiModelProperty("项目名称")
    private String itemNames;

    /** 开单时间 */
    @Excel(name = "开单时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("开单时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date orderDate;

    /** 开单门诊 */
    @Excel(name = "开单门诊")
    @ApiModelProperty("开单门诊")
    private String abbreviation;

    /** 挂号医生 */
    @Excel(name = "挂号医生")
    @ApiModelProperty("挂号医生")
    private String dentistName;

    /** 初复诊类型：0-初诊，1-复诊 */
    @Excel(name = "初复诊", readConverterExp = "0=初诊,1=复诊")
    @ApiModelProperty("初复诊类型：0-初诊，1-复诊")
    private Integer treatType;

    /** 回访日期 */
    @Excel(name = "回访日期", dateFormat = "yyyy-MM-dd")
    @ApiModelProperty("回访日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date returnDate;

    /** 回访原因 */
    @Excel(name = "回访原因")
    @ApiModelProperty("回访原因")
    private String returnReason;

    /** 回访内容 */
    @Excel(name = "回访内容")
    @ApiModelProperty("回访内容")
    private String returnContent;

    /** 回访人 */
    @Excel(name = "回访人")
    @ApiModelProperty("回访人")
    private String registerName;
}
