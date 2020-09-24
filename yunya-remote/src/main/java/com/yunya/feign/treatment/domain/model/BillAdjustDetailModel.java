package com.yunya.feign.treatment.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 简介: 账单详情调整参数模型
 *
 * @author: chow
 * @date: 2020/9/24 10:52
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("账单详情调整参数模型")
public class BillAdjustDetailModel implements Serializable {

  /** 就诊记录ID */
  @ApiModelProperty(value = "账单记录ID", required = true)
  @NotNull(message = "账单记录ID不能为空！")
  private Integer billRecordId;

  /** 提交账单修改开单信息提交账单修改开单信息 */
  @NotEmpty(message = "提交账单修改开单信息不能不能为空！")
  private List<OrderDetailModel> orderDetailModels;

  @ApiModelProperty("备注")
  private String remark;
}
