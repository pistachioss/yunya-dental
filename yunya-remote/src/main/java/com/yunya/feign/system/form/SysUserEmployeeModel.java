package com.yunya.feign.system.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简介: 用户（员工）详细信息查询模型
 *
 * @author: chow
 * @date: 2020/7/18 16:42
 * @description:
 * @since: 1.0.0
 */
@ApiModel("用户（员工）详细信息查询模型")
@Data
@ToString
public class SysUserEmployeeModel implements Serializable {
  /** 是否分页 */
  @ApiModelProperty("是否分页")
  private Boolean whetherPage;
  /** 当前页 */
  @ApiModelProperty("当前页")
  private Integer pageNum;
  /** 每页显示条数 */
  @ApiModelProperty("每页显示条数")
  private Integer pageSize;
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
