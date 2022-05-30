package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：患者自助登记查询模型
 *
 * @author: chenlin
 * @Description: 患者自助登记查询模型
 * @Date: 2022/5/26 17:19
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者自助登记查询模型")
public class PatientRegistrationQueryForm implements Serializable {

    /**患者id */
    @ApiModelProperty(value = "患者id",required = true)
    @NotNull(message = "患者id不能为空")
    private Integer patientId;
}
