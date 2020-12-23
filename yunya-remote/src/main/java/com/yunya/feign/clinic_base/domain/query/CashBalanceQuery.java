package com.yunya.feign.clinic_base.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 现金结存列表分页查询模型
 *
 * @author chow
 */
@ApiModel(value = "现金结存列表分页查询模型")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class CashBalanceQuery extends PageQuery implements Serializable {
  /** 组织ID */
  @ApiModelProperty(value = "门诊id", required = true)
  @NotNull(message = "门诊id")
  private Integer orgId;
  /** 查询开始日期 */
  @ApiModelProperty(value = "查询开始日期", required = true, example = "yyyy-MM-dd")
  @NotBlank(message = "开始时间不能为空！")
  private String startDate;
  /** 查询结束日期 */
  @ApiModelProperty(value = "查询结束日期", required = true, example = "yyyy-MM-dd")
  @NotBlank(message = "结束时间不能为空！")
  private String endDate;
}
