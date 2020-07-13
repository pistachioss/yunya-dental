package com.yunya.modules.system.form.query;

import com.yunya.framework.common.model.PageQueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
@EqualsAndHashCode(callSuper = true)
@ApiModel("地区列表查询参数封装模型")
public class SysDistrictsQueryForm extends PageQueryParams {
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
