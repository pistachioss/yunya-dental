package com.yunya.feign.report.domain.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author xiangyang
 * @date 2020/10/14
 */
@Getter
@Setter
@ToString
public class MessageModel {

  /** 多个参数用map封装(key1-"id"; key2-"type"),为解决原是表是多张，中间表为一张的数据同步问题 */
  private Map<String, Object> paramMap;
  /** 操作类型（0-新增 1-修改 2-删除） */
  @ApiModelProperty(value = "操作类型（0-新增 1-修改 2-删除）", required = true)
  @NotNull(message = "消息数据操作类型不能为空！")
  private Integer operateType;
}
