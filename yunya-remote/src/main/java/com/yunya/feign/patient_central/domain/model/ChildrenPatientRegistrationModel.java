package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 简介：儿童患者登记
 *
 * @author: chenlin
 * @Description: 儿童患者登记
 * @Date: 2022/2/28 17:44
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("儿童患者登记")
public class ChildrenPatientRegistrationModel extends PatientRegistrationModel implements Serializable {

    /** 学校 */
    @ApiModelProperty("学校")
    private String school;

    /** 年级 */
    @ApiModelProperty("年级")
    private String grade;

    /** 父母or监护人姓名 */
    @ApiModelProperty(value = "父母or监护人姓名", required = true)
    @NotEmpty(message = "父母or监护人姓名不能为空")
    private String guardian;

    /** 父母or监护人联系电话 */
    @ApiModelProperty(value = "联系电话", required = true)
    @NotEmpty(message = "联系电话不能为空")
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

    /** 父亲是否有龋齿 */
    @ApiModelProperty("父亲是否有龋齿")
    private Boolean fatherHasCaries;

    /** 母亲是否有龋齿 */
    @ApiModelProperty("母亲是否有龋齿")
    private Boolean motherHasCaries;

    /** 母亲孕期情况 */
    @ApiModelProperty("母亲孕期情况")
    private String motherPregnancy;

    /** 有或曾经有过以下习惯 */
    @ApiModelProperty("有或曾经有过以下习惯, 习惯id")
    private List<Integer> habitIds;

    /** 最近一次检查牙齿日期 */
    @ApiModelProperty("最近一次检查牙齿日期")
    private Date toothLastCheck;
}
