package com.yunya.feign.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 组织（公司）全部信息
 *
 * @author: chow
 * @date: 2020/7/15 13:28
 * @description:
 * @since: 1.0.0
 */
@ApiModel("简介: 组织（公司）全部信息")
@Data
@ToString
public class OrganizationInfo implements Serializable {
  @ApiModelProperty("组织ID")
  private Integer id;
  /** 公司名称 */
  @ApiModelProperty("组织全名")
  private String name;
  /** 公司属性0:公司,1:区域管理,2:医疗机构,3:其他 */
  @ApiModelProperty("公司属性0:公司,1:区域管理,2:医疗机构,3:其他")
  private String type;
  /** 组织统一社会代码 */
  @ApiModelProperty("组织统一社会代码")
  private String creditCode;
  /** 门诊编号 */
  @ApiModelProperty("门诊编号")
  private String clinicNumber;
  /** 诊所简称 */
  @ApiModelProperty("诊所简称")
  private String abbreviation;
  /** 诊所地址 */
  @ApiModelProperty("诊所地址")
  private String clinicAddress;
  /** 诊所电话 */
  @ApiModelProperty("诊所电话")
  private String clinicMobile;
}
