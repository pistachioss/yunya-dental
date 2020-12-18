package com.yunya.modules.sms.controller;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.sms.model.SmsChargeOrderModel;
import com.yunya.feign.sms.query.SmsChargeOrderQueryForm;
import com.yunya.feign.sms.vo.SmsChargeOrderVO;
import com.yunya.framework.common.annation.CurrentUser;
import com.yunya.framework.common.annation.RepeatSubmit;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.sms.biz.SmsChargeOrderBiz;
import com.yunya.modules.sms.enums.SmsOrderStatusEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.DATA_ERROR;

/**
 * 简介：短信充值订单管理
 *
 * @author: chenlin
 * @Description: 短信充值订单管理
 * @Date: 2020/12/12 20:45
 * @since: 1.0.0
 */
@Api(tags = "短信充值订单管理")
@RestController
@RequestMapping("smsChargeOrder")
public class SmsChargeOrderController {

    @Autowired
    private SmsChargeOrderBiz smsChargeOrderBiz;

    /**
     * 分页查询短信充值列表
     *
     * @param smsChargeOrderQueryForm 查询参数
     * @return
     */
    @ApiOperation(value = "分页查询短信充值列表")
    @PostMapping("/list")
    public ResponseResult<PageInfo<SmsChargeOrderVO>> findSmsChargeOrderList(@RequestBody SmsChargeOrderQueryForm smsChargeOrderQueryForm) {
        smsChargeOrderQueryForm.setOrderStatus(SmsOrderStatusEnum.PAY_SUC.getCode());
        List<SmsChargeOrderVO> smsChargeOrderList = smsChargeOrderBiz.findSmsChargeOrderList(smsChargeOrderQueryForm);
        PageInfo<SmsChargeOrderVO> result = new PageInfo<>(smsChargeOrderList);
        return ResponseUtil.success(result);
    }

    /**
     * 创建短信充值订单，返回支付二维码链接
     *
     * @param smsChargeOrderModel 签名设置添加模型
     */
    @ApiOperation("创建短信充值订单，返回支付二维码链接")
    @PostMapping("/create")
    @CurrentUser
    @RepeatSubmit
    public ResponseResult<SmsChargeOrderVO> create(@RequestBody @Validated SmsChargeOrderModel smsChargeOrderModel) {
        SmsChargeOrderVO SmsChargeOrderVO = smsChargeOrderBiz.create(smsChargeOrderModel);
        return ResponseUtil.success(SmsChargeOrderVO);
    }

    /**
     * 刷新充值二维码
     *
     * @param id 主键id
     */
    @ApiOperation("刷新充值二维码")
    @PutMapping("/refreshQrCode/{id}")
    @CurrentUser
    @RepeatSubmit
    public ResponseResult<String> refreshQrCode(@PathVariable(value = "id") @NotNull Integer id) {
        String qrcodeUrl = smsChargeOrderBiz.refreshQrCode(id);
        return ResponseUtil.success(qrcodeUrl);
    }

    /**
     * 采商支付返回的异步通知处理
     *
     * @param request
     * @param response
     */
    @PostMapping("notifyUrl")
    public void notifyUrl(HttpServletRequest request, HttpServletResponse response) {
        smsChargeOrderBiz.notifyUrl(request);
        try (PrintWriter out = response.getWriter()) {
            out.println("success");
            out.flush();
        } catch (IOException e) {
            throw new ClientServiceException("notify response io error", DATA_ERROR);
        }
    }
}
