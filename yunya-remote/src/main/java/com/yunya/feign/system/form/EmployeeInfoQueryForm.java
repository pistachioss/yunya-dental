package com.yunya.feign.system.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 简介: 员工信息查询模型
 *
 * @author: chow
 * @date: 2020/9/27 13:55
 * @description:
 * @since: 1.0.0
 */
@ApiModel("员工信息查询模型")
@Data
@ToString
public class EmployeeInfoQueryForm implements Serializable {
  @ApiModelProperty(value = "是否分页,默认分页")
  private Boolean whetherPage = true;
  @ApiModelProperty("页码，默认第1页")
  @Min(message = "最小值", value = 1)
  private Integer pageNum = 1;
  @ApiModelProperty("每页显示数量，默认显示10条")
  @Min(message = "最小值", value = 1)
  private Integer pageSize = 10;
  /** 用户ID列表 */
  @ApiModelProperty("用户ID列表")
  private Integer[] userIds;
  /** 组织ID列表 */
  @ApiModelProperty("组织ID列表")
  private Integer[] orgIds;
  /** 岗位组ID列表 */
  @ApiModelProperty("岗位组ID列表")
  private Integer[] postGroupIds;
  /** 岗位ID列表 */
  @ApiModelProperty("岗位ID列表")
  private Integer[] postIds;
}
