package com.yunya365.mini.controller;


import com.yunya.feign.ivy_mini.domain.vo.CartVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.ICartItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 购物车 前端控制器
 * </p>
 *
 * @author xiangyang
 * @since 2022-05-27
 */
@RestController
@Api(tags = "购物车")
public class CartItemController extends BaseController{

    @Resource
    private ICartItemService cartItemService;

    @PostMapping("/cart/list")
    @ApiOperation("购物车列表")
    public ResponseResult<CartVO> listCart() {
        return ResponseUtil.success(cartItemService.listCart());
    }
}

