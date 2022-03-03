package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简单介绍:</br> 患者基本信息+扩展信息+其他信息 参数模板
 *
 * @author: WY
 * @date 2020/7/28 11:13
 * @description:  者基本信息+扩展信息+其他信息
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者基本信息+扩展信息+其他信息")
public class PatientExtendInfoModel implements Serializable {

    /**
     * 患者基本信息表
     */
    @ApiModelProperty(value = "患者基本信息表")
    private PatientBaseInfoModel patientBaseInfoModel;

    /**
     * 患者信息扩展表
     */
    @ApiModelProperty(value = "患者信息扩展表")
    private PatientExpInfoModel patientExpInfoModel;

    /**
     * 患者其他信息表
     */
    @ApiModelProperty(value = "患者其他信息表")
    private List<PatientExtInfoModel> patientExtInfoModelList;

    /**
     * 患者儿童属性信息表
     */
    @ApiModelProperty(value = "患者信息扩展表")
    private PatientChildInfoModel patientChildInfoModel;
}
