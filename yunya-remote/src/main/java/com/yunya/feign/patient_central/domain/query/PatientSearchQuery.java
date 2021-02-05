package com.yunya.feign.patient_central.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 患者搜索参数模型
 *
 * @author: chow
 * @date: 2021/1/30 15:59
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者搜索参数模型")
@EqualsAndHashCode(callSuper = true)
public class PatientSearchQuery extends PageQuery implements Serializable {
    /***/
    @ApiModelProperty("患者关键字（姓名/手机号/拼音姓名/病历号）")
    private String patientKeyWord;
}