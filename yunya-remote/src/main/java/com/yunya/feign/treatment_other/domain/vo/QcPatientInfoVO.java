package com.yunya.feign.treatment_other.domain.vo;

import com.yunya.feign.treatment_other.domain.common.QcPatientInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author: chenlin
 * @date: 2023/8/31 15:22
 * @description: 全程医疗-患者信息出参模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗-患者信息出参模型")
public class QcPatientInfoVO extends QcPatientInfo {

    /** 就诊信息 */
    @ApiModelProperty("就诊信息")
    private List<QcTreatmentInfoVO> adm_info;
}
