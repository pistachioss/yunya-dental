package com.yunya365.mini.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunya365.mini.entity.OrderItem;
import tk.mybatis.mapper.common.Mapper;

/**
 * <p>
 * 订单明细 Mapper 接口
 * </p>
 *
 * @author xiangyang
 * @since 2022-06-10
 */
public interface OrderItemMapper extends Mapper<OrderItem>, BaseMapper<OrderItem> {

}
