package com.yunya.feign.report.domain.query;

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
 * 简介: 渠道来源患者消费数据查询模板
 *
 * @author: chenl
 * @date: 2022/4/19 17:15
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("渠道来源患者消费数据查询模板")
public class PatientOriginConsumptionQuery extends PageQuery implements Serializable {

    /** 患者来源分类列表 */
    @ApiModelProperty(value = "患者来源分类列表")
    private List<Integer> originTypes;

    /** 条件类型：0.年月日 1.年月 2.年 */
    @ApiModelProperty(value = "条件类型：0.年月日 1.年月 2.年",required = true)
    @NotNull(message = "日期类型不能为空")
    private Integer dateType;

    /** 开始（年-月-日） */
    @ApiModelProperty(value = "(年-月-日)开始日期yyyy-MM-dd", required = true)
    @NotEmpty(message = "开始日期不能为空")
    private String startDate;

    /** 结束（年-月-日） */
    @ApiModelProperty(value = "(年-月-日)结束日期yyyy-MM-dd", required = true)
    @NotEmpty(message = "结束日期不能为空")
    private String endDate;
}