package com.yunya.feign.patient_central.domain.vo.web;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/2/6 16:59
 * @description: 患者预付款账户类型数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者预付款账户类型数据模型")
public class PatientPrepaymentTypeVO implements Serializable {

    /** 类型 */
    @ApiModelProperty("类型")
    private Integer type;
    
    /** 名称 */
    @ApiModelProperty("名称")
    private String name;
}
