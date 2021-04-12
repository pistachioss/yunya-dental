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
public class MonthCategoryVO implements Serializable {
    private Integer categoryId;
    private Integer itemId;
    private Integer itemType;
    private Integer billId;
    @Excel(name = "项目大类名称")
    private String categoryName;
    @Excel(name = "项目大类金额")
    private BigDecimal actualAmount = BigDecimal.ZERO;
    @Excel(name = "当月免单金额")
    private BigDecimal freeAmount = BigDecimal.ZERO;
    @Excel(name = "项目收入")
    private BigDecimal categoryAmount = BigDecimal.ZERO;
    @Excel(name = "项目补入工作量")
    private BigDecimal couponAmount = BigDecimal.ZERO;
    @Excel(name = "合计收入")
    private BigDecimal totalAmount = BigDecimal.ZERO;
}
