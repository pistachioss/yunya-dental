package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 简介: 运营分析数据总览查询参数
 *
 * @author: chow
 * @date: 2020/12/8 13:40
 * @description:
 * @since: 1.0.0
 */
@ApiModel("运营分析数据总览查询参数")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class DataStatisticsQuery extends PageQuery implements Serializable {
  /** 门诊ID列表 */
  @ApiModelProperty(value = "门诊ID列表", required = true)
  @Size(min = 1, message = "门诊ID不能为空！")
  private Integer[] orgIds;
  /** 时间类型 */
  @ApiModelProperty(value = "时间类型:0-日；1-月；2-年", required = true)
  @NotNull(message = "时间类型不能为空！")
  private Byte dateType;
  /** 开始时间 */
  @ApiModelProperty(value = "开始时间", required = true)
  @NotBlank(message = "开始时间不能为空！")
  private String startDate;
  /** 结束时间 */
  @ApiModelProperty(value = "结束时间", required = true)
  @NotBlank(message = "结束时间不能为空！")
  private String endDate;
}
