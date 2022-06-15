package com.yunya365.mini.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunya.feign.ivy_mini.domain.model.CreateGoodsOrderModel;
import com.yunya.feign.ivy_mini.domain.model.CreateVirtualOrderModel;
import com.yunya.feign.ivy_mini.domain.vo.CreateOrderVO;
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

    CreateOrderVO createGoodsOrder(CreateGoodsOrderModel model);

    CreateOrderVO createVirtualOrder(CreateVirtualOrderModel model);

    void paySuccess();
}
