package com.yunya.feign.treatment.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 账单退费查询参数
 *
 * @author: chow
 * @date: 2020/11/3 14:28
 * @description:
 * @since: 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel("账单退费查询参数")
@Data
@ToString
public class BillRefundQuery extends PageQuery implements Serializable {
  /** 患者ID */
  @ApiModelProperty(value = "患者ID", required = true)
  @NotNull(message = "患者ID不能为空！")
  private Integer patientId;
  /** 账单编号 */
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 门诊ID */
  @ApiModelProperty("门诊ID")
  private Integer orgId;
}
