package com.yunya365.mini.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.yunya.feign.ivy_mini.domain.form.VirtualActiveForm;
import com.yunya365.mini.entity.OrderVirtual;
import com.yunya365.mini.mapper.OrderVirtualMapper;
import com.yunya365.mini.service.IOrderVirtualService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
        String orderSn = form.getOrderSn();
        Integer cardId = form.getCardId();
        Long count = ChainWrappers.lambdaQueryChain(baseMapper)
                .eq(OrderVirtual::getOrderSn, orderSn)
                .eq(OrderVirtual::getCardId, cardId)
                .eq(OrderVirtual::getDeleteStatus, false)
                .count();
        if (count > 0) {
            ChainWrappers.lambdaUpdateChain(baseMapper)
                    .eq(OrderVirtual::getOrderSn, form.getOrderSn())
                    .eq(OrderVirtual::getCardId, form.getCardId())
                    .set(OrderVirtual::getActiveMobile, form.getPatientMobile())
                    .set(OrderVirtual::getPatientId, form.getPatientId())
                    .set(OrderVirtual::getPatientName, form.getPatientName())
                    .set(OrderVirtual::getActiveDate, form.getActiveDate())
                    .set(OrderVirtual::getUpdId, form.getActiveUserId())
                    .update();
        }

    }
}
