package com.yunya365.mini.controller;


import com.github.pagehelper.PageInfo;
import com.yunya.feign.ivy_mini.domain.model.*;
import com.yunya.feign.ivy_mini.domain.query.ConfirmProductQuery;
import com.yunya.feign.ivy_mini.domain.query.MyOrderQuery;
import com.yunya.feign.ivy_mini.domain.vo.*;
import com.yunya.framework.common.annation.IgnoreUserToken;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.IOrderInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author xiangyang
 * @since 2022-06-06
 */
@RestController
@Api(tags = "订单")
public class OrderInfoController extends BaseController {

    @Resource
    private IOrderInfoService orderInfoService;

    @PostMapping("/product/order/confirm")
    @ApiOperation("【小程序】产品生成确认订单信息")
    public ResponseResult<ConfirmOrderVO> confirmProductOrder(@RequestBody @Valid ConfirmProductQuery query) {
        return ResponseUtil.success(orderInfoService.confirmProductOrder(query));
    }

    @PostMapping(value = "/cart/order/confirm")
    @ApiOperation("【小程序】根据购物车信息生成确认单")
    public ResponseResult<ConfirmOrderVO> generateConfirmOrder(@RequestBody List<Integer> cartIds) {
        return ResponseUtil.success(orderInfoService.confirmCartOrder(cartIds));
    }

    @PostMapping("/product/order/create")
    @ApiOperation("【小程序】产品详情页下单")
    public ResponseResult<CreateOrderVO> createProductOrder(@RequestBody @Valid CreateProductOrderModel model) {
        return ResponseUtil.success(orderInfoService.createProductOrder(model));
    }

    @PostMapping("/cart/order/create")
    @ApiOperation("【小程序】购物车页下单")
    public ResponseResult<CreateOrderVO> createProductOrder(@RequestBody @Valid CreateCartOrderModel model) {
        return ResponseUtil.success(orderInfoService.createCartOrder(model));
    }

    @GetMapping("/order/pay/query/{orderId}")
    @ApiOperation("【小程序】查询订单是否支付成功")
    public ResponseResult<WxOrderPayVO> payQuery(@PathVariable Integer orderId) {
        return ResponseUtil.success(orderInfoService.payQuery(orderId));
    }

    @PostMapping("/order/list")
    @ApiOperation("【小程序】我的订单")
    public ResponseResult<PageInfo<OrderFrontVO>> orderList(@RequestBody MyOrderQuery query) {
        return ResponseUtil.success(orderInfoService.orderList(query));
    }

    @GetMapping("/order/{orderId}")
    @ApiOperation("【小程序】订单详情")
    public ResponseResult<OrderDetailVO> orderDetail(@PathVariable Integer orderId) {
        return ResponseUtil.success(orderInfoService.orderDetail(orderId));
    }

    @PostMapping("/order/confirm/{orderId}")
    @ApiOperation("【小程序】确认收货")
    public ResponseResult<PayOrderVO> confirmDelivery(@PathVariable Integer orderId) {
        return ResponseUtil.success(orderInfoService.confirmDelivery(orderId));
    }

    @PostMapping("/order/refund")
    @ApiOperation("【小程序】申请退款")
    public ResponseResult<PayOrderVO> applyRefund(@RequestBody @Valid OrderRefundApplyModel model) {
        return ResponseUtil.success(orderInfoService.applyRefund(model));
    }

    @GetMapping("/order/refund/{orderId}")
    @ApiOperation("【小程序】申请退款详情")
    public ResponseResult<PayOrderVO> refundDetail(@PathVariable Integer orderId) {
        return ResponseUtil.success(orderInfoService.refundDetail(orderId));
    }

    @PostMapping("/order/refund/cancel/{orderId}")
    @ApiOperation("【小程序】取消退款")
    public ResponseResult<Boolean> cancelRefund(@PathVariable Integer orderId) {
        orderInfoService.cancelRefund(orderId);
        return ResponseUtil.success();
    }

    @PostMapping("/order/cancel/{orderId}")
    @ApiOperation("【小程序】取消订单")
    public ResponseResult<Boolean> cancel(@PathVariable Integer orderId) {
        orderInfoService.cancel(orderId);
        return ResponseUtil.success();
    }

    @PostMapping("/order/pay/continue/{orderId}")
    @ApiOperation("【小程序】继续支付订单")
    public ResponseResult<WxPaymentVO> continuePay(@PathVariable Integer orderId) {
        return ResponseUtil.success(orderInfoService.continuePay(orderId));
    }

    @PostMapping("/order/delete/{orderId}")
    @ApiOperation("【小程序】删除订单")
    public ResponseResult<Boolean> delete(@PathVariable Integer orderId) {
        orderInfoService.delete(orderId);
        return ResponseUtil.success();
    }

    @PostMapping("/wx/notify")
    @IgnoreUserToken
    public String wxNotify(HttpServletRequest request, HttpServletResponse response) {
        return orderInfoService.wxNotify(request, response);
    }

    @PostMapping("/wx/refund/notify")
    @IgnoreUserToken
    public String wxRefundNotify(HttpServletRequest request, HttpServletResponse response) {
        return orderInfoService.wxRefundNotify(request, response);
    }
}

