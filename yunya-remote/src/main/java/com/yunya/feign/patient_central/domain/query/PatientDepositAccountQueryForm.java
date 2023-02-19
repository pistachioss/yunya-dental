package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author: chenlin
 * @date: 2023/2/6 10:05
 * @description: 患者储蓄账号查询模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者储蓄账号查询模型")
public class PatientDepositAccountQueryForm implements Serializable {

    /** 患者id */
    @ApiModelProperty(value = "患者id", required = true)
    @NotNull(message = "患者id不能为空")
    private Integer patientId;

    /** 储蓄账号类型列表：0-会员卡，1-预付款，2-正畸预付款，3-美白预付款 */
    @ApiModelProperty("储蓄账号类型列表：0-会员卡，1-预付款，2-正畸预付款，3-美白预付款")
    @NotEmpty(message = "储蓄账号类型列表不能为空")
    private List<Integer> types;
}