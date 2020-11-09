package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * 简介: 操作标签记录Model
 *
 * @author: WY
 * @date: 2020/11/2 10:02
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("操作标签记录Model")
public class PatientLabelRecordModel implements Serializable {

    /**
     * 患者ID
     */
    @ApiModelProperty(value = "患者id",required = true)
    private Integer patientId;

    /**
     * 字典明细ID
     */
    @ApiModelProperty(value = "标签字典id",required = true)
    private Integer dictItemId;

    /**
     * 备注 备注
     */
    @ApiModelProperty(value = "备注",required = false)
    private String remarks;

    /**
     * 操作类型
     */
    @ApiModelProperty(value = "操作类型 0:删除，1：增加",required = true)
    private Integer operatingType;
}