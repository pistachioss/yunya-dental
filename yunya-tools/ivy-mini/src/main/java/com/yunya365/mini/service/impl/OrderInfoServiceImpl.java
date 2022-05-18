package com.yunya365.mini.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
 * @since 2022-05-18
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements IOrderInfoService {

}
