package com.yunya.feign.treatment_other.domain.vo;

import com.yunya.feign.treatment_other.domain.common.QcAdviceItemInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author: chenlin
 * @date: 2023/9/5 16:14
 * @description: 全程医疗-医嘱项信息出参模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗-医嘱项信息出参模型")
public class QcAdviceItemVO extends QcAdviceItemInfo {

    /** Mall平台唯一流水号 */
    @ApiModelProperty("Mall平台唯一流水号")
    private String Mall_order_no;

    /** 患者所在科室: 2-全程医疗全科门诊，3-艾维口腔门诊 */
    @ApiModelProperty("患者所在科室: 2-全程医疗全科门诊，3-艾维口腔门诊")
    private String OEORI_OrdDept_DR;
}
