package com.yunya.modules.treatment.biz;

import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.treatment.OrderDetail;
import com.yunya.modules.treatment.mapper.OrderDetailMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/8/18 11:16
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrderDetailBiz extends BaseBiz<OrderDetailMapper, OrderDetail> {
  /***/
}
