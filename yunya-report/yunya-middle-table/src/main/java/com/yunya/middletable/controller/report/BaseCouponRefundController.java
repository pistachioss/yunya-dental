package com.yunya.middletable.controller.report;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.middletable.service.BaseCouponRefundBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2020/10/29 20:20
 * @description:
 * @since: 1.0.0
 */
@Api(tags = "couponrefund")
@RestController
@RequestMapping("couponrefund")
public class BaseCouponRefundController {

    @Resource
    private BaseCouponRefundBiz baseCouponRefundBiz;


    /**
     * 根据消息更新中间表退费信息
     *
     * @param msg 消息
     * @return
     */
    @ApiOperation("单个卡券退费")
    @PostMapping(value = "/operate", name = "根据消息更新中间表退费信息")
    public ResponseResult<T> operateRefund(@RequestBody @Validated MessageModel msg) {
        baseCouponRefundBiz.operateRefund(msg);
        return ResponseUtil.success(null);
    }

    /**
     * 根据时间段批量操作中间表退费记录
     *
     * @param form 拉取时间
     * @return
     */
    @ApiOperation("批量卡券退费")
    @PostMapping(value = "/operate/batch", name = "form")
    public ResponseResult<T> pullRefundData(@RequestBody PullForm form) {
        baseCouponRefundBiz.pullRefundData(form);
        return ResponseUtil.success(null);
    }


}
