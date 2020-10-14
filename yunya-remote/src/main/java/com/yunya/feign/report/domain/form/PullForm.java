package com.yunya.feign.report.domain.form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @author xiangyang
 * @date 2020/10/14
 */
@Getter
@Setter
@ToString
public class PullForm {

	@NotBlank
	private String startDate;
	@NotBlank
	private String endDate;
}
