package com.yunya.feign.report.domain.model;

import com.yunya.feign.report.enums.MsgCategoryEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

/**
 * @author xiangyang
 * @date 2020/10/14
 */
@Getter
@Setter
@ToString
public class MessageModel implements Serializable {

  /** 为解决原是表是多张，中间表为一张的数据同步问题，多个参数用map封装(key1-"id",强制; key2-"type"，建议) */
  private Map<String, Object> paramMap;

  /** 操作类型（0-新增 1-修改 2-删除） */
  @ApiModelProperty(value = "操作类型（0-新增 1-修改 2-删除）", required = true)
  @NotNull(message = "消息数据操作类型不能为空！")
  private Integer operateType;

  @ApiModelProperty(value = "操作对象，用消息分类代表，一个中间表可有多个分类", required = true)
  @NotNull(message = "消息操作对象不能为空！")
  private MsgCategoryEnum msgCategoryEnum;

  @ApiModelProperty(value = "消息识别ID，不必传，调用消息服务时自动生成")
  private String MsgID;
}
