package com.yunya.feign.patient_central.domain.vo.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 简介：患者儿童属性信息VO
 *
 * @author: chenlin
 * @Description: 患者儿童属性信息VO
 * @Date: 2022/3/2 16:27
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者儿童属性信息VO")
public class PatientChildInfoVO implements Serializable {
    /** 学校 */
    @ApiModelProperty("学校")
    private String school;

    /** 年级 */
    @ApiModelProperty("年级")
    private String grade;

    /** 父母or监护人姓名 */
    @ApiModelProperty(value = "父母or监护人姓名")
    private String guardian;

    /** 父母or监护人联系电话 */
    @ApiModelProperty(value = "父母or监护人联系电话")
    private String guardianPhone;

    /** 服药史 */
    @ApiModelProperty("服药史")
    private String medicationHistory;

    /** 饮食结构 */
    @ApiModelProperty("饮食结构")
    private String diet;

    /** 牙齿萌生的情况 */
    @ApiModelProperty("牙齿萌生的情况")
    private String toothSprouting;

    /** 牙齿清洁情况 */
    @ApiModelProperty("牙齿清洁情况")
    private String toothClearliness;

    /** 每天刷牙次数 */
    @ApiModelProperty("每天刷牙次数")
    private Short brushingTimes;

    /** 使用的牙膏含氟吗：0-否，1-是 */
    @ApiModelProperty("使用的牙膏含氟吗：0-否，1-是")
    private Boolean usedFluorideToothpaste;

    /** 使用牙线吗：0-有，1-无，2-偶尔 */
    @ApiModelProperty("使用牙线吗：0-有，1-无，2-偶尔")
    private Byte usedDentalFloss;

    /** 每周使用牙线次数 */
    @ApiModelProperty("每周使用牙线次数")
    private Integer useFlossTimes;

    private String parentHasCarieStr;

    /** 父母是否有龋齿：0-父有，1-父无，2-母有，3-母无 */
    @ApiModelProperty("父母是否有龋齿：0-父有，1-父无，2-母有，3-母无")
    private List<Integer> parentHasCaries;

    /** 母亲孕期情况 */
    @ApiModelProperty("母亲孕期情况")
    private String motherPregnancy;

    private String habitIdStr;

    /** 有或曾经有过以下习惯 */
    @ApiModelProperty("有或曾经有过以下习惯, 习惯id")
    private List<Integer> habitIds;

    /** 最近一次检查牙齿日期 */
    @ApiModelProperty("最近一次检查牙齿日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date toothLastCheck;
}
