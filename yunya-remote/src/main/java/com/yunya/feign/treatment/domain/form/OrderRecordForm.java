package com.yunya.feign.treatment.domain.form;

import com.yunya.feign.treatment.domain.model.OrderDetailModel;
import io.swagger.annotations.ApiModel;
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
  /** 开单信息修改参数列表 */
  private List<OrderDetailModel> orderDetails;
}
