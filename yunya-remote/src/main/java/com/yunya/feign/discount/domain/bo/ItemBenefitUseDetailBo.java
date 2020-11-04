package com.yunya.feign.discount.domain.bo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author xiangyang
 * @date 2020/9/15
 */
@Getter
@Setter
public class ItemBenefitUseDetailBo {
    private Integer itemId;
    private Integer type;
    /**
     * 当前可用数量
     */
    private Integer count;
    /**
     * 该字段套餐券需要使用（计算优惠金额）
     */
    private BigDecimal packageUnitPrice;

    public ItemBenefitUseDetailBo(Integer itemId, Integer type, Integer count, BigDecimal packageUnitPrice) {
        this.itemId = itemId;
        this.type = type;
        this.count = count;
        this.packageUnitPrice = packageUnitPrice;
    }
}
