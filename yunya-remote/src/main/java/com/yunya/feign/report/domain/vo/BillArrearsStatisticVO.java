package com.yunya.feign.report.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 简介: 账单欠费统计VO
 *
 * @author: chow
 * @date: 2020/12/10 14:36
 * @description:
 * @since: 1.0.0
 */
@ApiModel("账单欠费统计VO")
@Data
@ToString
public class BillArrearsStatisticVO implements Serializable {
  /** 欠费患者人数 */
  @ApiModelProperty("欠费患者人数")
  private Integer arrearsPatientCount;
  /** 欠费账单数 */
  @ApiModelProperty("欠费账单数")
  private Integer arrearsBillCount;
  /** 欠费总额 */
  @ApiModelProperty("欠费总额")
  private BigDecimal totalArrears;
}
