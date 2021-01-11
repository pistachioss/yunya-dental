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
 * 简介: 门诊产品售出记录明细查询参数模型
 *
 * @author: chow
 * @date: 2021/1/11 14:47
 * @description:
 * @since: 1.0.0
 */
@ApiModel("门诊产品售出记录明细查询参数模型")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class StatementProductSoldDetailQuery extends PageQuery implements Serializable {
  /** 组织ID */
  @ApiModelProperty(value = "组织ID", required = true)
  @NotNull(message = "组织ID")
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
  @NotBlank(message = "查询结束时间不能为空！")
  private String endDate;
  /** 售出对象 */
  @ApiModelProperty("售出对象")
  private String soldTargetName;
  /** 售出对象手机号 */
  @ApiModelProperty("售出对象手机号")
  private String soldTargetMobile;
  /** 卡号 */
  @ApiModelProperty("卡号")
  private String cardNum;
  /** 产品名称 */
  @ApiModelProperty("产品名称")
  private String productName;
  /** 售出开始时间 */
  @ApiModelProperty("售出开始时间")
  private String soldStartDate;
  /** 售出结束时间 */
  @ApiModelProperty("售出结束时间")
  private String soldEndDate;
}
