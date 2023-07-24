package com.yunya.feign.treatment.domain.vo;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 简介: 账单详情组合信息VO
 *
 * @author: chow
 * @date: 2020/9/11 17:35
 * @description:
 * @since: 1.0.0
 */
@Data
@ToString
@ApiModel("账单详情组合信息VO")
public class BillDetailGroupVO extends TreatOrderRecordVO {

  /** 收费记录列表 */
  @ApiModelProperty("收费记录列表")
  private List<BillPayRecordVO> billPayRecords = Lists.newArrayList();

  /** 账单异常处理列表 */
  @ApiModelProperty("账单异常处理列表")
  private List<BillHandleRecordVO> billHandleRecords = Lists.newArrayList();
}
