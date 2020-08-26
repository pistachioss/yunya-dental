package com.yunya.modules.treatment.biz;

import com.yunya.feign.treatment.domain.model.TollModel;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.redis.util.RedisUtils;
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
  @Autowired private OrderRecordBiz orderRecordBiz;
  /** 开单明细 */
  @Autowired private OrderDetailBiz orderDetailBiz;
  /** 缓存 */
  @Autowired private RedisUtils redisUtils;

  /**
   * 确认收费
   *
   * @param model 收费参数
   */
  public void confirm(TollModel model) {
    Integer orderRecordId = model.getOrderRecordId();
    OrderRecord orderRecord = orderRecordBiz.selectById(orderRecordId);
    if (null == orderRecord) {
      throw new ClientServiceException(
          "收费失败，当前未选择正确的就诊记录或传入参数有误！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }
    Byte status = orderRecord.getStatus();
    if (!status.equals(BusinessConstants.ORDER_LOCK_STATUS)) {
      throw new ClientServiceException(
          "收费失败，当前账单处于解锁状态或收费完成状态，暂不能进行收费！", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }

    String resultRecordId = redisUtils.get(RedisConstants.LOCK_ORDER_PROCESSING_UNLOCK);
    if(StringHelper.isNotBlank(resultRecordId)){
      throw new ClientServiceException("收费失败，当前账单正在修改！",OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }

    redisUtils.set(RedisConstants.LOCK_ORDER_PROCESSING_CHARGE + orderRecordId, orderRecordId);

    redisUtils.delete(RedisConstants.LOCK_ORDER_PROCESSING_CHARGE);
  }
}
