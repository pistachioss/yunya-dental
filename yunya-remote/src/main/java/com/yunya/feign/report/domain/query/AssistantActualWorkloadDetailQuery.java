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

/**
 * 简介: 助手实收工作量明细列表查询参数
 *
 * @author: chow
 * @date: 2020/12/3 19:48
 * @description:
 * @since: 1.0.0
 */
@ApiModel("助手实收工作量明细列表查询参数")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class AssistantActualWorkloadDetailQuery extends PageQuery implements Serializable {
  /** 门诊ID */
  @ApiModelProperty(value = "门诊ID", required = true)
  @NotNull(message = "门诊ID不能为空！")
  private Integer orgId;
  /** 时间类型 */
  @ApiModelProperty(value = "时间类型：0-日；1-月；2-年", required = true)
  @NotNull(message = "时间类型不能为空！")
  private Byte dateType;
  /** 开始时间 */
  @ApiModelProperty(value = "开始时间", required = true)
  @NotBlank(message = "查询开始时间不能为空！")
  private String startDate;
  /** 结束时间 */
  @ApiModelProperty(value = "结束时间", required = true)
  private String endDate;
  /** 助手类型 */
  @ApiModelProperty(value = "助手类型：0-助手1；1-助手2；2-巡回", required = true)
  @NotNull(message = "助手类型不能为空！")
  private Byte assistantType;
  /** 助手ID */
  @ApiModelProperty(value = "助手ID", required = true)
  @NotNull(message = "助手ID不能为空！")
  private Integer assistantId;
  /** 患者关键字 */
  @ApiModelProperty("患者关键字:患者姓名、拼音姓名、手机号")
  private String keyword;
  /** 账单编号 */
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 开单开始日期 */
  @ApiModelProperty(value = "开单开始日期", example = "yyyy-MM-dd")
  private String orderStartDate;
  /** 开单结束日期 */
  @ApiModelProperty(value = "开单结束日期", example = "yyyy-MM-dd")
  private String orderEndDate;
}
