package com.yunya.feign.middletable.domain.model;

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
public class MessageModel {
	private Integer id;
	/**
	 * 0-新增 1-修改 2-删除
	 */
	private Integer operateId;
}
