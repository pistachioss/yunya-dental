package com.yunya365.mini.service.impl;

import com.yunya365.mini.entity.OrderItem;
import com.yunya365.mini.mapper.OrderItemMapper;
import com.yunya365.mini.service.IOrderItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单明细 服务实现类
 * </p>
 *
 * @author xiangyang
 * @since 2022-06-10
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements IOrderItemService {

}
