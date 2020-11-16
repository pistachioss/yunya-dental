package com.yunya.feign.discount.domain.bo;

import lombok.Data;

/**
 * @author xiangyang
 * @date 2020/11/11
 */
@Data
public class CardUseBo {
	private Integer cardId;
	private Integer couponId;
	private Integer useWay;
	private Long total;
	private Long useCount;
	//是否可用 0-否 1-是
	private Integer usable;
}
