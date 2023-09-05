package com.yunya.feign.treatment_other.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author: chenlin
 * @date: 2023/8/31 15:09
 * @description: 全程医疗mall平台-医嘱记录数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗mall平台-医嘱记录数据模型")
public class QcDoctorAdviceRecordVO extends QcResult {

    /** 患者信息 */
    @ApiModelProperty("患者信息")
    private QcPatientInfoVO pat_info;
}
