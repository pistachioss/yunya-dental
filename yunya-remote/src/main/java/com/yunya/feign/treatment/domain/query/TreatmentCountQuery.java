package com.yunya.feign.treatment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 就诊中心计数参数模型
 * @author: LHB
 * @create: 2020-11-16 16:10
 **/
@Data
@ApiModel(value = "TreatmentCountQuery",description = "就诊中心计数参数模型")
public class TreatmentCountQuery implements Serializable {
    @ApiModelProperty(value = "门诊ID")
    private Integer orgId;
    @ApiModelProperty(value = "日期")
    private String queryDate;
    @ApiModelProperty(value = "用户ID")
    private Integer userId;
}
