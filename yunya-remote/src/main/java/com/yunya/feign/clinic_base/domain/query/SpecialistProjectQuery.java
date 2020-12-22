package com.yunya.feign.clinic_base.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 专科项目列表查询模型
 *
 * @author: chow
 * @date: 2020/12/22 14:38
 * @description:
 * @since: 1.0.0
 */
@ApiModel("专科项目列表查询模型")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class SpecialistProjectQuery extends PageQuery implements Serializable {
  /** 专科项目名称 */
  @ApiModelProperty(value = "专科项目名称", required = true)
  private String specialistProjectName;
}
