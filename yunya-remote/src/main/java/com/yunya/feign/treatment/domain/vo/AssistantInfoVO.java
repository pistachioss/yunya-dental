package com.yunya.feign.treatment.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 简介: 配诊助手信息VO
 *
 * @author: chow
 * @date: 2020/8/21 10:01
 * @description:
 * @since: 1.0.0
 */
@ApiModel("配诊助手信息VO")
@Data
@ToString
public class AssistantInfoVO implements Serializable {
  /** 助手ID */
  @ApiModelProperty("助手ID")
  private Integer assistantId;
  /** 助手类型(0-助手1；1-助手2；2-巡回) */
  @ApiModelProperty("助手类型(0-助手1；1-助手2；2-巡回)")
  private Byte type;
  /** 助手姓名 */
  @ApiModelProperty("助手姓名")
  private String assistantName;
}
