package com.yunya.feign.treatment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 简介: 就诊中患者列表查询参数模型
 *
 * @author: chow
 * @date: 2020/8/12 16:00
 * @description:
 * @since: 1.0.0
 */
@ApiModel("就诊中患者列表查询参数模型")
@Data
@ToString
public class TreatmentRecordQueryForm implements Serializable {
  @ApiModelProperty(value = "是否分页,默认true")
  private Boolean whetherPage = true;

  @ApiModelProperty("页码，默认第1页")
  @Min(message = "最小值", value = 1)
  private Integer pageNum = 1;

  @ApiModelProperty("每页显示数量，默认显示10条")
  @Min(message = "最小值", value = 1)
  private Integer pageSize = 10;

  @ApiModelProperty(value = "组织（门诊）ID", required = true)
  @NotNull(message = "组织ID不能为空！")
  private Integer orgId;

  @ApiModelProperty(value = "查询日期(yyyy-MM-dd)", required = true)
  @NotBlank(message = "查询日期不能为空！")
  private String currentDate;

  @ApiModelProperty(value = "诊疗状态(0-就诊中;1-已开单;2-治疗完成;3-已结账)", required = true)
  @NotNull(message = "就诊状态不能为空！")
  private Byte treatmentStatus;

  @ApiModelProperty("接诊ID")
  private Integer id;

  @ApiModelProperty("挂号医生ID")
  private Integer dentistId;
}
