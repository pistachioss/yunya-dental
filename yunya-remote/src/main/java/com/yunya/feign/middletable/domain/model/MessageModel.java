package com.yunya.feign.middletable.domain.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @author xiangyang
 * @date 2020/10/14
 */
@Getter
@Setter
@ToString
public class MessageModel {

	/**
	 * "采集消息id
	 */
	@ApiModelProperty(value = "采集消息id", required = true)
	@NotNull
	private Integer id;
	/**
	 * 操作类型（0-新增 1-修改 2-删除）
	 */
	@ApiModelProperty(value = "操作类型（0-新增 1-修改 2-删除）", required = true)
	@NotNull
	private Integer operateType;
}
