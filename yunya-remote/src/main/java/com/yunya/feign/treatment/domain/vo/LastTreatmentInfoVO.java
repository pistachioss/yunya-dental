package com.yunya.feign.treatment.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 末次就诊信息
 * @author: LHB
 * @create: 2020-10-28 16:19
 **/
@ApiModel(value = "LastTreatmentInfoVO",description = "末次就诊信息")
@Data
public class LastTreatmentInfoVO implements Serializable {
    @ApiModelProperty(name = "patientId", value = "末次就诊患者ID")
    private Integer patientId;
    @ApiModelProperty(name = "dentistId", value = "末次就诊医生ID")
    private Integer dentistId;
    @ApiModelProperty(name = "dentistId", value = "末次就诊日期")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date treatmentDate;
    @ApiModelProperty(name = "dentistId", value = "末次就诊科室ID")
    private Integer deptRoomId;
}
