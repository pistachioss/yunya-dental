package com.yunya.feign.clinic_base.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 业务目标查询参数模型
 *
 * @author: chow
 * @date: 2020/12/23 10:14
 * @description:
 * @since: 1.0.0
 */
@ApiModel("业务目标查询参数模型")
@Data
@ToString
public class BusinessTargetQuery implements Serializable {
  /** 业务类型 */
  @ApiModelProperty(value = "业务类型", required = true, example = "0-实收金额；1-工作量；2-初诊人数；3-就诊人次")
  @NotNull(message = "业务类型不能为空！")
  private Byte businessType;
  /** 年份 */
  @ApiModelProperty(value = "添加年份", required = true, example = "2020")
  @NotBlank(message = "添加年份不能为空！")
  private String businessYear;
  /** 数据所属类型 */
  @ApiModelProperty(value = "数据所属类型", required = true, example = "0-组织；1-个人")
  @NotNull(message = "数据所属类型不能为空！0-组织；1-个人")
  private Byte belongType;
  /** 数据所属ID（组织ID或个人ID） */
  @ApiModelProperty(value = "数据所属ID（组织ID或个人ID）", required = true)
  @NotNull(message = "数据所属ID不能为空！")
  private Integer belongId;
}
