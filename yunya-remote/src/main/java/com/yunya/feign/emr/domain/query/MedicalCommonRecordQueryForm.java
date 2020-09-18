package com.yunya.feign.emr.domain.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 杨柳絮
 * @className MedicalCommonRecordQueryForm
 * @description
 * @date 2020/7/30 14:33
 */
@Data
public class MedicalCommonRecordQueryForm {
  /**
   * 患者ID
   */
  @ApiModelProperty("患者ID")
  @NotNull(message = "患者ID不能为空")
  private Integer patientId;

  @ApiModelProperty("病历ID")
  private Integer id;

  /**
   * 就诊ID
   */
  @ApiModelProperty("就诊ID")
  private Integer treatmentId;
}
