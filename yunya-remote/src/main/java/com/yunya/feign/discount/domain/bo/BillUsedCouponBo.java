package com.yunya.feign.discount.domain.bo;

import lombok.Data;

/**
 * @author xiangyang
 * @date 2020/11/21
 */
@Data
public class BillUsedCouponBo {
	private Integer couponId;
	private String couponName;
	private Integer benefitType;
}
