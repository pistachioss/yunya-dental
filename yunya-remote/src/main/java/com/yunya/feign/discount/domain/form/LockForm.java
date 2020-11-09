package com.yunya.feign.discount.domain.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author xiangyang
 * @date 2020/11/6
 */
@Data
@ApiModel(value = "加锁参数")
public class LockForm {
	@ApiModelProperty(value = "需加锁资源", required = true)
	@NotEmpty
	private List<Integer> ids;
	@ApiModelProperty(value = "加锁对象，卡券售出:lock:card:sold", required = true)
	@NotBlank
	private String lockPrefix;
}
