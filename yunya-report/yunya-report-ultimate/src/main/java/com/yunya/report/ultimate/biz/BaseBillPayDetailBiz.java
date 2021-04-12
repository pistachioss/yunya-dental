package com.yunya.report.ultimate.biz;

import com.yunya.feign.report.domain.query.BillDiscountAndFreePaymentQuery;
import com.yunya.feign.report.domain.vo.BillDiscountAndFreePaymentVO;
import com.yunya.feign.report.domain.vo.BillPayFreePayAmountVO;
import com.yunya.feign.report.domain.vo.StatementPaymentVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.report.BaseBillPayDetail;
import com.yunya.report.ultimate.mapper.BaseBillPayDetailMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * 简介: 收费明细记录业务层
 *
 * @author: chow
 * @date: 2021/3/22 13:57
 * @description:
 * @since: 1.0.0
 */
@Service
public class BaseBillPayDetailBiz extends BaseBiz<BaseBillPayDetailMapper, BaseBillPayDetail> {

  /**
   * 根据收费记录ID查询对账单支付方式列表
   *
   * @param billPayId 收费记录ID
   * @return list
   */
  public List<StatementPaymentVO> findBillPayDetailList(Integer billPayId) {
    return mapper.selectBillPayDetailList(billPayId);
  }

  /**
   * 根据收费记录ID查询免单支付金额
   *
   * @param billPayId 支付记录ID
   * @return
   */
  public BigDecimal findFreePayAmount(Integer billPayId) {
    return mapper.selectFreePayAmount(billPayId);
  }

  /**
   * 查询支付记录列表对应免单支付金额
   *
   * @param billPayIds 收费记录ID列表
   * @return
   */
  public List<BillPayFreePayAmountVO> findBillFreePayAmountList(Collection<Integer> billPayIds) {
    return mapper.selectTotalFreePayAmount(billPayIds);
  }

  public List<BillDiscountAndFreePaymentVO> billDiscountAndFreePaymentList(
      BillDiscountAndFreePaymentQuery query) {
    return mapper.billDiscountAndFreePaymentList(query);
  }
}
