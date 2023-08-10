package com.yunya.middletable.controller.report;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.BaseCouponBillPayBiz;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("couponpay")
public class BaseCouponBillPayController {

    /**
     * 账单收费记录
     */
    @Resource
    private BaseCouponBillPayBiz baseCouponBillPayBiz;

    /**
     * 根据消息操作中间表账单收费记录
     *
     * @param msg 消息
     * @return
     */
    @ApiOperation("单个卡券收费")
    @PostMapping(value = "/operate", name = "根据消息操作中间表账单收费记录")
    public ResponseResult<T> operateBillPay(@RequestBody @Validated MessageModel msg) {
        baseCouponBillPayBiz.operateBillPay(msg);
        return ResponseUtil.success(null);
    }

    /**
     * 根据时间段批量操作中间表账单收费记录
     *
     * @param form 拉取时间
     * @return
     */
    @ApiOperation("批量卡券收费")
    @PostMapping(value = "/operate/batch", name = "根据时间段批量操作中间表账单收费记录")
    public ResponseResult<T> pullBillPayData(@RequestBody PullForm form) throws InterruptedException {
        baseCouponBillPayBiz.pullBillPayData(form);
        return ResponseUtil.success(null);
    }


}
