package com.yunya.modules.system.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简单介绍: 岗位资源权限查询参数封装
 *
 * @author: chow
 * @date: 2020/7/1 13:24
 * @description:
 * @since: 1.0.0
 */
@ApiModel("岗位资源权限查询参数封装模型")
@Data
@ToString
public class ResourceAuthorityForm implements Serializable {
  /** 岗位ID */
  @ApiModelProperty("岗位ID")
  private Integer postId;
  /** 权限类型 */
  @ApiModelProperty("资源类型0-web资源；1-app资源")
  private Byte resourceType;
  /** 岗位组ID */
  @ApiModelProperty("岗位组ID")
  private Integer postGroupId;
}
