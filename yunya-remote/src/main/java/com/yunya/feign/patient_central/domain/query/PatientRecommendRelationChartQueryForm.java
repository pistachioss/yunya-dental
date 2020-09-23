package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简单介绍:</br> 患者关系推荐图
 *
 * @author: WY
 * @date 2020/7/30 9:36
 * @description: 患者关系推荐图 QueryForm
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者关系推荐图")
public class PatientRecommendRelationChartQueryForm implements Serializable {

    /**
     * 患者ID
     */
    @ApiModelProperty(value ="患者ID",required = true)
    private Integer patientId;

    /**
     * 患者类型
     */
    @ApiModelProperty(value ="患者来源类型 患者来源分类ID(这里需要患者推荐类型)",required = true)
    @NotNull(message = "患者类型为空！")
    private Integer originType;

}
