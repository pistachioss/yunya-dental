package com.yunya.feign.middle;

import com.yunya.feign.middle.factory.RemoteMiddleServiceFactory;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.models.discount.CouponChangeRecord;
import com.yunya.models.report.CreditsShop;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.constraints.NotNull;

@FeignClient(name = YunyaServiceNameConstants.YUNYA_REPORT_MIDDLE,
        fallbackFactory = RemoteMiddleServiceFactory.class)
public interface RemoteMiddleServiceFeign {

    @ApiOperation("查询患者个人积分")
    @GetMapping("/api/credits/{patientId}")
    @ApiImplicitParam(name = "patientId", value = "患者ID", required = true,dataTypeClass = Integer.class)
    ResponseResult<CreditsShop> lastPatientCredits(@NotNull(message = "患者ID不能为空") @PathVariable("patientId") Integer patientId);

    @PostMapping("/deduction/occur")
    ResponseResult<Boolean> occur(@RequestBody CouponChangeRecord newBean);
}
