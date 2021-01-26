package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 未结账订单信息VO
 *
 * @author: chow
 * @date: 2021/1/26 14:13
 * @description:
 * @since: 1.0.0
 */
@ApiModel("未结账订单信息VO")
@Data
@ToString
public class BillRecordOfUncheckedVO implements Serializable {
  /** 订单记录ID */
  @ApiModelProperty(value = "订单记录ID")
  private Integer id;
  /** 开单门诊ID */
  @ApiModelProperty(value = "开单门诊ID")
  private Integer orgId;
  /** 开单门诊名称 */
  @ApiModelProperty(value = "开单门诊名称")
  private String orgName;
  /** 订单编号 */
  @ApiModelProperty(value = "订单编号")
  private String orderRecordNum;
  /** 原价合计 */
  @ApiModelProperty(value = "原价合计")
  private BigDecimal totalAmount;
  /** 订单日期 */
  @ApiModelProperty(value = "订单日期")
  private String crtTime;
  /** 患者ID */
  @ApiModelProperty(value = "患者ID")
  private Integer patientId;
  /** 患者名字 */
  @ApiModelProperty(value = "患者名字")
  private String patientName;
  /** 患者手机号 */
  @ApiModelProperty(value = "患者手机号")
  private String patientMobile;
  /** 就诊ID */
  @ApiModelProperty(value = "就诊ID")
  private Integer treatmentRecordId;
  /** 诊疗状态(0-接诊中;1-已开单;2-接诊完成3-已结账) */
  @ApiModelProperty(value = "诊疗状态(0-接诊中;1-已开单;2-接诊完成3-已结账)")
  private Integer treatmentStatus;
  /** 开单人ID */
  @ApiModelProperty("开单人ID")
  private Integer crtId;
  /** 开单人姓名 */
  @ApiModelProperty(value = "开单人姓名")
  private String crtName;
}
