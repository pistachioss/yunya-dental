package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @program: yunya-dental
 * @description: 账单打印信息VO
 * @author: LHB
 * @create: 2020-11-24 12:46
 */
@ApiModel(value = "BillPrintInfoVO", description = "账单打印信息VO")
@Data
public class BillPrintInfoVO implements Serializable {
  @ApiModelProperty("患者ID")
  private Integer patientId;

  @ApiModelProperty("患者姓名")
  private String patientName;

  @ApiModelProperty("性别：0-男；1-女；2-未知")
  private Byte gender;

  @ApiModelProperty("生日")
  private String birthday;

  @ApiModelProperty("年龄")
  private Integer age;

  @ApiModelProperty("电子病历号")
  private String medicalNumber;

  @ApiModelProperty("会员卡类型ID")
  private Integer memberTypeId;

  @ApiModelProperty("会员卡名词")
  private String memberTypeName;

  @ApiModelProperty("本单优惠总额")
  private BigDecimal totalPrivilegeAmount;

  @ApiModelProperty("应收金额（消费总额）")
  private BigDecimal totalReceivableAmount;

  @ApiModelProperty("实际应收金额")
  private BigDecimal totalActualReceivableAmount;

  @ApiModelProperty("实收金额（本单收费总额）")
  private BigDecimal totalReceivedAmount;

  @ApiModelProperty("欠费金额（本单欠费）")
  private BigDecimal totalDebtAmount;

  @ApiModelProperty("发票编号")
  private String invoiceNumber;

  @ApiModelProperty("账单记录ID")
  private Integer orderRecordId;

  @ApiModelProperty("账单日期")
  private String crtTime;

  @ApiModelProperty("账单明细打印列表")
  private List<BillDetailPrintInfoVO> billDetail;

  @ApiModelProperty("支付方式以及金额列表")
  private List<Map> billPayTypeList;
}
