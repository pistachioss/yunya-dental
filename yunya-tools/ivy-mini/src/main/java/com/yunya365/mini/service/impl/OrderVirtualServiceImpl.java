package com.yunya365.mini.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.yunya365.mini.entity.OrderVirtual;
import com.yunya365.mini.mapper.OrderVirtualMapper;
import com.yunya365.mini.service.IOrderVirtualService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static com.yunya.framework.common.enums.TrueFalseEnum.*;

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
    public List<OrderVirtual> listByOrderIds(Collection<Integer> ids) {
        return ChainWrappers.lambdaQueryChain(baseMapper).in(OrderVirtual::getOrderId, ids)
                .eq(OrderVirtual::getDeleteStatus, FALSE.getCode())
                .list();
    }

    @Override
    public void deleteOrderCard(Integer orderId) {
        ChainWrappers.lambdaUpdateChain(baseMapper)
                .eq(OrderVirtual::getOrderId, orderId).set(OrderVirtual::getDeleteStatus, TRUE.getCode()).update();
    }
}
