package com.yunya365.mini.mapper;

import com.yunya.feign.ivy_mini.domain.form.OrderForm;
import com.yunya.feign.ivy_mini.domain.vo.OrderVO;
import com.yunya365.mini.entity.Order;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface OrderMapper extends Mapper<Order> {

    List<OrderVO> findOrderList(OrderForm form);
}