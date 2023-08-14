package com.yunya.feign.treatment.domain.vo;

import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @author: chenlin
 * @date: 2023/8/11 17:26
 * @description: 就诊订单优惠数据模型
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("就诊订单优惠数据模型")
public class TreatOrderBenefitVO implements Serializable {

    /** 总优惠 */
    @ApiModelProperty("总优惠")
    private BigDecimal benefitTotalAmount = BigDecimal.ZERO;
    
    /** 优惠项目明细列表 */
    @ApiModelProperty("优惠项目明细列表")
    private Map<Integer, OrderDetailPayBenefitVO> discountMap = Maps.newHashMap();
}
