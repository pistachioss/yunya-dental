package com.yunya.feign.patient_central.domain.vo.web;

import com.yunya.framework.common.annation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简单介绍:</br> 返回预付款退费记录信息模型
 *
 * @author: WY
 * @date 2020/8/26 16:35
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("返回预付款退费记录信息模型")
public class PrepaidMeturnRecordVo implements Serializable {

  /** 预付款退费记录id */
  @ApiModelProperty("预付款退费记录id")
  private Integer id;

  /** 操作时间 */
  @Excel(name = "操作时间")
  @ApiModelProperty("操作时间")
  private String operatingTime;

  /** 退本金金额 */
  @Excel(name = "退本金金额")
  @ApiModelProperty("退本金金额")
  private BigDecimal returnRrincipalAmount;

  /** 退赠金金额 */
  @Excel(name = "退赠金金额")
  @ApiModelProperty("退赠金金额")
  private BigDecimal returnGiftAmount;

  /** 退费方式id */
  @ApiModelProperty("退费方式id")
  private Integer returnWayId;

  /** 退费方式 */
  @Excel(name = "退费方式")
  @ApiModelProperty("退费方式")
  private String returnWayType;

  /** 实际退费金额 */
  @Excel(name = "实际退费金额")
  @ApiModelProperty("实际退费金额")
  private BigDecimal returnPayAmount;

  /** 门诊id */
  @ApiModelProperty("门诊id")
  private Integer orgId;

  /** 诊所 */
  @Excel(name = "诊所")
  @ApiModelProperty("诊所")
  private String orgName;

  /** 操作人id */
  @ApiModelProperty("操作人id")
  private Integer operatorId;

  /** 操作人员 */
  @Excel(name = "操作人员")
  @ApiModelProperty("操作人员")
  private String operatorName;

  /** 备注 */
  @Excel(name = "备注")
  @ApiModelProperty("备注")
  private String remarks;
}
