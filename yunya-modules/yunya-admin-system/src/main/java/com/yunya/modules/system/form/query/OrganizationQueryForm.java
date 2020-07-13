package com.yunya.modules.system.form.query;

import com.yunya.framework.common.model.PageQueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 简单介绍:</br> 组织信息参数Form
 *
 * @author: chow
 * @date: 2020/6/4 16:49
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "组织列表查询参数模型", parent = PageQueryParams.class)
public class OrganizationQueryForm extends PageQueryParams {
  /** 组织ID */
  @ApiModelProperty("组织主键ID")
  private Integer id;
  /** 组织名称 */
  @ApiModelProperty("组织全名")
  private String name;
  /** 组织类型 */
  @ApiModelProperty("组织类型，传数组：example[0,1]，可传多个")
  private Byte[] types;
  /** 组织编号 */
  @ApiModelProperty("医疗机构编号")
  private String clinicNumber;
  /** 组织简称 */
  @ApiModelProperty("医疗机构简称")
  private String abbreviation;
}
