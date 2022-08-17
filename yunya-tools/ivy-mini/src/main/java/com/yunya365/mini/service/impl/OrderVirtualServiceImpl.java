package com.yunya365.mini.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.yunya.feign.ivy_mini.domain.form.VirtualActiveForm;
import com.yunya365.mini.entity.OrderInfo;
import com.yunya365.mini.entity.OrderVirtual;
import com.yunya365.mini.mapper.OrderVirtualMapper;
import com.yunya365.mini.service.IOrderInfoService;
import com.yunya365.mini.service.IOrderVirtualService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.yunya365.mini.enums.OrderStatusEnum.*;

/**
 * <p>
 * 虚拟卡券 服务实现类
 * </p>
 *
 * @author xiangyang
 * @since 2022-07-13
 */
@Service
public class OrderVirtualServiceImpl extends ServiceImpl<OrderVirtualMapper, OrderVirtual> implements IOrderVirtualService {

    @Resource
    private IOrderInfoService orderInfoService;

    @Override
    public List<OrderVirtual> listByOrderIds(Collection<Integer> ids, Boolean deleteStatus) {
        return ChainWrappers.lambdaQueryChain(baseMapper).in(OrderVirtual::getOrderId, ids)
                .eq(Objects.nonNull(deleteStatus), OrderVirtual::getDeleteStatus, deleteStatus)
                .list();
    }

    @Override
    public void deleteOrderCard(Integer orderId) {
        ChainWrappers.lambdaUpdateChain(baseMapper)
                .eq(OrderVirtual::getOrderId, orderId).remove();
    }

    @Override
    public void soldActiveOrInvalid(Integer orderId, boolean status) {
        ChainWrappers.lambdaUpdateChain(baseMapper)
                .eq(OrderVirtual::getOrderId, orderId)
                .eq(OrderVirtual::getDeleteStatus, !status)
                .set(OrderVirtual::getDeleteStatus, status).update();
    }

    @Override
    public void activeCard(VirtualActiveForm form) {
        Integer cardId = form.getCardId();
        OrderVirtual virtual = ChainWrappers.lambdaQueryChain(baseMapper)
                .eq(OrderVirtual::getCardId, cardId)
                .eq(OrderVirtual::getDeleteStatus, false)
                .one();
        if (Objects.nonNull(virtual)) {
            //订单核销状态更新
            boolean calActiveStatus = calActiveStatus(virtual.getOrderSn());
            if (calActiveStatus) {
                orderInfoService.activeStatus(virtual.getOrderId(), true);
            }
            ChainWrappers.lambdaUpdateChain(baseMapper)
                    .eq(OrderVirtual::getOrderId, virtual.getOrderId())
                    .eq(OrderVirtual::getCardId, form.getCardId())
                    .set(OrderVirtual::getActiveMobile, form.getPatientMobile())
                    .set(OrderVirtual::getPatientId, form.getPatientId())
                    .set(OrderVirtual::getPatientName, form.getPatientName())
                    .set(OrderVirtual::getActiveDate, form.getActiveDate())
                    .set(OrderVirtual::getUpdId, form.getActiveUserId())
                    .update();
        }
    }

    @Override
    public void deleteCard(Integer cardId) {
        ChainWrappers.lambdaUpdateChain(baseMapper)
                .eq(OrderVirtual::getCardId, cardId).remove();
    }

    @Override
    public boolean cardRefund(Integer cardId) {
        OrderVirtual orderVirtual = ChainWrappers.lambdaQueryChain(baseMapper)
                .select(OrderVirtual::getOrderId)
                .eq(OrderVirtual::getCardId, cardId)
                .isNull(OrderVirtual::getPatientId).one();
        if (Objects.nonNull(orderVirtual)) {
            OrderInfo orderInfo = orderInfoService.getById(orderVirtual.getOrderId());
            return Objects.equals(APPLY_REFUND.getCode().byteValue(), orderInfo.getStatus());
        }
        return false;
    }

    private boolean calActiveStatus(String orderSn) {
        //未核销卡券
        Long count = ChainWrappers.lambdaQueryChain(baseMapper)
                .eq(OrderVirtual::getOrderSn, orderSn)
                .eq(OrderVirtual::getDeleteStatus, false)
                .isNull(OrderVirtual::getPatientId).count();
        return count > 0;
    }
}
