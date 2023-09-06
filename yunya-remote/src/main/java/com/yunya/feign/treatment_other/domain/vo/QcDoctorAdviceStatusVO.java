package com.yunya.feign.treatment_other.domain.vo;

import com.yunya.feign.treatment_other.domain.form.QcAdviceItemNotice;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author: chenlin
 * @date: 2023/9/6 9:23
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗-医嘱状态数据模型")
public class QcDoctorAdviceStatusVO extends QcResult {

    /** 平台患者登记号 */
    @ApiModelProperty("平台患者登记号")
    private String patient_no;

    /** 平台就诊流水号 */
    @ApiModelProperty("平台就诊流水号")
    private String adm_no;

    /** 医嘱项状态 */
    @ApiModelProperty("医嘱项状态")
    private List<QcAdviceItemNotice> order_infos;
}
