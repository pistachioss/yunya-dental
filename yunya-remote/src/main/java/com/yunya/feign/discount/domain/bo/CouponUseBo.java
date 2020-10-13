package com.yunya.feign.discount.domain.bo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author xiangyang
 * @date 2020/9/16
 */
@Getter
@Setter
public class CouponUseBo {
    private Integer couponId;
    private String couponName;
    private ItemBenefitUseDetailBo itemUseDetail;

    public static CouponUseBo getInstance() {
        CouponUseBo couponUseBo = new CouponUseBo();
        return couponUseBo;
    }
}
