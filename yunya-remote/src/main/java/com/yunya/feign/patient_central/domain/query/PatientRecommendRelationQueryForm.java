package com.yunya.feign.patient_central.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简单介绍:</br>患者推荐关系QueryForm
 *
 * @author: WY
 * @date 2020/7/29 13:35
 * @description: 患者关系推荐信息查询模型类
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者关系推荐")
public class PatientRecommendRelationQueryForm implements Serializable {

    /**
     * ID
     */
    @ApiModelProperty(value = "推荐关系信息id")
    private Integer Id;

    /**
     * 患者ID
     */
    @NotNull(message = "患者ID不能为空！")
    @ApiModelProperty(value = "患者id",required = true)
    private Integer patientId;

    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;
}
