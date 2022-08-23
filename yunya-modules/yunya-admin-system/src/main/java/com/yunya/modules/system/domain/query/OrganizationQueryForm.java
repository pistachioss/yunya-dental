package com.yunya.modules.system.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

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
@ApiModel(value = "组织列表查询参数模型")
public class OrganizationQueryForm implements Serializable {
  @ApiModelProperty(value = "是否分页", required = true)
  private Boolean whetherPage = true;

  @ApiModelProperty("页码")
  @Min(message = "最小值", value = 1)
  private Integer pageNum = 1;

  @ApiModelProperty("每页显示数量")
  @Min(message = "最小值", value = 1)
  private Integer pageSize = 10;
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
  /** 经度*/
  @ApiModelProperty("经度")
  private String longitude;
  /** 纬度*/
  @ApiModelProperty("纬度")
  private String latitude;
}
