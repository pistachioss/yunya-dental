package com.yunya.feign.treatment_other.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 治疗计划模型
 *
 */
@ApiModel("治疗计划模型")
@Data
@ToString
public class TreatPlanVO extends XUploadFileVO implements Serializable {
    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("患者ID")
    private Integer patientId;
}