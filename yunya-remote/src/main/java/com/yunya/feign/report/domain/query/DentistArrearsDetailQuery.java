package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 医生欠费明细列表查询参数
 *
 * @author: chow
 * @date: 2020/12/10 13:37
 * @description:
 * @since: 1.0.0
 */
@ApiModel("医生欠费明细列表查询参数")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class DentistArrearsDetailQuery extends PageQuery implements Serializable {
  /** 医生ID */
  @ApiModelProperty(value = "医生ID", required = true)
  @NotNull(message = "医生ID不能为空！")
  private Integer dentistId;
  /** 账单开始时间 */
  @ApiModelProperty(value = "账单开始日期", example = "yyyy-MM-dd")
  private String billStartDate;
  /** 账单结束日期 */
  @ApiModelProperty(value = "账单结束日期", example = "yyyy-MM-dd")
  private String billEndDate;
  /** 患者关键字 */
  @ApiModelProperty("患者关键字：姓名；姓名拼音；手机号")
  private String keyword;
}
