package com.yunya.feign.treatment_other.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: chenlin
 * @date: 2023/8/31 15:05
 * @description: 全程医疗mall平台医嘱查询模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗mall平台医嘱查询模型")
public class QcDoctorAdviceQuery implements Serializable {

    /** 开始日期 */
    @ApiModelProperty("开始日期")
    private String start_date;

    /** 结束日期 */
    @ApiModelProperty("结束日期")
    private String end_date;

    /** 平台患者登记号 */
    @ApiModelProperty("平台患者登记号")
    private String patient_no;

    /** 平台就诊流水号 */
    @ApiModelProperty("平台就诊流水号")
    private String adm_no;

    /** 患者证件号码 */
    @ApiModelProperty("患者证件号码")
    private String cred_no;
}
