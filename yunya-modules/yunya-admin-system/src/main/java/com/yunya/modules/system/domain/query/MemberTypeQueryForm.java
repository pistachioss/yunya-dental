package com.yunya.modules.system.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 简介: 会员卡类型列表查询参数模型
 *
 * @author: chow
 * @date: 2020/7/24 09:57
 * @description:
 * @since: 1.0.0
 */
@ApiModel("会员卡类型列表查询参数模型")
@Data
@ToString
public class MemberTypeQueryForm implements Serializable {

  @ApiModelProperty(value = "是否分页,默认true")
  private Boolean whetherPage = true;

  @ApiModelProperty("页码，默认第1页")
  @Min(message = "最小值", value = 1)
  private Integer pageNum = 1;

  @ApiModelProperty("每页显示数量，默认显示10条")
  @Min(message = "最小值", value = 1)
  private Integer pageSize = 10;

  @ApiModelProperty("会员卡类型ID")
  private Integer id;
  /** 会员卡名称 */
  @ApiModelProperty("会员卡名称")
  private String name;
  /** 类型,0:普通,1:VIP */
  @ApiModelProperty("类型,0:普通,1:VIP")
  private Byte type;
  /** 是否启用 */
  @ApiModelProperty("是否启用")
  private Boolean inservice;
}
