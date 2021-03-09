package com.yunya.feign.treatment_other.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: yunya-dental
 * @description: 后续随访视图模型
 * @author: LHB
 * @create: 2020-12-09 14:41
 */
@Data
@ApiModel(value = "NextVisitingRecordVo", description = "后续随访、提醒信息视图模型")
public class NextVisitingRecordVo implements Serializable {
  /** 患者ID */
  @ApiModelProperty("患者ID")
  private Integer patientId;
  /** 后续随访数量 */
  @ApiModelProperty("后续随访数量")
  private Integer visitRecordCount;
  /** 后续提醒数量 */
  @ApiModelProperty("后续提醒数量")
  private Integer visitRemindCount;
}
