package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 医生所属账单欠费信息查询参数
 *
 * @author: chow
 * @date: 2020/12/10 13:01
 * @description:
 * @since: 1.0.0
 */
@ApiModel("医生所属账单欠费信息查询参数")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class DentistArrearsCallForQuery extends PageQuery implements Serializable {
  /** 医生ID列表 */
  @ApiModelProperty("医生ID列表")
  private Integer[] dentistIds;
}
