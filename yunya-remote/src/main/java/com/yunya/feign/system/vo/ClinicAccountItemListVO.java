package com.yunya.feign.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简介: 门诊支付方式列表
 *
 * @author: chow
 * @date: 2020/9/29 17:29
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊支付方式列表")
@Data
@ToString
public class ClinicAccountItemListVO implements Serializable {
  /** 公司端对应的诊所ID */
  @ApiModelProperty("组织ID")
  private Integer orgId;
  /** 组织名称 */
  @ApiModelProperty("组织名称")
  private String orgName;
  /** 就诊方式信息列表 */
  private List<AccountItemVO> clinicAccountItems;
}
