package com.yunya.feign.treatment.domain.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
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
public class BillDetailGroupVO implements Serializable {
  /** 开单明细列表 */
  private List<OrderDetailChargeVO> orderDetails;

  /** 收费记录列表 */
  private List<BillPayRecordVO> billPayRecords;

  /** 账单异常处理列表 */
  private List<BillHandleRecordVO> billHandleRecords;
}
