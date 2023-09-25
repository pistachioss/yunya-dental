package com.yunya.feign.treatment_other.domain.form;

import com.yunya.feign.treatment_other.domain.common.QcPatientInfo;
import com.yunya.feign.treatment_other.domain.common.QcTreatmentInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author: chenlin
 * @date: 2023/9/11 10:26
 * @description: 全程医疗-医嘱上传入参模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗-医嘱上传入参模型")
public class QcAdviceUploadForm implements Serializable {


    /** 患者信息 */
    @ApiModelProperty("患者信息")
    private QcPatientInfo pat_info;

    /** 就诊信息 */
    @ApiModelProperty("就诊信息")
    private QcTreatmentInfo adm_info;

    /** 医嘱信息列表 */
    @ApiModelProperty("医嘱信息列表")
    private List<QcAdviceUploadItemForm> order_infos;
}
