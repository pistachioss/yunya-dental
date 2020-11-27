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
 * 简介: 应收帐款余额查询参数模型
 *
 * @author: chow
 * @date: 2020/11/25 18:08
 * @description:
 * @since: 1.0.0
 */
@ApiModel("应收帐款余额查询参数模型")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class BillOfReceivableQuery extends PageQuery implements Serializable {
  /** 组织ID */
  @ApiModelProperty(value = "组织ID", required = true)
  @NotNull(message = "组织ID不能为空！")
  private Integer orgId;
  /** 开单日期 */
  @ApiModelProperty(value = "开单日期", required = true)
  @NotBlank(message = "开单日期不能为空！")
  private String orderDate;
  /** 患者关键字 */
  @ApiModelProperty("患者关键字：姓名/姓名拼音/手机号")
  private String keyword;
  /** 挂号医生列表 */
  @ApiModelProperty("挂号医生列表")
  private Integer[] regDentistIds;
}
