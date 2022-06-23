package com.yunya365.mini.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunya.feign.ivy_mini.domain.model.CreateCartOrderModel;
import com.yunya.feign.ivy_mini.domain.model.CreateProductOrderModel;
import com.yunya.feign.ivy_mini.domain.query.ConfirmProductQuery;
import com.yunya.feign.ivy_mini.domain.vo.*;
import com.yunya365.mini.entity.OrderInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author xiangyang
 * @since 2022-06-06
 */
public interface IOrderInfoService extends IService<OrderInfo> {

    /**
     * 根据产品生成确认订单信息
     * @param query:
     * @return ConfirmOrderVO
     */
    ConfirmOrderVO confirmProductOrder(ConfirmProductQuery query);

    /**
     * 购物车生成确认订单信息
     * @param cartIds:
     * @return ConfirmOrderVO
     */
    ConfirmOrderVO confirmCartOrder(List<Integer> cartIds);

    /**
     * 创建商品或虚拟服务订单（商品详情界面下单）
     * @param model:
     * @return CreateOrderVO
     */
    CreateOrderVO createProductOrder(CreateProductOrderModel model);

    /**
     * 创建商品或虚拟服务订单（购物车下单）
     * @param model:
     * @return CreateOrderVO
     */
    CreateOrderVO createCartOrder(CreateCartOrderModel model);

    /**
     * 采宝支付回调
     * @param request:
	 * @param response:
     */
    void cbNotify(HttpServletRequest request, HttpServletResponse response);

    public List<CBQueryVO> queryPayOrder(Collection<Integer> orderIds);
}
