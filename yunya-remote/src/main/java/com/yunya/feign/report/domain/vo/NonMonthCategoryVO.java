package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/4/9 16:37
 * @since: 1.0.0
 */
@ToString
@Data
public class NonMonthCategoryVO implements Serializable {
    private Integer categoryId;
    private Integer itemId;
    private Integer itemType;
    private Integer billId;
    @Excel(name = "开单时间")
    private String billDate;
    @Excel(name = "患者姓名")
    private String patientName;
    @Excel(name = "开单金额")
    private BigDecimal billAmount = BigDecimal.ZERO;
    @Excel(name = "项目大类")
    private String categoryName;
    @Excel(name = "项目大类金额")
    private BigDecimal actualAmount = BigDecimal.ZERO;
    @Excel(name = "非当月免单金额")
    private BigDecimal freeBillAmount = BigDecimal.ZERO;
    @Excel(name = "免单时间")
    private String freeDate;
    @Excel(name = "非当月免单金额分摊")
    private BigDecimal freeAmount = BigDecimal.ZERO;
}
