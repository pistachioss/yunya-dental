package com.yunya.feign.emr.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 简介：治疗计划类型统计查询模型
 *
 * @author: chenlin
 * @Description: 治疗计划类型统计查询模型
 * @Date: 2022/4/22 15:52
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("治疗计划类型统计查询模型")
public class PlanTypeStatisticsQuery extends PageQuery implements Serializable {
    /** 门诊id*/
    @ApiModelProperty(value = "门诊id", required = true)
    @NotNull(message = "门诊不能为空")
    private List<Integer> orgIds;

    /** 时间类型 */
    @ApiModelProperty(value = "时间类型:0-日；1-月；2-年", required = true)
    @NotNull(message = "时间类型不能为空")
    private Byte dateType = 0;

    /** 查询时间 */
    @ApiModelProperty(value = "查询开始时间", required = true)
    @NotEmpty(message = "开始时间不能为空")
    private String startDate;

    /** 查询结束时间 */
    @ApiModelProperty(value = "查询结束时间", required = true)
    @NotEmpty(message = "结束时间不能为空")
    private String endDate;

    /** 治疗计划类型id*/
    @ApiModelProperty("治疗计划类型id")
    private Integer planTypeId;
}
