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
 * 简介: 就诊记录查询条件封装
 *
 * @author: chow
 * @date: 2020/10/26 14:44
 * @description:
 * @since: 1.0.0
 */
@ApiModel("就诊记录查询条件封装")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class TreatmentRecordQuery extends PageQuery implements Serializable {
  /** 门诊ID */
  @ApiModelProperty(value = "门诊ID", required = true)
  @NotNull(message = "请选择需要查询的诊所！")
  private Integer orgId;
  /** 患者关键字（患者姓名/姓名拼音/手机号） */
  @ApiModelProperty("患者关键字（患者姓名/姓名拼音/手机号）")
  private String keyWord;
  /** 查询就诊开始时间 */
  @ApiModelProperty(value = "就诊开始时间", example = "yyyy-MM-dd", required = true)
  @NotBlank(message = "开始时间不能为空！")
  private String treatStartDate;
  /** 查询就诊结束时间 */
  @ApiModelProperty(value = "就诊结束时间", example = "yyyy-MM-dd", required = true)
  @NotBlank(message = "结束时间不能为空！")
  private String treatEndDate;
  /** 挂号医生ID列表 */
  @ApiModelProperty("挂号医生ID列表")
  private Integer[] regDentistIds;
}
