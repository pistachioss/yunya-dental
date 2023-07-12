package com.yunya.feign.discount.domain.bo;

import lombok.Data;

@Data
public class CouponRemainingBo {
    private Integer cardId;
	private Integer couponId;
	private Integer orgId;
	private Integer remaining;
    private String cardNumber;
}
