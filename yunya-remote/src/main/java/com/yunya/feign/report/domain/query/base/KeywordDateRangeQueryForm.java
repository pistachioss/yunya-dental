package com.yunya.feign.report.domain.query.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author: chenlin
 * @date: 2023/9/25 16:18
 * @description: 关键词模糊查询模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("关键词模糊查询模型")
public class KeywordDateRangeQueryForm extends DateRangeQueryForm {

    /** 关键词模糊查询 */
    @ApiModelProperty("关键词模糊查询")
    private String keyword;
}
