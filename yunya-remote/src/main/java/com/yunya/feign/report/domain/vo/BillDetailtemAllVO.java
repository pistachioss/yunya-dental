package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import static com.yunya.framework.common.annation.Excel.ColumnType.NUMERIC;
import static com.yunya.framework.common.annation.Excel.ColumnType.STRING;
import static com.yunya.framework.common.annation.Excel.Type.EXPORT;

/**
 * 简介: 开单项目数量及金额VO
 *
 * @author: chow
 * @date: 2020/12/7 10:40
 * @description:
 * @since: 1.0.0
 */
@ApiModel("开单项目数量VO")
@Data
@ToString
public class BillDetailtemAllVO implements Serializable {
  private Integer billId;
  @Excel(name = "门诊名称", cellType = STRING, isStatistics = false, type = EXPORT)
  private String billOrgname;
  private Integer executorId;
  private Integer consulterId;
  private String executorName;
  @Excel(name = "员工", cellType = STRING, isStatistics = false, type = EXPORT)
  private String consulterName;
  @Excel(name = "收费日期", cellType = STRING, isStatistics = false, type = EXPORT)
  private String chargeDate;
  @Excel(name = "收费门诊", cellType = STRING, isStatistics = false, type = EXPORT)
  private String payOrgName;
  @Excel(name = "账单编号", cellType = STRING, isStatistics = false, type = EXPORT)
  private String billNum;
  @Excel(name = "订单日期", cellType = STRING, isStatistics = false, type = EXPORT)
  private String billDate;
  @Excel(name = "账单日期", cellType = STRING, isStatistics = false, type = EXPORT)
  private String orderDate;
  private String billPayId;
  @Excel(name = "患者名称", cellType = STRING, isStatistics = false, type = EXPORT)
  private String patientName;
  @Excel(name = "手机号", cellType = STRING, isStatistics = false, type = EXPORT)
  private String patientMobile;
  private Integer orderDetailId;
  /** 项目ID */
  @ApiModelProperty("项目ID")
  @Excel(name = "项目编号", cellType = STRING, isStatistics = false, type = EXPORT)
  private Integer itemId;

  /** 项目类型 */
  @ApiModelProperty("项目类型：0-价目表；1-商品")
  private Byte itemType;

  @Excel(name = "项目名称", cellType = STRING, isStatistics = false, type = EXPORT)
  private String itemName;

  /** 门诊ID */
  @ApiModelProperty("门诊ID")
  private Integer orgId;

  /** 开单数量 */
  @ApiModelProperty("开单数量")
  private Integer quantity;

  /** 患者id */
  @ApiModelProperty("患者id")
  private Integer patientId;

  @Excel(name = "实收工作量", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  private BigDecimal receivedWorkload;
}
