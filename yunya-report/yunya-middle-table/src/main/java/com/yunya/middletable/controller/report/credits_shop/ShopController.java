package com.yunya.middletable.controller.report.credits_shop;

import com.yunya.feign.report.domain.query.PatientCreditsRecordQuery;
import com.yunya.feign.report.domain.vo.CreditsRecordVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.credits_shop.CreditsShopBiz;
import com.yunya.models.credits_shop.CreditsShop;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "openId", value = "用户唯一标识", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "patientId", value = "患者ID", required = true, dataTypeClass = Integer.class)
    })
    @ApiResponse(code = 0,message = "兑吧登录URL",response = String.class)
    public ResponseResult<Map<String,String>> duibaAutoLogin(@RequestParam String openId,
                                                             @RequestParam Integer patientId) throws UnsupportedEncodingException {
        return creditsShopBiz.duibaAutoLogin(openId,patientId);
    }

    @ApiOperation("查询患者积分记录")
    @GetMapping("/credits/record")
    public ResponseResult<List<CreditsRecordVO>> patientCreditsRecord(PatientCreditsRecordQuery query) {
        return creditsShopBiz.selectPatientCreditsRecord(query);
    }

    @ApiOperation("查询患者个人积分")
    @GetMapping("/credits/{patientId}")
    @ApiImplicitParam(name = "patientId", value = "患者ID", required = true,dataTypeClass = Integer.class)
    public ResponseResult<CreditsShop> lastPatientCredits(@NotNull(message = "患者ID不能为空") @PathVariable("patientId") Integer patientId) {
        return creditsShopBiz.lastPatientCredits(patientId);
    }

    @ApiOperation("初始化患者积分")
    @PostMapping("/initialization")
    public ResponseResult<T> initialization() throws InterruptedException {
        creditsShopBiz.initialization();
        return ResponseUtil.success();
    }

}
