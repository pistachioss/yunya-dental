package com.yunya.feign.report.domain.vo;

import com.github.pagehelper.PageInfo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 简介: 欠费查询Vo
 *
 * @author: WY
 * @date: 2020/12/4 20:53
 * @description:
 * @since: 1.0.0
 */
@Data
public class ArrearsStatisticsVo {
    /***/
    private PageInfo<ArrearsVo> arrearsVoList;

    /** 欠费患者合计 */
    private Integer numberPatient;

    /** 欠费账单合计 */
    private Integer arrearsCount;

    /** 剩余欠费金额合计 */
    private BigDecimal debtAmount;

}