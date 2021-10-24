package com.yunya.feign.treatment_other.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * @description: 治疗计划查询参数模型
 * @author: chenl
 * @create: 2021-10-19 18:33
 **/
@ApiModel(description = "治疗计划查询参数模型")
@Data
public class TreatPlanQuery implements Serializable {
    @ApiModelProperty("是否开启分页，默认开启")
    private Boolean whetherPage = true;
    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    @ApiModelProperty(value = "患者ID")
    private Integer patientId;
}
