package com.yunya.middletable.controller.credits_shop;

import com.yunya.feign.report.domain.query.PatientCreditsRecordQuery;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.credits_shop.CreditsShopBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @program: yunya-dental
 * @description: 积分商城
 * @author: LHB
 * @create: 2021-04-21 16:07
 **/
@RestController
@Slf4j
@Api(tags = "积分商城")
@RequestMapping("/duiba/shop")
public class ShopController {
    @Autowired
    private CreditsShopBiz creditsShopBiz;

    @ApiOperation("生成免登录URL")
    @GetMapping("/dautoLogin")
    public String duibaAutoLogin(@NotBlank(message = "openID不能为空") String openId,
                                 @NotNull(message = "患者ID不能为空") Integer patientId) {
        return creditsShopBiz.duibaAutoLogin(openId,patientId);
    }

    @ApiOperation("查询患者积分记录")
    @GetMapping("/credits/record")
    public ResponseResult patientCreditsRecord(PatientCreditsRecordQuery query) {
        return creditsShopBiz.selectPatientCreditsRecord(query);
    }

    @GetMapping("/credits/{patientId}")
    public ResponseResult lastPatientCredits(@NotNull(message = "患者ID不能为空") @PathVariable("patientId") Integer patientId) {
        return creditsShopBiz.lastPatientCredits(patientId);
    }

    @ApiOperation("初始患者化积分")
    @PostMapping("/initialization")
    public ResponseResult<T> initialization() throws InterruptedException {
        creditsShopBiz.initialization();
        return ResponseUtil.success();
    }
}
