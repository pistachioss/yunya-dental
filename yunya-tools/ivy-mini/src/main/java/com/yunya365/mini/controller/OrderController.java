package com.yunya365.mini.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.ivy_mini.domain.form.OrderDetailForm;
import com.yunya.feign.ivy_mini.domain.form.OrderForm;
import com.yunya.feign.ivy_mini.domain.form.OrderUpdateForm;
import com.yunya.feign.ivy_mini.domain.vo.OrderVO;
import com.yunya.feign.ivy_mini.domain.vo.OrderWechatDetailVO;
import com.yunya.feign.treatment.domain.vo.OrderDetailVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya365.mini.service.OrderService;
import com.yunya365.mini.service.impl.OrderAdminiServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @description:
 * @author: xy
 * @date 2022/4/28 15:52
 **/
@Api(tags = {"订单api"})
@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderAdminiServiceImpl orderAdminiService;

    @PostMapping("/findlist")
    @ApiOperation("后台-订单-列表")
    public ResponseResult<PageInfo<OrderVO>> findList(@RequestBody @Valid OrderForm form) {
        return ResponseUtil.success(orderAdminiService.findList(form));
    }

    @ApiOperation("后台-订单-发货/退款")
    @PutMapping("/update")
    @CurrentUser
    public ResponseResult update(@RequestBody @Validated OrderUpdateForm form) {
        return orderAdminiService.update(form);
    }

    @PostMapping("/findDetail")
    @ApiOperation("后台-订单-详情")
    public ResponseResult<OrderWechatDetailVO> findDetail(@RequestBody @Valid OrderDetailForm form) {
        return ResponseUtil.success(orderAdminiService.findDetail(form));
    }

}
