package com.yunya365.mini.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.yunya.feign.ivy_mini.domain.model.OrderRefundModel;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya365.mini.entity.OrderInfo;
import com.yunya365.mini.entity.OrderReturnApply;
import com.yunya365.mini.mapper.OrderReturnApplyMapper;
import com.yunya365.mini.service.IOrderReturnApplyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.yunya365.mini.enums.IvyMiniError.*;
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
        OrderReturnApply returnApply = ChainWrappers.lambdaQueryChain(baseMapper)
                .eq(OrderReturnApply::getOrderId, orderInfo.getId()).orderByDesc(OrderReturnApply::getCrtTime)
                .last("limit 1").one();
        if (Objects.nonNull(returnApply)) {
            if (Objects.equals(REFUND_FINISH.getCode(), returnApply.getStatus())) {
                throw ClientServiceException.wrap(ORDER_REFUND_FINISH);
            }
            if (!Objects.equals(REFUND_REFUSE.getCode(), returnApply.getStatus())) {
                throw ClientServiceException.wrap(ORDER_REFUNDING);
            }
        }
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

    @Override
    public OrderReturnApply queryRefund(Integer orderId) {
        return ChainWrappers.lambdaQueryChain(baseMapper)
                .eq(OrderReturnApply::getOrderId, orderId).orderByDesc(OrderReturnApply::getCrtTime)
                .last("limit 1").one();
    }
}
