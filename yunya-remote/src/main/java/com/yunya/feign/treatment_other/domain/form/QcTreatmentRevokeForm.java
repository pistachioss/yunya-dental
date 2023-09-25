package com.yunya.feign.treatment_other.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author: chenlin
 * @date: 2023/9/19 9:11
 * @description: 全程医疗就诊记录撤销模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗就诊记录撤销模型")
public class QcTreatmentRevokeForm implements Serializable {

    /** 订单记录id */
    @ApiModelProperty("订单记录id")
    private Integer orderRecordId;

    /** 全程医疗就诊记录id列表 */
    @ApiModelProperty("全程医疗就诊记录id列表")
    private List<Integer> qcTreatmentIds;
}
