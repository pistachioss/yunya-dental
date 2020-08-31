package com.yunya.feign.treatment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 简介: 授权折扣参数模型
 *
 * @author: chow
 * @date: 2020/8/27 16:45
 * @description:
 * @since: 1.0.0
 */
@ApiModel("授权折扣参数模型")
@Data
@ToString
public class AccreditDiscountModel implements Serializable {
  /** 授权人ID */
  @ApiModelProperty(value = "授权人ID", required = true)
  @NotNull(message = "授权人ID不能为空！")
  private Integer warrantId;

  @ApiModelProperty("授权备注")
  @Size(max = 150, message = "备注不能超过150个字符！")
  private String remarks;

  /** 授权折扣详情信息 */
  private List<AccreditDiscountDetailModel> accreditDiscountDetailModels;
}
