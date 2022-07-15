package com.yunya365.mini.controller;


import com.yunya.feign.ivy_mini.domain.form.VirtualActiveForm;
import com.yunya365.mini.service.IOrderVirtualService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * <p>
 * 虚拟卡券 前端控制器
 * </p>
 *
 * @author xiangyang
 * @since 2022-07-13
 */
@RestController
public class OrderVirtualController {

    @Resource
    private IOrderVirtualService virtualService;

    @PostMapping("/orderVirtual/active/save")
    public void activeCard(@RequestBody @Valid VirtualActiveForm form) {
       virtualService.activeCard(form);
    }
}

