package com.yunya.modules.treatment.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.treatment.OrderDetailPayRecord;
import com.yunya.modules.treatment.mapper.OrderDetailPayRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  /***/
}
