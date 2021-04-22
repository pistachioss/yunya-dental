package com.yunya.middletable.controller.credits_shop;

import com.yunya.middletable.service.credits_shop.CreditsShopBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: yunya-dental
 * @description: 积分商城
 * @author: LHB
 * @create: 2021-04-21 16:07
 **/
@RestController
@Slf4j
@Api(tags = "积分商城")
@RequestMapping("/duiba/credits/shop")
public class ShopController {
    @Autowired
    private CreditsShopBiz creditsShopBiz;

    @ApiOperation("生成免登录URL")
    @GetMapping("/dautoLogin")
    public String duibaAutoLogin(HttpServletRequest request) {

        // TODO 生成免登录URL
        return "";
    }
}
