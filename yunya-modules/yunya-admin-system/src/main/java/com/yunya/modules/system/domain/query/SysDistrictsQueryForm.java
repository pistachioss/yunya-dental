package com.yunya.modules.system.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 简单介绍:</br> 地区列表查询参数封装
 *
 * @author: chow
 * @date: 2020/6/13 16:57
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("地区列表查询参数封装模型")
public class SysDistrictsQueryForm implements Serializable {
  @ApiModelProperty(value = "是否分页", required = true)
  private Boolean whetherPage = true;

  @ApiModelProperty("页码")
  @Min(message = "最小值", value = 1)
  private Integer pageNum = 1;

  @ApiModelProperty("每页显示数量")
  @Min(message = "最小值", value = 1)
  private Integer pageSize = 10;
  /** 地区ID */
  @ApiModelProperty("地区编号")
  private Integer id;
  /** 上级编号 */
  @ApiModelProperty("上级编号，顶级为0")
  private Integer parentId;
  /** 层级 */
  @ApiModelProperty("地区层级")
  private Byte deep;
  /** 名称 */
  @ApiModelProperty("关键字（可模糊匹配地区名称、扩展名、拼音、简拼）")
  private String keyWord;
}
