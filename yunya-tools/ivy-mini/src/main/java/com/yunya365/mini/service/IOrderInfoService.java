package com.yunya365.mini.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.ivy_mini.domain.model.*;
import com.yunya.feign.ivy_mini.domain.query.ConfirmProductQuery;
import com.yunya.feign.ivy_mini.domain.query.MyOrderQuery;
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
     * 微信支付回调
     * @param request:
	 * @param response:
     */
    String wxNotify(HttpServletRequest request, HttpServletResponse response);

    /**
     * 查询微信支付订单
     * @param orderIds:
     * @return List<WxPayOrderQueryResult>
     */
    List<WxPayOrderQueryResult> queryPayOrder(Collection<Integer> orderIds) throws WxPayException;

    /**
     * 订单列表
     * @param query:
     * @return PageInfo<OrderFrontVO>
     */
    PageInfo<OrderFrontVO> orderList(MyOrderQuery query);

    /**
     * 查询订单微信支付状态
     * @param orderId:
     * @return WxOrderPayVO
     */
    WxOrderPayVO payQuery(Integer orderId);

    /**
     * 订单详情
     * @param orderId:
     * @return OrderDetailVO
     */
    OrderDetailVO orderDetail(Integer orderId);

    /**
     * 确认收货
     * @param orderId:
     * @return ConfirmDeliveryVO
     */
    PayOrderVO confirmDelivery(Integer orderId);

    /**
     * 申请退款
     * @param model:
     * @return PayOrderVO
     */
    PayOrderVO applyRefund(OrderRefundApplyModel model);

    /**
     * 取消订单
     * @param orderId:
     */
    void cancel(Integer orderId);

    /**
     * 删除订单
     * @param orderId:
     */
    void delete(Integer orderId);

    /**
     * 取消退款
     * @param orderId:
     */
    void cancelRefund(Integer orderId);

    /**
     * 继续支付
     * @param orderId:
     * @return WxPaymentVO
     */
    WxPaymentVO continuePay(Integer orderId);

    void refund(OrderRefundModel model);

    /**
     * 微信退款支付回调
     * @param request:
     * @param response:
     */
    String wxRefundNotify(HttpServletRequest request, HttpServletResponse response);

    void handleDelayPay(Integer orderId);
}
