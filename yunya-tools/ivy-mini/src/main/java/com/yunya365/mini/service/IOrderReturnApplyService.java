package com.yunya365.mini.service;

import com.yunya.feign.ivy_mini.domain.model.OrderRefundModel;
import com.yunya365.mini.entity.OrderInfo;
import com.yunya365.mini.entity.OrderReturnApply;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单退货申请 服务类
 * </p>
 *
 * @author xiangyang
 * @since 2022-07-04
 */
public interface IOrderReturnApplyService extends IService<OrderReturnApply> {

    void refund(OrderInfo orderInfo, OrderRefundModel model);
}
