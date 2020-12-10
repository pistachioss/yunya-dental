package com.yunya.feign.report.domain.vo;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 患者催缴欠费信息VO
 *
 * @author: chow
 * @date: 2020/12/10 09:40
 * @description:
 * @since: 1.0.0
 */
@ApiModel("患者催缴欠费信息VO")
@Data
@ToString
public class PatientArrearsCallForVO implements Serializable {
  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 患者姓名 */
  @Excel(name = "患者姓名")
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 性别 */
  @ApiModelProperty("性别")
  private Byte gender;
  /** 手机号 */
  @Excel(name = "手机号")
  @ApiModelProperty("手机号")
  private String mobile;
  /** 实收金额合计 */
  @Excel(name = "实收金额合计")
  @ApiModelProperty("实收金额合计")
  private BigDecimal totalActualAmount;
  /** 已收金额合计 */
  @Excel(name = "已收金额合计")
  @ApiModelProperty("已收金额合计")
  private BigDecimal totalReceivedAmount;
  /** 剩余欠费金额合计 */
  @Excel(name = "剩余欠费金额合计")
  @ApiModelProperty("剩余欠费金额合计")
  private BigDecimal totalRemainingArrearsAmount;
}
