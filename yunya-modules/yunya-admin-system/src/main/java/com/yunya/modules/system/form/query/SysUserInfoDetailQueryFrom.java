package com.yunya.modules.system.form.query;

import com.yunya.framework.common.model.PageQueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简单介绍:</br> 用户详细信息列表查询参数封装模型
 *
 * @author: chow
 * @date: 2020/6/13 13:19
 * @description:
 * @since: 1.0.0
 */
@ApiModel("用户详情信息查询模型")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class SysUserInfoDetailQueryFrom extends PageQueryParams implements Serializable {
  /** 用户ID */
  @ApiModelProperty("用户ID")
  private Integer userId;
  /** 组织ID */
  @ApiModelProperty("组织ID（不传查询全部员工）")
  private List<Integer> orgIds;
  /** 岗位组ID */
  @ApiModelProperty("岗位组ID列表")
  private List<Integer> postGroupId;
  /** 岗位组ID列表 */
  @ApiModelProperty("岗位ID列表")
  private List<Integer> postIds;
  /** 关键字（姓名、手机号） */
  @ApiModelProperty("关键字（员工姓名或手机号）")
  private String keyWord;
  /** 就职状态 */
  @ApiModelProperty("就职状态（试用: 0, 正式: 1，实习: 2, 离职:3）")
  private Byte workStatus;
}
