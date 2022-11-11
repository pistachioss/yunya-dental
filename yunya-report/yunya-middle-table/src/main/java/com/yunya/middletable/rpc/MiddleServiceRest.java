package com.yunya.middletable.rpc;

import com.yunya.framework.common.model.ResponseResult;
import com.yunya.middletable.service.credits_shop.CreditsShopBiz;
import com.yunya.models.report.CreditsShop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

/**
 * @author: chenlin
 * @date: 2022/11/10 9:55
 * @description:
 * @since: 1.0.0
 */
@Api("中间服务接口暴露")
@RestController
@RequestMapping("/api")
public class MiddleServiceRest {

    @Autowired
    private CreditsShopBiz creditsShopBiz;

    @ApiOperation("查询患者个人积分")
    @GetMapping("/credits/{patientId}")
    @ApiImplicitParam(name = "patientId", value = "患者ID", required = true,dataTypeClass = Integer.class)
    public ResponseResult<CreditsShop> lastPatientCredits(@NotNull(message = "患者ID不能为空") @PathVariable("patientId") Integer patientId) {
        return creditsShopBiz.lastPatientCredits(patientId);
    }
}
