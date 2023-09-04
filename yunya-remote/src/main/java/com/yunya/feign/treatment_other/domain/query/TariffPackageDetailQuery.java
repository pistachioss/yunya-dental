package com.yunya.feign.treatment_other.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author: chenlin
 * @date: 2023/9/4 13:52
 * @description: 项目组合明细查询模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("项目组合明细查询模型")
public class TariffPackageDetailQuery extends PageQuery {
    
    /** 组合id */
    @ApiModelProperty(value = "组合id", required = true)
    @NotNull(message = "组合id不能为空")
    private Integer packageId;
}
