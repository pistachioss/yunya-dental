package com.yunya.feign.treatment.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;

/**
 * 简介: 患者就诊记录列表查询参数模型
 *
 * @author: chow
 * @date: 2020/9/11 10:23
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("患者就诊记录列表查询参数模型")
public class PatientTreatmentRecordQueryForm implements Serializable {
  @ApiModelProperty(value = "是否分页,默认true")
  private Boolean whetherPage = true;

  @ApiModelProperty("页码，默认第1页")
  @Min(message = "最小值", value = 1)
  private Integer pageNum = 1;

  @ApiModelProperty("每页显示数量，默认显示10条")
  @Min(message = "最小值", value = 1)
  private Integer pageSize = 10;
  /** 患者ID */
  @NotNull(message = "患者ID不能为空！")
  @ApiModelProperty(value = "患者ID", required = true)
  private Integer patientId;
  /** 就诊开始开始时间 */
  @ApiModelProperty("就诊开始开始时间")
  private String startDate;
  /** 就诊结束时间 */
  @ApiModelProperty("就诊结束时间")
  private String endDate;
  /** 门诊列表 */
  @ApiModelProperty("就诊门诊列表（默认全部门诊）")
  private Integer[] orgIds;
  /** 就诊状态 */
  @ApiModelProperty("就诊状态(0-就诊中；1-已开单；2-就诊完成；3-离店)；默认全部状态")
  private Byte[] treatStatus;
  /** 挂号ID */
  @ApiModelProperty("挂号ID")
  private Integer[] registeredId;
  /** 入账方式id列表 */
  @ApiModelProperty("入账方式id列表")
  private Collection<Integer> payIds;
}
