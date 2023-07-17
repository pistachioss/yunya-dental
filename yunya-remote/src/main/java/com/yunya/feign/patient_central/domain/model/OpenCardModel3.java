package com.yunya.feign.patient_central.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 简单介绍:</br> 原藤卡激活Model
 *
 * @author: WY
 * @date 2020/8/14 13:55
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("原藤卡激活")
public class OpenCardModel3 implements Serializable {
    /**
     * 患者ID
     */
    @NotNull(message = "患者ID不能为空")
    @ApiModelProperty(value = "患者ID",required = true)
    private Integer patientId;

    /**
     * 待转化患者ID列表
     */
    @ApiModelProperty(value = "待转化患者ID列表")
    private List<Integer> transformPatientIds;
}
