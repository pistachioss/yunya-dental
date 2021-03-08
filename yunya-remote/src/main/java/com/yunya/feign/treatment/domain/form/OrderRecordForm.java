package com.yunya.feign.treatment.domain.form;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yunya.feign.treatment.domain.model.OrderDetailModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 简介: 开单信息修改参数模型
 *
 * @author: chow
 * @date: 2020/8/31 13:03
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("开单信息修改参数模型")
public class OrderRecordForm implements Serializable {
  /** 助手1ID */
  @ApiModelProperty("助手1ID")
  @JsonIgnoreProperties(ignoreUnknown = true)
  private Integer assistantId1;
  /** 助手2ID */
  @ApiModelProperty("助手2ID")
  @JsonIgnoreProperties(ignoreUnknown = true)
  private Integer assistantId2;
  /** 助手3ID */
  @ApiModelProperty("助手3ID")
  @JsonIgnoreProperties(ignoreUnknown = true)
  private Integer assistantId3;
  /** 开单信息修改参数列表 */
  private List<OrderDetailModel> orderDetails;
}
