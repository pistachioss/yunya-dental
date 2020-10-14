package com.yunya.feign.middletable.domain.form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author xiangyang
 * @date 2020/10/14
 */
@Getter
@Setter
@ToString
public class PullForm {
	private String startDate;
	private String endDate;
}
