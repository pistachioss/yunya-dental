package com.yunya.feign.system.form;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 支付方式配置查询参数模型
 *
 * @author: chow
 * @date: 2020/9/29 16:43
 * @description:
 * @since: 1.0.0
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel("支付方式配置查询参数模型")
@Data
@ToString
public class ClinicAccountItemConfigureQueryForm extends PageQuery implements Serializable {
  /** 入账方式ID */
  @ApiModelProperty(value = "入账方式ID", required = true)
  @NotNull(message = "入账方式ID不能为空！")
  private Integer accountItemId;
}
