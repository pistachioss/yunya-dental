package com.yunya365.mini.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunya.feign.ivy_mini.domain.model.CreateGoodsOrderModel;
import com.yunya365.mini.entity.OrderInfo;
import com.yunya365.mini.mapper.OrderInfoMapper;
import com.yunya365.mini.service.IOrderInfoService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author xiangyang
 * @since 2022-06-06
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements IOrderInfoService {

    @Override
    public void createGoodsOrder(CreateGoodsOrderModel model) {
        //加锁
        //库存数量

    }

    @Override
    public void paySuccess() {

    }
}
