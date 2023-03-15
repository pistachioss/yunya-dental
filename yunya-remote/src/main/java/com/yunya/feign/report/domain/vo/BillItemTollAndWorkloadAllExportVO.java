package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

import static com.yunya.framework.common.annation.Excel.ColumnType.NUMERIC;
import static com.yunya.framework.common.annation.Excel.Type.EXPORT;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2023/3/14
 * @description:
 */
@Data
@ToString
public class BillItemTollAndWorkloadAllExportVO {
    /** 门诊ID */
    @ApiModelProperty("门诊ID")
    private Integer orgId;
    /** 门诊名称 */
    @Excel(name = "门诊名称")
    @ApiModelProperty("门诊名称")
    private String abbreviation;
    /** 项目分类ID */
    @ApiModelProperty("项目分类ID")
    private Integer itemCategoryId;
    /** 项目明细ID */
    @ApiModelProperty("项目明细ID")
    private Integer itemId;
    /** 项目明细编号 */
    @ApiModelProperty("项目明细编号")
    private String itemNum;
    /** 项目明细ID */
    @ApiModelProperty("账单ID")
    private Integer billId;
    /** 账单编号 */
    @Excel(name = "账单编号")
    @ApiModelProperty("账单编号")
    private String orderNum;
    @Excel(name = "患者姓名")
    @ApiModelProperty("患者姓名")
    private String name;
    @Excel(name = "患者电话")
    @ApiModelProperty("患者电话")
    private String mobile;
    /** 执行人ID */
    @ApiModelProperty("执行人ID")
    private Integer executorId;
    /** 执行人姓名 */
    @Excel(name = "执行人")
    @ApiModelProperty("执行人姓名")
    private String executorName;
    /** 项目分类名称 */
    @Excel(name = "所属分类")
    @ApiModelProperty("项目分类名称")
    private String itemCategoryName;
    /** 项目明细名称 */
    @Excel(name = "项目名称")
    @ApiModelProperty("项目明细名称")
    private String itemName;
    @Excel(name = "挂号医生")
    @ApiModelProperty("挂号医生")
    private String employeeName;
    @Excel(name = "账单日期")
    @ApiModelProperty("账单日期")
    private String billDate;
    /** 开单数量 */
    @Excel(name = "开单数量", cellType = NUMERIC, isStatistics = true, type = EXPORT)
    @ApiModelProperty("开单数量")
    private Integer quantity = 0;

    /** 实收工作量 */
    @Excel(name = "实收工作量",  cellType = NUMERIC, isStatistics = true, type = EXPORT)
    @ApiModelProperty("实收工作量")
    private BigDecimal receivedWorkload = BigDecimal.ZERO;
    /** 免单工作量 */
    @Excel(name = "其中免单工作量",  cellType = NUMERIC, isStatistics = true, type = EXPORT)
    @ApiModelProperty("免单工作量")
    private BigDecimal freePayWorkload = BigDecimal.ZERO;
    /** 补入工作量 */
    @Excel(name = "补入工作量",  cellType = NUMERIC, isStatistics = true, type = EXPORT)
    @ApiModelProperty("补入工作量")
    private BigDecimal supplyWorkload = BigDecimal.ZERO;
    /** 退费工作量 */
    @Excel(name = "退费工作量",  cellType = NUMERIC, isStatistics = true, type = EXPORT)
    @ApiModelProperty("退费工作量")
    private BigDecimal refundWorkload = BigDecimal.ZERO;

    @Excel(name = "开单备注")
    @ApiModelProperty("开单备注")
    private String remark;
}
