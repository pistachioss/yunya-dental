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
 * 简介: 患者储值卡（会员卡、预付款）退费记录明细查询参数模型
 *
 * @author: chow
 * @date: 2021/1/12 19:21
 * @description:
 * @since: 1.0.0
 */
@ApiModel("患者储值卡（会员卡、预付款）退费记录明细查询参数模型")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class StatementPatientCardRefundDetailQuery extends PageQuery implements Serializable {
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
  /** 卡类型:0-会员卡；1-预付款 */
  @ApiModelProperty(value = "卡类型:0-会员卡；1-预付款", required = true, example = "0-会员卡；1-预付款")
  @NotNull(message = "储值卡类型不能为空！")
  private Byte cardType;
  /** 患者关键字 */
  @ApiModelProperty("患者关键字")
  private String patientKeyword;
  /** 会员卡号、预付款卡号 */
  @ApiModelProperty("会员卡号、预付款卡号")
  private String cardNum;
  /** 开始退费日期 */
  @ApiModelProperty(value = "开始退费日期", example = "yyyy-MM-dd")
  private String refundStartDate;
  /** 结束退费日期 */
  @ApiModelProperty(value = "结束退费日期", example = "yyyy-MM-dd")
  private String refundEndDate;
}
