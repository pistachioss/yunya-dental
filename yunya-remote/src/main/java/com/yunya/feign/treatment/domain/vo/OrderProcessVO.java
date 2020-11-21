package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: yunya-dental
 * @description: 订单处理视图模型(门诊管理-订单管理)
 * @author: LHB
 * @create: 2020-11-10 10:21
 */
@ApiModel(value = "OrderProcessVO", description = "订单处理视图模型(门诊管理-订单管理)")
@Data
public class OrderProcessVO implements Serializable {
  @ApiModelProperty(value = "订单记录ID")
  private Integer id;
  @ApiModelProperty(value = "订单编号")
  private String orderRecordNum;
  @ApiModelProperty(value = "订单日期")
  private String crtTime;
  @ApiModelProperty(value = "患者ID")
  private Integer patientId;
  @ApiModelProperty(value = "患者名字")
  private String patientName;
  @ApiModelProperty(value = "患者手机号")
  private String patientMobile;
  @ApiModelProperty(value = "原价合计")
  private BigDecimal totalAmount;
  @ApiModelProperty(value = "开单门诊ID")
  private Integer orgId;
  @ApiModelProperty(value = "开单门诊名称")
  private String orgName;
  @ApiModelProperty(value = "开单人")
  private String crtName;
  @ApiModelProperty(value = "就诊ID")
  private Integer treatmentRecordId;
  @ApiModelProperty(value = "诊疗状态(0-接诊中;1-已开单;2-接诊完成3-已结账)")
  private Integer treatmentStatus;
  @ApiModelProperty(value = "订单状态 （0-账单未锁定 ；1-账单锁定；2-结算完成状态；3-收费中）")
  private Integer status;
}
