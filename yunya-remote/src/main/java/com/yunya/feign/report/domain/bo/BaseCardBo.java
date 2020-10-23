package com.yunya.feign.report.domain.bo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author xiangyang
 * @date 2020/10/21
 */
@Getter
@Setter
public class BaseCardBo {
	private Integer couponId;
	private LocalDateTime activationDeadline;
	private Integer effectiveDays;

	public BaseCardBo(Integer couponId, LocalDateTime activationDeadline, Integer effectiveDays) {
		this.couponId = couponId;
		this.activationDeadline = activationDeadline;
		this.effectiveDays = effectiveDays;
	}
}
