package com.yunya.feign.treatment_other.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author: chenlin
 * @date: 2023/9/4 13:52
 * @description: 项目组合查询模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("项目组合查询模型")
public class TariffPackageQuery extends PageQuery {
    
    /** 名称（模糊查询） */
    @ApiModelProperty("名称（模糊查询）")
    private String name;
}
