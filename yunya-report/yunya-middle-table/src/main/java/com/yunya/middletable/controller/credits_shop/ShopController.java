package com.yunya.middletable.controller.credits_shop;

import com.yunya.middletable.service.credits_shop.CreditsShopBiz;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: yunya-dental
 * @description: 积分商城
 * @author: LHB
 * @create: 2021-04-21 16:07
 **/
@RestController
@Slf4j
@Api(tags = "积分商城")
@RequestMapping("/credits/shop")
public class ShopController {
    @Autowired
    private CreditsShopBiz creditsShopBiz;

}
