package com.yunya.feign.treatment_other.domain.vo;

import com.yunya.feign.treatment.domain.vo.QcTreatmentVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

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
    
    /** 可使用优惠的订单明细id列表 */
    @ApiModelProperty("可使用优惠的订单明细id列表")
    private List<Integer> orderDetailIds;
}
