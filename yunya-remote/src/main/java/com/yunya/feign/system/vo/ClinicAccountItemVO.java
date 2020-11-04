package com.yunya.feign.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 门诊入账方式VO
 *
 * @author: chow
 * @date: 2020/7/27 11:24
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊入账方式VO")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class ClinicAccountItemVO extends AccountItemVO implements Serializable {
  /** 公司端对应的诊所ID */
  @ApiModelProperty("组织ID")
  private Integer orgId;
  /** 组织名称 */
  @ApiModelProperty("组织名称")
  private String orgName;
}
