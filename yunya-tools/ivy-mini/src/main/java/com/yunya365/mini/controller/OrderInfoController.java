package com.yunya365.mini.controller;


import com.yunya.feign.ivy_mini.domain.model.CreateCartOrderModel;
import com.yunya.feign.ivy_mini.domain.model.CreateProductOrderModel;
import com.yunya.feign.ivy_mini.domain.query.ConfirmProductQuery;
import com.yunya.feign.ivy_mini.domain.vo.ConfirmOrderVO;
import com.yunya.feign.ivy_mini.domain.vo.CreateOrderVO;
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

    @PostMapping("/cb/notify")
    @IgnoreUserToken
    public ResponseResult<Boolean> cbNotify(HttpServletRequest request, HttpServletResponse response) {
        orderInfoService.cbNotify(request, response);
        return ResponseUtil.success();
    }
}

