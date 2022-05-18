package com.yunya365.mini.mapper;

import com.yunya.feign.ivy_mini.domain.form.OrderForm;
import com.yunya.feign.ivy_mini.domain.vo.OrderVO;
import com.yunya.feign.ivy_mini.domain.vo.OrderWechatDetailVO;
import com.yunya365.mini.entity.OrderInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface OrderInfoMapper extends Mapper<OrderInfo> {


    List<OrderVO> findOrderList(OrderForm form);

    OrderWechatDetailVO findDetail(Integer id);
}