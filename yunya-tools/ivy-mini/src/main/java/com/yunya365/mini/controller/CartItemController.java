package com.yunya365.mini.controller;


import com.yunya.feign.ivy_mini.domain.form.UpdateCartForm;
import com.yunya.feign.ivy_mini.domain.model.AddCartModel;
import com.yunya.feign.ivy_mini.domain.vo.CartVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.ICartItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

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
    @ApiOperation("【小程序】购物车列表")
    public ResponseResult<CartVO> listCart() {
        return ResponseUtil.success(cartItemService.listCart());
    }

    @PostMapping("/cart/add")
    @ApiOperation("【小程序】添加某个商品购物车")
    public ResponseResult<Boolean> add(@Valid @RequestBody AddCartModel model) {
        cartItemService.add(model);
        return ResponseUtil.success();
    }

    @PostMapping("/cart/update")
    @ApiOperation("【小程序】修改购物车某个商品数量")
    public ResponseResult<Boolean> updateQuantity(@Valid @RequestBody UpdateCartForm form) {
        cartItemService.updateQuantity(form);
        return ResponseUtil.success();
    }

    @PostMapping("/cart/delete")
    @ApiOperation("【小程序】删除购物车某个商品（可多选）")
    public ResponseResult<Boolean> delete(@Valid @RequestBody List<Integer> cartIds) {
        cartItemService.delete(cartIds);
        return ResponseUtil.success();
    }

    @PostMapping("/cart/clear")
    @ApiOperation("【小程序】清空购物车")
    public ResponseResult<Boolean> clear() {
        cartItemService.clear();
        return ResponseUtil.success();
    }

}

