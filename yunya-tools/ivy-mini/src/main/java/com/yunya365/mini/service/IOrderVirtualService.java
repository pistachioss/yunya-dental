package com.yunya365.mini.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunya.feign.ivy_mini.domain.form.VirtualActiveForm;
import com.yunya365.mini.entity.OrderVirtual;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 虚拟卡券 服务类
 * </p>
 *
 * @author xiangyang
 * @since 2022-07-13
 */
public interface IOrderVirtualService extends IService<OrderVirtual> {

    List<OrderVirtual> listByOrderIds(Collection<Integer> ids, Boolean deleteStatus);

    void deleteOrderCard(Integer orderId);

    void soldActiveOrInvalid(Integer orderId, boolean status);

    void activeCard(VirtualActiveForm form);

    void deleteCard(Integer cardId);
}
