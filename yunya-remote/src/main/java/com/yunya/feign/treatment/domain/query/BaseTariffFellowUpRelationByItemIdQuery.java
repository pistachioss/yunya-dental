package com.yunya.feign.treatment.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @Class BaseTariffFellowUpRelationByItemIdQuery
 * @Description 根据项目ID查询随访信息请求参数
 * @Author lihuibin
 * @Date 2022/8/20 10:53
 * @Version 1.0
 */
@Data
@ApiModel("根据项目ID查询随访信息请求参数")
public class BaseTariffFellowUpRelationByItemIdQuery extends PageQuery {
    @ApiModelProperty("基础项目ID")
    @NotNull(message = "基础项目ID不能为空")
    @Min(value = 1,message = "基础项目ID不存在")
    private Integer itemId;
}
