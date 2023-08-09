package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author: chenlin
 * @date: 2023/7/21 15:08
 * @description: 就诊订单明细记录对象
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("就诊订单明细记录对象")
public class TreatOrderRecordVO implements Serializable {
    /** 账单总优惠 */
    @ApiModelProperty("账单总优惠")
    private BigDecimal benefitTotalAmount;

    /** 划扣项目订单明细 */
    @ApiModelProperty("划扣项目订单明细")
    private List<OrderDetailChargeVO> swipeItemList; 
    
    /** 非划扣项目订单明细 */
    @ApiModelProperty("非划扣项目订单明细")
    private List<OrderDetailChargeVO> itemList;
}
