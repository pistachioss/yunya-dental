package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简单介绍:</br>
 *
 * @author: WY
 * @date 2020/7/30 9:36
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者关系推荐图")
public class PatientRecommendRelationChartQueryForm implements Serializable {

    /**
     * 患者ID
     */
    private Integer patientId;

    /**
     * 患者类型
     */
    @NotNull(message = "患者类型为空！")
    private Integer originType;

}
