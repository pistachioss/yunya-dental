package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author: chenlin
 * @date: 2023/9/19 11:00
 * @description: 全程医疗登记单数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗登记单数据模型")
public class QcRecommondOrderVO implements Serializable {
    
    /** 全程医疗就诊记录列表 */
    @ApiModelProperty("全程医疗就诊记录列表")
    private List<QcTreatmentVO> qcTreatments;
    
    /** 可使用优惠的订单明细Map【id,数量】 */
    @ApiModelProperty("可使用优惠的订单Map【id,数量】")
    private Map<Integer, Integer> orderDetailMap;
}
