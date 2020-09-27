package com.yunya.modules.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍: 用户可登陆组织信息返回
 *
 * @author: chow
 * @date: 2020/7/1 09:55
 * @description:
 * @since: 1.0.0
 */
@ApiModel("用户可登陆组织信息返回")
@Data
@ToString
public class SysUserLoginOrgVO implements Serializable {
  /** 组织ID */
  @ApiModelProperty("组织ID")
  private Integer orgId;
  /** 组织名称 */
  @ApiModelProperty("组织名称")
  private String name;
  /** 组织简称 */
  @ApiModelProperty("组织简称")
  private String abbreviation;
  /** 组织类型 */
  @ApiModelProperty("组织属性0:公司,1:区域管理,2:医疗机构,3:其他")
  private Byte type;
}
