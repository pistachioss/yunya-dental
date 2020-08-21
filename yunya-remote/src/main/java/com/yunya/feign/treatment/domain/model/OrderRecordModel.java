package com.yunya.feign.treatment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 简介: 开单信息新增参数模型
 *
 * @author: chow
 * @date: 2020/8/17 20:24
 * @description:
 * @since: 1.0.0
 */
@ApiModel("开单信息新增参数模型")
@Data
@ToString
public class OrderRecordModel implements Serializable {

  /** 就诊记录ID */
  @ApiModelProperty(value = "就诊记录ID", required = true)
  @NotNull(message = "就诊记录ID不能为空！")
  private Integer treatmentRecordId;
  /** 助手1ID */
  @ApiModelProperty("助手1ID")
  private Integer assistantId1;
  /** 助手2ID */
  @ApiModelProperty("助手2ID")
  private Integer assistantId2;
  /** 助手3ID */
  @ApiModelProperty("助手3ID")
  private Integer assistantId3;
  /** 开单详情 */
  private List<OrderDetailModel> orderDetails;
}
