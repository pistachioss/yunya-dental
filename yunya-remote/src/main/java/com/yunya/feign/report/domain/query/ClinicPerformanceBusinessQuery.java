
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
import java.util.List;

/**
 * 简介: 门诊业绩&业务报表查询参数
 *
 * @author: chenlin
 * @date: 2021/04/06 14:12
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊业绩&业务报表查询参数")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class ClinicPerformanceBusinessQuery extends PageQuery implements Serializable {
  /** 门诊ID列表 */
  @ApiModelProperty(value = "门诊ID列表")
  private List<Integer> orgIds;
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
  /** 患者来源类型ID列表 */
  @ApiModelProperty(value = "患者来源类型ID列表")
  private Collection<Integer> originTypes;
  /** 专科项目ID列表 */
  @ApiModelProperty(value = "专科项目ID列表")
  private Collection<Integer> specailistProjectIds;
  /** 项目ID列表 */
  private Collection<Integer> itemIds;
  /** 账单ID列表 */
  private Collection<Integer> billIds;
  /** 产品ID列表 */
  private Collection<Integer> couponIds;
  /** 项目类型  0:基础价目表,1:基础商品表 */
  private Integer itemType = 0;
  private Integer parentId = 0;
}