package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

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
  private String billOrgname;
  private String billDate;
  private String billNum;
  private String billPayId;
  private String payOrgName;
  private String chargeDate;
  private String patientName;
  private String patientMobile;
  private Integer orderDetailId;
  private Integer executorId;
  private Integer consulterId;
  private String executorName;
  private String consulterName;
  /** 项目ID */
  @ApiModelProperty("项目ID")
  private Integer itemId;

  /** 项目类型 */
  @ApiModelProperty("项目类型：0-价目表；1-商品")
  private Byte itemType;

  /** 门诊ID */
  @ApiModelProperty("门诊ID")
  private Integer orgId;

  /** 开单数量 */
  @ApiModelProperty("开单数量")
  private Integer quantity;

  /** 患者id */
  @ApiModelProperty("患者id")
  private Integer patientId;

  /** 开单日期*/
  @ApiModelProperty("开单日期")
  private String orderDate;
}
