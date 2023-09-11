package com.yunya.feign.treatment_other.domain.form;

import com.yunya.feign.treatment_other.domain.common.QcTreatmentInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author: chenlin
 * @date: 2023/9/11 10:58
 * @description: 全程医疗-医嘱上传-就诊信息模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗-医嘱上传-就诊信息模型")
public class QcAdviceUploadTreatmentForm extends QcTreatmentInfo {
    /** 平台就诊流水号，引导单模式下必填 */
    @ApiModelProperty("平台就诊流水号，引导单模式下必填")
    private String adm_no;
}
