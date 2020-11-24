package com.yunya.feign.treatment.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 账单调整记录VO
 *
 * @author: chow
 * @date: 2020/11/23 15:50
 * @description:
 * @since: 1.0.0
 */
@ApiModel("账单调整记录VO")
@Data
@ToString
public class BillOfAdjustRecordVO implements Serializable {
  /** 账单异常记录ID */
  @ApiModelProperty("账单异常记录ID")
  private Integer billExceptionHandleRecordId;
  /** 调整日期 */
  @Excel(name = "调整日期")
  @ApiModelProperty("调整日期")
  private String adjustDate;
  /** 开单日期 */
  @Excel(name = "开单日期")
  @ApiModelProperty("开单日期")
  private String orderDate;
  /** 账单编号 */
  @Excel(name = "账单编号")
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 患者姓名 */
  @Excel(name = "患者姓名")
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 患者姓名拼音 */
  @ApiModelProperty("患者姓名拼音")
  private String pinyinName;
  /** 患者性别 */
  @ApiModelProperty("患者性别：0-男；1-女")
  private Byte gender;
  /** 患者手机号 */
  @Excel(name = "手机号")
  @ApiModelProperty("患者手机号")
  private String mobile;
  /** 挂号医生ID */
  @ApiModelProperty("挂号医生ID")
  private Integer regDentistId;
  /** 挂号医生姓名 */
  @Excel(name = "挂号医生")
  @ApiModelProperty("挂号医生姓名")
  private String regDentistName;
  /** 调整人ID */
  @ApiModelProperty("调整人ID")
  private Integer adjustOperatorId;
  /** 调整人姓名 */
  @Excel(name = "调整人")
  @ApiModelProperty("调整人姓名")
  private String adjustOperatorName;
}
