package com.yunya365.mini.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunya.feign.ivy_mini.domain.model.OrderRefundApplyModel;
import com.yunya.feign.ivy_mini.domain.model.OrderRefundModel;
import com.yunya365.mini.entity.OrderInfo;
import com.yunya365.mini.entity.OrderReturnApply;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 订单退货申请 服务类
 * </p>
 *
 * @author xiangyang
 * @since 2022-07-04
 */
public interface IOrderReturnApplyService extends IService<OrderReturnApply> {

    void refundApply(OrderInfo orderInfo, OrderRefundApplyModel model);

    OrderReturnApply queryRefund(Integer orderId);

    void refund(OrderInfo orderInfo, OrderRefundModel model);

    List<OrderReturnApply> listLast(Collection<Integer> orderIds);
}
