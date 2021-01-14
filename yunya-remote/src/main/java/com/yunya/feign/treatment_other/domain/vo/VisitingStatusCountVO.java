package com.yunya.feign.treatment_other.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介：随访状态统计响应模型
 *
 * @author: chenlin
 * @Description: 随访状态统计响应模型
 * @Date: 2021/1/14 11:27
 * @since: 1.0.0
 */
@ApiModel("随访状态统计响应模型")
@Data
@ToString
public class VisitingStatusCountVO implements Serializable {

    /**
     * 已随访条数
     */
    @ApiModelProperty("已随访条数")
    private Integer visitedCount;

    /**
     * 未随访条数
     */
    @ApiModelProperty("未随访条数")
    private Integer unVisitCount;
}
