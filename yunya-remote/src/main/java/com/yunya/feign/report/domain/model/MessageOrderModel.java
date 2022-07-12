package com.yunya.feign.report.domain.model;

import com.yunya.feign.report.enums.MsgCategoryEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

/**
 * @author xiangyang
 * @date 2020/10/14
 */
@Data
public class MessageOrderModel implements Serializable {

  /** 为解决原是表是多张，中间表为一张的数据同步问题，多个参数用map封装(key1-"id",强制; key2-"type"，建议) */
  private Map<String, Object> paramMap;

  @ApiModelProperty(value = "消息识别ID，不必传，调用消息服务时自动生成")
  private String MsgID;
}
