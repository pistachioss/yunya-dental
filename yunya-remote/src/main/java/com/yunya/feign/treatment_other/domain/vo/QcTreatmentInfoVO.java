package com.yunya.feign.treatment_other.domain.vo;

import com.yunya.feign.treatment_other.domain.common.QcTreatmentInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author: chenlin
 * @date: 2023/9/5 16:02
 * @description: 全程医疗-就诊信息出参模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗-就诊信息出参模型")
public class QcTreatmentInfoVO extends QcTreatmentInfo {

    /** 医嘱项信息列表 */
    @ApiModelProperty("医嘱项信息列表")
    private List<QcAdviceItemVO> order_infos;
}
