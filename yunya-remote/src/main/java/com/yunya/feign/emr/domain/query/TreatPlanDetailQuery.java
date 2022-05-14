package com.yunya.feign.emr.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介：治疗计划明细查询模型
 *
 * @author: chenlin
 * @Description: 治疗计划明细查询模型
 * @Date: 2022/5/10 15:52
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("治疗计划明细查询模型")
public class TreatPlanDetailQuery extends PageQuery implements Serializable {
    /** 治疗计划id*/
    @ApiModelProperty(value = "治疗计划id", required = true)
    @NotNull(message = "治疗计划id不能为空")
    private Integer planId;

    /** 治疗计划详情id*/
    @ApiModelProperty(value = "治疗计划详情id")
    private Integer planDetailId;
}
