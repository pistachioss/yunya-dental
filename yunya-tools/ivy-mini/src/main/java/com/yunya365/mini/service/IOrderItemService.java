package com.yunya365.mini.service;

import com.yunya365.mini.entity.OrderItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 订单明细 服务类
 * </p>
 *
 * @author xiangyang
 * @since 2022-06-10
 */
public interface IOrderItemService extends IService<OrderItem> {

    List<OrderItem> listByOrderIds(Collection<Integer> ids);

    void delete(Integer orderId);
}
