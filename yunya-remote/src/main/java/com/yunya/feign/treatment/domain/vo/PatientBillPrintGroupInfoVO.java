package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 简介: 患者账单打印组合信息VO
 *
 * @author: chow
 * @date: 2021/5/19 15:26
 * @description:
 * @since: 1.0.0
 */
@ApiModel("患者账单打印组合信息VO")
@Data
@ToString
public class PatientBillPrintGroupInfoVO implements Serializable {
  /** 患者信息 */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 患者姓名 */
  @ApiModelProperty("患者姓名")
  private String patientName;
  /** 病历编号 */
  @ApiModelProperty("病历编号")
  private String medicalNum;
  /** 会员类型 */
  @ApiModelProperty("会员类型")
  private String memberType;
  /** 账单信息列表 */
  private List<PatientBillPrintInfoVO> billDetailInfos;
  /** 应收合计 */
  @ApiModelProperty("应收合计")
  private BigDecimal totalActualAmount;
  /** 优惠合计 */
  @ApiModelProperty("优惠合计")
  private BigDecimal totalPrivilegeAmount;
  /** 免单合计 */
  @ApiModelProperty("免单合计")
  private BigDecimal totalFreeAmount;
  /** 实收合计 */
  @ApiModelProperty("实收合计")
  private BigDecimal totalReceivedAmount;
  /** 欠费合计 */
  @ApiModelProperty("欠费合计")
  private BigDecimal totalDebtAmount;
  /** 门诊id */
  @ApiModelProperty("门诊ID")
  private Integer clinicId;
  /** 门诊名称 */
  @ApiModelProperty("门诊名称")
  private String clinicName;
  /** 门诊地址 */
  @ApiModelProperty("门诊地址")
  private String clinicAddress;
  /** 联系电话 */
  @ApiModelProperty("联系电话")
  private String clinicMobile;
}
