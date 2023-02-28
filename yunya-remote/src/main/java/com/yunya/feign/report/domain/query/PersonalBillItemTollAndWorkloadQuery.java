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
 * 简介: 个人收费项目工作量明细统计查询参数
 *
 * @author: chow
 * @date: 2021/4/14 10:54
 * @description:
 * @since: 1.0.0
 */
@ApiModel("个人收费项目工作量明细统计查询参数")
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class PersonalBillItemTollAndWorkloadQuery extends PageQuery implements Serializable {
  /** 门诊ID */
  @ApiModelProperty(value = "门诊ID")
  @NotNull(message = "门诊ID不能为空！")
  private Integer orgId;
  /** 查询时间 */
  @ApiModelProperty(value = "查询开始时间", required = true)
  @NotBlank(message = "查询开始时间不能为空！")
  private String startDate;
  /** 查询结束时间 */
  @ApiModelProperty(value = "查询结束时间", required = true)
  @NotBlank(message = "查询结束时间不能为空！")
  private String endDate;
  /** 项目类型：0-价目，1-商品 */
  @ApiModelProperty(value = "项目类型：0-价目，1-商品", required = true)
  @NotNull(message = "项目类型不能为空")
  private Byte itemType;
  /** 项目ID */
  @ApiModelProperty("项目ID")
  @NotNull(message = "项目ID不能为空")
  private Integer itemId;
  /** 员工ID */
  @ApiModelProperty(value = "员工ID")
  @NotNull(message = "员工ID不能为空！")
  private Integer executorId;
  /** 患者关键字 */
  @ApiModelProperty("患者关键字（姓名/姓名拼音/手机号）")
  private String patientKeyword;
  /** 账单编号 */
  @ApiModelProperty("账单编号")
  private String billNum;
}
