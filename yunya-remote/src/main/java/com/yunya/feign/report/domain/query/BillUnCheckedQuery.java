package com.yunya.feign.report.domain.query;

import com.yunya.framework.common.model.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 简介: 未结账订单记录查询参数
 *
 * @author: chow
 * @date: 2021/1/26 14:08
 * @description:
 * @since: 1.0.0
 */
@ApiModel("未结账订单记录查询参数模型")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class BillUnCheckedQuery extends PageQuery implements Serializable {
  /** 订单编号 */
  @ApiModelProperty(value = "订单编号")
  private String orderRecordNum;
  /** 检索关键字 患者姓名/手机号/拼音名字 */
  @ApiModelProperty(value = "检索关键字 患者姓名/手机号/拼音名字")
  private String search;
  /** 开单门诊ID列表 */
  @ApiModelProperty(value = "开单门诊ID列表", required = true)
  @Size(min = 1, message = "门诊不能为空！")
  private Integer[] orgIds;
}
