package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2021/11/18
 * @description: 公司端-产品使用报表参数
 */
@ApiModel(value = "划扣结存查询")
@Data
public class DeductionPeriodQuery extends PageQuery {
    @ApiModelProperty(value = "产品Id")
    private List<String> couponIds;
    @ApiModelProperty(value = "门诊Id")
    private String orgIds;
    @ApiModelProperty(value = "开始时间")
    @NotNull(message = "开始时间不能为空")
    private String startDate;
    @ApiModelProperty(value = "结束时间")
    @NotNull(message = "结束时间不能为空")
    private String endDate;
}
