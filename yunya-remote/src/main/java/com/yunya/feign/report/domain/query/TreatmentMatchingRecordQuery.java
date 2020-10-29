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
 * 简介: 配诊记录查询参数封装
 *
 * @author: chow
 * @date: 2020/10/28 09:29
 * @description:
 * @since: 1.0.0
 */
@ApiModel("配诊记录查询参数封装")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class TreatmentMatchingRecordQuery extends PageQuery implements Serializable {
  /** 门诊ID */
  @ApiModelProperty(value = "门诊ID", required = true)
  @NotNull(message = "请选择需要查询的诊所！")
  private Integer orgId;
  /** 患者关键字（患者姓名/姓名拼音/手机号） */
  @ApiModelProperty("患者关键字（患者姓名/姓名拼音/手机号）")
  private String keyWord;
  /** 查询就诊开始时间 */
  @ApiModelProperty(value = "配诊开始时间", example = "2020-01-01", required = true)
  @NotBlank(message = "开始时间不能为空！")
  private String matchingStartDate;
  /** 查询就诊结束时间 */
  @ApiModelProperty(value = "配诊结束时间", example = "2021-01-01", required = true)
  @NotBlank(message = "结束时间不能为空！")
  private String matchingEndDate;
  /** 挂号医生ID列表 */
  @ApiModelProperty("配诊医生ID列表")
  private Integer[] matchingDentistIds;
}
