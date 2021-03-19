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
 * 简介: 开单数量及金额统计明细查询参数
 *
 * @author: chow
 * @date: 2020/12/7 14:12
 * @description:
 * @since: 1.0.0
 */
@ApiModel("开单数量及金额统计明细查询参数")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class BillItemDetailQuery extends PageQuery implements Serializable {
  /** 门诊ID */
  @ApiModelProperty(value = "门诊ID")
  private Integer orgId;
  /** 时间类型 */
  @ApiModelProperty(value = "时间类型:0-日；1-月；2-年", required = true)
  @NotNull(message = "时间类型不能为空！")
  private Byte dateType;
  /** 查询时间 */
  @ApiModelProperty(value = "查询开始时间", required = true)
  @NotBlank(message = "查询开始时间不能为空！")
  private String startDate;
  /** 查询结束时间 */
  @ApiModelProperty(value = "查询结束时间", required = true)
  @NotBlank(message = "查询结束时间不能为空！")
  private String endDate;
  /** 项目分类ID */
  @ApiModelProperty(value = "项目ID", required = true)
  @NotNull(message = "项目ID不能为空！")
  private Integer itemId;
  /** 项目类型 */
  @ApiModelProperty(value = "项目类型：0-价目表；1-商品", required = true)
  @NotNull(message = "项目类型不能为空！")
  private Byte itemType;
  /** 患者关键字（姓名/拼音姓名/手机号） */
  @ApiModelProperty("患者关键字（姓名/拼音姓名/手机号）")
  private String keyword;
  /** 账单编号 */
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 账单开始日期 */
  @ApiModelProperty("账单开始日期")
  private String billStartDate;
  /** 账单结束日期 */
  @ApiModelProperty("账单结束日期")
  private String billEndDate;
  /** 执行人ID列表 */
  @ApiModelProperty("执行人ID列表")
  private Collection<Integer> executorIds;
  /** 挂号医生ID */
  @ApiModelProperty(value = "挂号医生ID", required = true)
  private Integer regDentistId;
}
