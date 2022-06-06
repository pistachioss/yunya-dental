package com.yunya365.mini.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunya365.mini.entity.OrderInfo;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author xiangyang
 * @since 2022-06-06
 */
public interface IOrderInfoService extends IService<OrderInfo> {

    void createOrder();

    void paySuccess();
}
