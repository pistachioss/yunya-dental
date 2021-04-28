package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

import static com.yunya.framework.common.annation.Excel.ColumnType.NUMERIC;
import static com.yunya.framework.common.annation.Excel.Type.EXPORT;

/**
 * 简介: 开单项目统计明细VO
 *
 * @author: chow
 * @date: 2020/12/7 14:26
 * @description:
 * @since: 1.0.0
 */
@ApiModel("开单项目统计明细VO")
@Data
@ToString
public class BillingItemDetailVO implements Serializable {
  /** 账单明细ID */
  @ApiModelProperty("账单明细ID")
  private Integer billDetailId;
  /** 开单日期 */
  @ApiModelProperty("开单日期")
  private String orderDate;
  /** 账单日期 */
  @Excel(name = "账单日期")
  @ApiModelProperty("账单日期")
  private String billDate;
  /** 账单编号 */
  @Excel(name = "账单编号")
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 开单数量 */
  @Excel(name = "开单数量", cellType = NUMERIC, isStatistics = true, type = EXPORT)
  @ApiModelProperty("开单数量")
  private Integer quantity;
  /** 挂号医生ID */
  @ApiModelProperty("挂号医生ID")
  private Integer regDentistId;
  /** 挂号医生 */
  @Excel(name = "挂号医生")
  @ApiModelProperty("挂号医生")
  private String regDentistName;
  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 患者姓名 */
  @Excel(name = "患者姓名")
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 手机号 */
  @Excel(name = "手机号")
  @ApiModelProperty("手机号")
  private String mobile;
}
