package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 末次就诊信息
 * @author: LHB
 * @create: 2020-10-28 16:19
 */
@ApiModel(value = "LastTreatmentInfoVO", description = "末次就诊信息")
@Data
public class LastTreatmentInfoVO implements Serializable {
  /** 末诊门诊ID */
  @ApiModelProperty("末诊门诊ID")
  private Integer orgId;
  /** 末诊门诊名称 */
  @ApiModelProperty("末诊门诊名称")
  private String orgName;
  /** 末诊医生ID */
  @ApiModelProperty(name = "dentistId", value = "末次就诊医生ID")
  private Integer dentistId;
  /** 末诊医生姓名 */
  @ApiModelProperty("末诊医生姓名")
  private String dentistName;
  /** 末诊日期 */
  @ApiModelProperty(name = "dentistId", value = "末次就诊日期")
  private String treatmentDate;
}
