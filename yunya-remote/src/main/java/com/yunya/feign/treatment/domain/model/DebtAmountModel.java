package com.yunya.feign.treatment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: yunya-dental
 * @description: 欠费金额实体模型
 * @author: LHB
 * @create: 2020-11-16 19:22
 **/
@Data
@ApiModel(value = "DebtAmountModel",description = "欠费金额实体模型")
public class DebtAmountModel implements Serializable {
    @ApiModelProperty(value = "患者ID")
    private Integer patientId;
    @ApiModelProperty(value = "欠费总额")
    private BigDecimal debtAmount;

}
