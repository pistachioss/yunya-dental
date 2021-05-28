package com.yunya.feign.report.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 简介: 市场推荐查询模板
 *
 * @author: WY
 * @date: 2021/5/25 13:06
 * @description:
 * @since: 1.0.0
 */
@ApiModel("市场推荐查询详情模板")
@Data
public class MarketRecommendationDetailedQueryFrom implements Serializable {

    @ApiModelProperty(value = "是否分页", required = true)
    private Boolean whetherPage = true;

    @ApiModelProperty("页码")
    @Min(message = "最小值", value = 1)
    private Integer pageNum = 1;

    @ApiModelProperty("每页显示数量")
    @Min(message = "最小值", value = 1)
    private Integer pageSize = 10;

    /** 开始时间 */
    @ApiModelProperty(value = "开始时间", example = "yyyy-MM-dd")
    private String startDate;

    /** 结束时间 */
    @ApiModelProperty(value = "结束时间", example = "yyyy-MM-dd")
    @NotNull(message = "结束时间不能为空！")
    private String endDate;

    /**
     * 活动id
     */
    @ApiModelProperty(value ="活动id",required = false)
    private Integer activityIds;

}