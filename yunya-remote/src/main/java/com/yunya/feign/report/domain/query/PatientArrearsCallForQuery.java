package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 患者欠费催缴列表查询
 *
 * @author: chow
 * @date: 2020/12/10 09:31
 * @description:
 * @since: 1.0.0
 */
@ApiModel("患者欠费催缴列表查询")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class PatientArrearsCallForQuery extends PageQuery implements Serializable {
  /** 患者关键字 */
  @ApiModelProperty("患者关键字：患者姓名/姓名拼音/手机号")
  private String keyword;
}
