package com.yunya.feign.treatment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 会员卡记录查询参数模型
 * @author: LHB
 * @create: 2020-11-10 19:18
 */
@ApiModel(value = "MemberAccountQuery", description = "会员卡记录查询参数模型")
@Data
public class PaymentRecordQuery implements Serializable {
  @ApiModelProperty(value = "是否分页,默认true")
  private Boolean whetherPage = true;

  @ApiModelProperty("页码，默认第1页")
  @Min(message = "最小值", value = 1)
  private Integer pageNum = 1;

  @ApiModelProperty("每页显示数量，默认显示10条")
  @Min(message = "最小值", value = 1)
  private Integer pageSize = 10;

  @ApiModelProperty(value = "支付记录ID", required = true)
  @NotNull(message = "收费记录记录ID不能为空！")
  private Integer billPayRecordId;

  @ApiModelProperty(value = "入账方式类型（0-预付款；1-会员卡)", required = true)
  private Byte type;
}
