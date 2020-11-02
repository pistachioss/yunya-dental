package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import java.io.Serializable;

/**
 * 简介:添加标签记录信息Model
 *
 * @author: WY
 * @date: 2020/10/31 17:51
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("添加标签记录信息参数模型")
public class PatientLabelRecordModel implements Serializable {

    /**
     * 患者ID
     */
    private Integer patientId;

    /**
     * 字典明细
     */
    private Integer dictItemId;

    /**
     * 备注
     */
    private String remarks;
}