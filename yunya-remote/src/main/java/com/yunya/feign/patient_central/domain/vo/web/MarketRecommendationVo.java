package com.yunya.feign.patient_central.domain.vo.web;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

import static com.yunya.framework.common.annation.Excel.ColumnType.NUMERIC;
import static com.yunya.framework.common.annation.Excel.ColumnType.STRING;
import static com.yunya.framework.common.annation.Excel.Type.EXPORT;

/**
 * 简介: 市场推荐列表返回模板
 *
 * @author: WY
 * @date: 2021/5/27 11:11
 * @description:
 * @since: 1.0.0
 */
@ApiModel(value = "MarketRecommendationVo",description = "市场推荐列表返回模板")
@Data
public class MarketRecommendationVo implements Serializable {
    /** 活动id */
    @ApiModelProperty(value = "活动id", required = false)
    private Integer activityIds;

    /** 活动名称 */
    @Excel(name = "活动名称")
    @ApiModelProperty(value = "活动名称", required = false)
    private String activityName;

    /** 实收金额合计 */
    @Excel(name = "实收金额合计", cellType = NUMERIC, isStatistics = true, type = EXPORT)
    @ApiModelProperty(value = "实收金额合计", required = false)
    private BigDecimal totalAmountPaid = new BigDecimal(0);

    /** 其中免单支付工作量合计 */
    @Excel(name = "其中免单支付合计", cellType = NUMERIC, isStatistics = true, type = EXPORT)
    @ApiModelProperty(value = "其中免单支付合计", required = false)
    private BigDecimal totalFreePayment = new BigDecimal(0);

    /** 退费金额合计 */
    @Excel(name = "退费金额合计", cellType = NUMERIC, isStatistics = true, type = EXPORT)
    @ApiModelProperty(value = "退费金额合计", required = false)
    private BigDecimal totalRefundAmount = new BigDecimal(0);
}