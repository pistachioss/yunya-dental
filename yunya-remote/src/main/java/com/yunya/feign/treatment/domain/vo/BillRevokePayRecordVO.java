package com.yunya.feign.treatment.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

import static com.yunya.framework.common.annation.Excel.ColumnType.NUMERIC;
import static com.yunya.framework.common.annation.Excel.Type.EXPORT;

/**
 * 简介: 账单撤销收费记录VO
 *
 * @author: chow
 * @date: 2021/1/7 15:44
 * @description:
 * @since: 1.0.0
 */
@ApiModel("账单撤销收费记录VO")
@Data
@ToString
public class BillRevokePayRecordVO implements Serializable {
  /** 账单异常记录ID */
  @ApiModelProperty("账单异常记录ID")
  private Integer billExceptionHandleRecordId;
  /** 调整日期 */
  @Excel(name = "撤销日期")
  @ApiModelProperty("撤销日期")
  private String revokeDate;
  /** 账单ID */
  @ApiModelProperty("账单ID")
  private Integer billId;
  /** 账单日期 */
  @Excel(name = "账单日期")
  @ApiModelProperty("账单日期")
  private String billDate;
  /** 账单编号 */
  @Excel(name = "账单编号")
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 当月/非当月账单 */
  private String currentMonthBill;
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
  private String patientMobile;
  /** 挂号医生ID */
  @ApiModelProperty("挂号医生ID")
  private Integer regDentistId;
  /** 挂号医生姓名 */
  @Excel(name = "挂号医生")
  @ApiModelProperty("挂号医生姓名")
  private String regDentistName;
  /** 应收金额 */
  @Excel(name = "应收金额", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("应收金额")
  private BigDecimal actualAmount;
  /** 收费记录ID */
  private Integer billPayId;
  /** 收费日期 */
  @Excel(name = "收费日期")
  @ApiModelProperty("收费日期")
  private String chargeDate;
  /** 本次收费金额 */
  @Excel(name = "本次收费金额", cellType = NUMERIC, type = EXPORT, isStatistics = true)
  @ApiModelProperty("本次收费金额")
  private BigDecimal receivedAmount;
  /** 调整人ID */
  @ApiModelProperty("撤销人ID")
  private Integer revokeOperatorId;
  /** 调整人姓名 */
  @Excel(name = "撤销人")
  @ApiModelProperty("撤销人姓名")
  private String revokeOperatorName;
}
