package com.yunya365.mini.service.impl;

import com.yunya.feign.ivy_mini.domain.model.OrderRefundModel;
import com.yunya365.mini.entity.OrderInfo;
import com.yunya365.mini.entity.OrderReturnApply;
import com.yunya365.mini.mapper.OrderReturnApplyMapper;
import com.yunya365.mini.service.IOrderReturnApplyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.yunya365.mini.enums.OrderRefundEnum.*;

/**
 * <p>
 * 订单退货申请 服务实现类
 * </p>
 *
 * @author xiangyang
 * @since 2022-07-04
 */
@Service
public class OrderReturnApplyServiceImpl extends ServiceImpl<OrderReturnApplyMapper, OrderReturnApply> implements IOrderReturnApplyService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refund(OrderInfo orderInfo, OrderRefundModel model) {
        OrderReturnApply apply = new OrderReturnApply();
        apply.setOrderId(model.getOrderId());
        apply.setOrderSn(orderInfo.getOrderSn());
        apply.setReturnAmount(orderInfo.getPayAmount());
        apply.setReturnName(orderInfo.getReceiverName());
        apply.setReturnPhone(orderInfo.getReceiverPhone());
        apply.setStatus(HANDLE_PENDING.getCode());
        apply.setDeliveryStatus(model.getStatus());
        apply.setReason(model.getRefundReason());
        baseMapper.insert(apply);
    }
}
