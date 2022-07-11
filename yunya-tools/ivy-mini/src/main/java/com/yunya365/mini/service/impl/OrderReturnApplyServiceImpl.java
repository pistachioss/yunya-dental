package com.yunya365.mini.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.github.binarywang.wxpay.bean.request.BaseWxPayRequest;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.yunya.feign.ivy_mini.domain.model.OrderRefundApplyModel;
import com.yunya.feign.ivy_mini.domain.model.OrderRefundModel;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya365.mini.config.WxMiniPayProperties;
import com.yunya365.mini.entity.OrderInfo;
import com.yunya365.mini.entity.OrderReturnApply;
import com.yunya365.mini.mapper.OrderReturnApplyMapper;
import com.yunya365.mini.service.IOrderReturnApplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

import static com.yunya.framework.common.enums.TrueFalseEnum.*;
import static com.yunya365.mini.enums.IvyMiniError.*;
import static com.yunya365.mini.enums.OrderRefundEnum.*;

/**
 * <p>
 * 订单退货申请 服务实现类
 * </p>
 *
 * @author xiangyang
 * @since 2022-07-04
 */
@Slf4j
@Service
public class OrderReturnApplyServiceImpl extends ServiceImpl<OrderReturnApplyMapper, OrderReturnApply> implements IOrderReturnApplyService {

    @Resource
    private WxPayService wxPayService;
    @Resource
    private WxMiniPayProperties properties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refundApply(OrderInfo orderInfo, OrderRefundApplyModel model) {
        OrderReturnApply returnApply = ChainWrappers.lambdaQueryChain(baseMapper)
                .eq(OrderReturnApply::getOrderId, orderInfo.getId()).orderByDesc(OrderReturnApply::getCrtTime)
                .last("limit 1").one();
        if (Objects.nonNull(returnApply)) {
            if (Objects.equals(REFUNDING.getCode(), returnApply.getHandleStatus())) {
                if (Objects.isNull(returnApply.getRefundStatus())) {
                    throw ClientServiceException.wrap(ORDER_REFUNDING);
                }
                if (Objects.equals(FALSE.getCode(), returnApply.getRefundStatus())) {
                    throw ClientServiceException.wrap(ORDER_REFUND_FINISH);
                }
            }
        }
        OrderReturnApply apply = new OrderReturnApply();
        apply.setOrderId(model.getOrderId());
        apply.setOrderSn(orderInfo.getOrderSn());
        apply.setReturnAmount(orderInfo.getPayAmount());
        apply.setReturnName(orderInfo.getReceiverName());
        apply.setReturnPhone(orderInfo.getReceiverPhone());
        apply.setHandleStatus(HANDLE_PENDING.getCode());
        apply.setDeliveryStatus(model.getStatus());
        apply.setReason(model.getRefundReason());
        apply.setPreStatus(orderInfo.getStatus().intValue());
        baseMapper.insert(apply);
    }

    @Override
    public OrderReturnApply queryRefund(Integer orderId) {
        return ChainWrappers.lambdaQueryChain(baseMapper)
                .eq(OrderReturnApply::getOrderId, orderId).orderByDesc(OrderReturnApply::getCrtTime)
                .last("limit 1").one();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refund(OrderInfo orderInfo, OrderRefundModel model) {
        try {
            OrderReturnApply apply = queryRefund(orderInfo.getId());
            if (Objects.isNull(apply)) {
                throw ClientServiceException.wrap(ORDER_ERROR);
            }
            if (!Objects.equals(HANDLE_PENDING.getCode(), apply.getHandleStatus())) {
                throw ClientServiceException.wrap(ORDER_REFUND_STATUS_ERROR);
            }
            if (Objects.equals(REFUNDING.getCode(), model.getStatus())) {
                WxPayRefundRequest refundRequest = assembleRefundModel(orderInfo, apply);
                WxPayRefundResult refund = wxPayService.refund(refundRequest);
                apply.setOutOrderNo(refund.getRefundId());
            }
            apply.setHandleStatus(model.getStatus());
            baseMapper.updateById(apply);
        } catch (WxPayException e) {
            log.error("微信退款失败！订单号：{},原因:{}", orderInfo.getOrderSn(), e.getMessage());
            throw ClientServiceException.wrap(CB_PAY_ERROR);
        }
    }

    private WxPayRefundRequest assembleRefundModel(OrderInfo orderInfo, OrderReturnApply apply) {
        WxPayRefundRequest refundRequest = new WxPayRefundRequest();
        refundRequest.setTransactionId(orderInfo.getOutOrderNo());
//        refundRequest.setOutTradeNo(orderInfo.getOrderSn());
        refundRequest.setOutRefundNo(orderInfo.getOrderSn());
        refundRequest.setTotalFee(BaseWxPayRequest.yuanToFen(orderInfo.getPayAmount().toPlainString()));
        refundRequest.setRefundFee(BaseWxPayRequest.yuanToFen(orderInfo.getPayAmount().toPlainString()));
        refundRequest.setRefundDesc(apply.getReason());
        refundRequest.setNotifyUrl(properties.getRefundNotifyUrl());
        return refundRequest;
    }
}
