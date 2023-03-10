package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 简介: 账单优惠明细查询参数模型
 *
 * @author: chow
 * @date: 2020/11/25 10:47
 * @description:
 * @since: 1.0.0
 */
@ApiModel("账单优惠明细查询参数模型")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class BillOfDiscountDetailQuery extends PageQuery implements Serializable {
  @ApiModelProperty("患者关键字-姓名/拼音姓名/手机号")
  private String keyWord;

  @ApiModelProperty("账单编号")
  private String billNum;

  @ApiModelProperty("门诊ID列表")
  private Integer[] orgIds;

  @ApiModelProperty(value = "开单开始日期", example = "yyyy-MM-dd", required = true)
  @NotBlank(message = "开单开始日期不能为空！")
  private String orderStartDate;

  @ApiModelProperty(value = "开单结束日期", required = true, example = "yyyy-MM-dd")
  @NotBlank(message = "开单结束日期不能为空！")
  private String orderEndDate;

  @ApiModelProperty("挂号医生ID列表")
  private Integer[] regDentistIds;

  @ApiModelProperty("优惠类型列表：1-产品优惠；2-授权折扣；3-混搭优惠（价目使用产品+商品使用折扣）")
  private Byte[] privilegeTypes;
}
