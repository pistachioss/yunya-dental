package com.yunya.feign.emr.domain.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author 杨柳絮
 * @className MedicalOrthodonticsRecordQueryForm
 * @description
 * @date 2020/7/28 13:40
 */
@Data
public class MedicalOrthodonticsRecordQueryForm {
  /**
   * 患者ID
   */
  @ApiModelProperty("患者ID")
  @NotNull(message = "患者ID不能为空")
  private Integer patientId;

}
