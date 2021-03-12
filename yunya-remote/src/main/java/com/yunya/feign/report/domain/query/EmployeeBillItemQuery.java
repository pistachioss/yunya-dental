package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;

/**
 * 简介: 开单项目查询参数
 *
 * @author: chow
 * @date: 2020/11/28 16:44
 * @description:
 * @since: 1.0.0
 */
@ApiModel("开单项目查询参数")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class EmployeeBillItemQuery extends PageQuery implements Serializable {
  /** 组织ID */
  @ApiModelProperty(value = "组织ID", required = true)
  @NotNull(message = "组织ID不能为空！")
  private Integer orgId;
  /** 日期类型 */
  @ApiModelProperty(value = "时间类型：0-年月；1-年", required = true)
  @NotNull(message = "日期类型不能为空！")
  private Byte dateType;
  /** 查询时间 */
  @ApiModelProperty("查询时间")
  @NotBlank(message = "查询时间不能为空！")
  private String queryDate;
  /** 执行人id列表 */
  @ApiModelProperty(value = "执行人id列表", required = true)
  private Collection<Integer> executorIds;
  /** 患者关键字 */
  @ApiModelProperty("患者关键字：患者姓名、姓名拼音、手机号")
  private String keyword;
  /** 账单编号 */
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 开单日期 */
  @ApiModelProperty("开单日期")
  private String orderDate;
  /** 账单日期 */
  @ApiModelProperty("账单日期")
  private String billDate;
}
