package com.yunya.modules.treatment.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.treatment.OrderDetailPayRecord;
import com.yunya.modules.treatment.mapper.OrderDetailPayRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 简介: 订单明细支付记录业务层
 *
 * @author: chow
 * @date: 2020/11/6 10:14
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrderDetailPayRecordBiz
    extends BaseBiz<OrderDetailPayRecordMapper, OrderDetailPayRecord> {

  /**
   * 查询账单优惠为0的订单金额
   *
   * @param billRecordId 账单记录ID
   * @return BigDecimal
   */
  public BigDecimal selectNoDiscountAmount(Integer billRecordId) {
    return mapper.selectNoDiscountAmount(billRecordId);
  }

  /**
   * 更新订单明细收费的已收金额
   *
   * @param billRecordId 账单记录ID
   * @param receivedAmount 撤销收费金额
   */
  public void updateReceivedAmount(Integer billRecordId, BigDecimal receivedAmount) {
    OrderDetailPayRecord entity = new OrderDetailPayRecord();
    entity.setBillRecordId(billRecordId);
    List<OrderDetailPayRecord> list = mapper.select(entity);
    if (StringHelper.isNotEmpty(list)) {
      list = list.stream().sorted((v1, v2) -> v2.getId() - v1.getId()).collect(Collectors.toList());
      for (OrderDetailPayRecord vo : list) {
        BigDecimal amount = vo.getReceivedAmount();
        receivedAmount = receivedAmount.subtract(amount);
        if (receivedAmount.compareTo(BigDecimal.ZERO) <= 0) {
          // receivedAmount已经用完了
          vo.setReceivedAmount(receivedAmount.abs());
          mapper.updateByPrimaryKeySelective(vo);
          break;
        } else {
          vo.setReceivedAmount(BigDecimal.ZERO);
          mapper.updateByPrimaryKeySelective(vo);
        }
      }
    }
  }
}
