package com.yunya.modules.treatment.biz;

import com.yunya.feign.treatment.domain.model.TollModel;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.treatment.OrderRecord;
import com.yunya.modules.treatment.mapper.OrderDetailMapper;
import com.yunya.modules.treatment.mapper.OrderRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 简介: 就诊收费业务层
 *
 * @author: chow
 * @date: 2020/8/21 20:53
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TollBiz {

  /** 开单记录 */
  @Autowired private OrderRecordMapper orderRecordMapper;
  /** 开单明细 */
  @Autowired private OrderDetailMapper orderDetailMapper;

  /**
   * 确认收费
   *
   * @param model 收费参数
   */
  public void confirm(TollModel model) {
    Integer orderRecordId = model.getOrderRecordId();
    OrderRecord orderRecord = orderRecordMapper.selectByPrimaryKey(orderRecordId);
    if (null == orderRecord) {
      throw new ClientServiceException(
          "收费失败，当前未选择正确的就诊记录或传入参数有误！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }
    Byte status = orderRecord.getStatus();
    if (!status.equals(BusinessConstants.ORDER_LOCK_STATUS)) {
      throw new ClientServiceException(
          "收费失败，当前账单处于解锁状态或收费完成状态，暂不能进行收费！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }
  }
}
