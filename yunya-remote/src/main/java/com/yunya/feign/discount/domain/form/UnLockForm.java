package com.yunya.feign.discount.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author xiangyang
 * @date 2020/11/6
 */
@Data
@ApiModel(value = "解锁参数")
public class UnLockForm {
	@ApiModelProperty(value = "操作人", required = true)
	@NotNull
	private Integer requestId;
	@ApiModelProperty(value = "加锁对象，卡券售出:lock:card:sold", required = true)
	@NotBlank
	private String lockPrefix;
}
