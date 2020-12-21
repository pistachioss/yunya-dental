package com.yunya.feign.discount.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 结存凭证修改参数模型
 *
 * @author: chow
 * @date: 2020/12/21 14:09
 * @description:
 * @since: 1.0.0
 */
@ApiModel("结存凭证修改参数模型")
@Data
@ToString
public class CertificatesForm implements Serializable {
  /** 结存记录ID */
  @ApiModelProperty("id")
  private Integer id;
  /** 结存凭证列表 */
  @ApiModelProperty("结存凭证列表")
  private String[] certificates;
}
