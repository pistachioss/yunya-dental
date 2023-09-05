package com.yunya.feign.treatment_other.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author: chenlin
 * @date: 2023/9/5 17:11
 * @description: 全程医疗-医嘱状态变更
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗-医嘱状态变更通知")
public class QcDoctorAdviceStatusNotice implements Serializable {

    /** 平台患者登记号 */
    @ApiModelProperty("平台患者登记号")
    private String patient_no;
    
    /** 平台就诊流水号 */
    @ApiModelProperty("平台就诊流水号")
    private String adm_no;

    /** 医嘱信息列表变更 */
    @ApiModelProperty("医嘱信息列表变更")
    private List<QcAdviceItemNotice> order_infos;
}
