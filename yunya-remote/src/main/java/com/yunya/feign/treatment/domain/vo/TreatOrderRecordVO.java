package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author: chenlin
 * @date: 2023/7/21 15:08
 * @description: 就诊订单记录对象
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("就诊订单记录对象")
public class TreatOrderRecordVO implements Serializable {
    
    /** 应收金额合计 */
    private BigDecimal actualTotalAmount;
    
    /** 划扣项目订单明细 */
    private List<OrderDetailChargeVO> swipeItemList; 
    
    /** 非划扣项目订单明细 */
    private List<OrderDetailChargeVO> itemList;
}
