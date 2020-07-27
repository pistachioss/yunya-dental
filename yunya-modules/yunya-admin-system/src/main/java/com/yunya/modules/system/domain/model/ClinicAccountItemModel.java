package com.yunya.modules.system.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 门诊入账方式新增参数模型
 *
 * @author: chow
 * @date: 2020/7/27 13:04
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊入账方式新增参数模型")
@Data
@ToString
public class ClinicAccountItemModel implements Serializable {

  /** 公司端对应的诊所ID */
  @ApiModelProperty(value = "组织（门诊）ID", required = true)
  @NotNull(message = "组织ID不能为空！")
  private Integer companyId;
  /** 公司端对应支付方式分类表的ID */
  @ApiModelProperty(value = "入账方式ID", required = true)
  @NotNull(message = "入账方式ID不能为空！")
  private Integer accountItemId;
}
