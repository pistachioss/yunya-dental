package com.yunya.models.report;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 简介: 患者消费积分统计model
 *
 * @author: WY
 * @date: 2021/4/23 09:27
 * @description: 患者消费积分统计model
 * @since: 1.0.0
 */
@ApiModel("患者消费积分统计model")
@Data
public class BasePatientConsumptionCountVo {

    /**
     * 患者id
     */
    private Integer patientId;

    /**
     * 积分总额
     */
    private Integer integral;
}
