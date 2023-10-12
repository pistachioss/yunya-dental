package com.yunya.feign.treatment_other.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author: chenlin
 * @date: 2023/9/20 10:18
 * @description: 全程医疗-登记单详情数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("全程医疗-登记单详情数据模型")
public class QcRecommondDetailVO implements Serializable {

    /** 客户基本信息 */
    @ApiModelProperty("客户基本信息")
    private QcCustomerTreatmentVO qcCustomerInfoVO;

    /** mall平台医嘱明细 */
    @ApiModelProperty("mall平台医嘱明细")
    private List<OrderAdviceItemVO> qcAdviceItems;
    
    /** 到店开单明细 */
    @ApiModelProperty("到店开单明细")
    private List<OrderAdviceItemVO> orderDetails;
}
