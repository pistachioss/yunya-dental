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
 * 简介: 开单记录查询参数模型
 *
 * @author: chow
 * @date: 2020/10/27 10:18
 * @description:
 * @since: 1.0.0
 */
@ApiModel("开单记录查询参数模型（数据记录-账单记录-开单记录）")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class OrderRecordQuery extends PageQuery implements Serializable {
  /** 门诊ID */
  @ApiModelProperty(value = "门诊ID", required = true)
  @NotNull(message = "请选择需要查询的诊所！")
  private Integer orgId;
  /** 患者关键字（患者姓名/姓名拼音/手机号） */
  @ApiModelProperty("患者关键字（患者姓名/姓名拼音/手机号）")
  private String keyWord;
  /** 订单编号 */
  @ApiModelProperty("订单编号")
  private String orderNum;
  /** 账单编号 */
  @ApiModelProperty("账单编号")
  private String billNum;
  /** 查询就诊开始时间 */
  @ApiModelProperty(value = "开单开始时间", example = "2020-01-01", required = true)
  @NotBlank(message = "开始时间不能为空！")
  private String orderStartDate;
  /** 查询就诊结束时间 */
  @ApiModelProperty(value = "开单结束时间", example = "2021-01-01", required = true)
  @NotBlank(message = "结束时间不能为空！")
  private String orderEndDate;
  /** 挂号医生ID列表 */
  @ApiModelProperty("挂号医生ID列表")
  private Integer[] regDentistIds;
}
