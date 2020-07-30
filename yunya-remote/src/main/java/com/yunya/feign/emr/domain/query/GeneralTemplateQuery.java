package com.yunya.feign.emr.domain.query;

import com.yunya.framework.common.model.PageQueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author xiangyang
 * @date 2020/7/29
 */
@Setter
@Getter
@ApiModel(value = "普通模板查询参数模型（词条，范句，要点，诊断）")
public class GeneralTemplateQuery extends PageQueryParams {

    @ApiModelProperty(value = "查询条件")
    private String keyword;
}
