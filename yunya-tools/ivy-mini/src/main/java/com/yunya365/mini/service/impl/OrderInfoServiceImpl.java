package com.yunya365.mini.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.github.binarywang.wxpay.bean.notify.*;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.*;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryResult;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.pagehelper.*;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.discount.domain.form.*;
import com.yunya.feign.discount.domain.query.CardSaleQuery;
import com.yunya.feign.discount.domain.vo.CardSalePageVo;
import com.yunya.feign.ivy_mini.domain.bo.FansAddressBO;
import com.yunya.feign.ivy_mini.domain.bo.OrderItemBO;
import com.yunya.feign.ivy_mini.domain.model.*;
import com.yunya.feign.ivy_mini.domain.query.ConfirmProductQuery;
import com.yunya.feign.ivy_mini.domain.query.MyOrderQuery;
import com.yunya.feign.ivy_mini.domain.vo.*;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.report.domain.model.MessageOrderModel;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya365.mini.config.WxMiniPayProperties;
import com.yunya365.mini.entity.*;
import com.yunya365.mini.enums.IvyMiniError;
import com.yunya365.mini.mapper.OrderInfoMapper;
import com.yunya365.mini.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.yunya.framework.common.constant.BusinessConstants.*;
import static com.yunya.framework.common.constant.RedisConstants.*;
import static com.yunya.framework.common.enums.TrueFalseEnum.*;
import static com.yunya365.mini.enums.IvyMiniError.*;
import static com.yunya365.mini.enums.OrderRefundEnum.*;
import static com.yunya365.mini.enums.OrderStatusEnum.*;
import static java.util.stream.Collectors.*;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author xiangyang
 * @since 2022-06-06
 */
@Service
@Slf4j
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements IOrderInfoService {

    @Resource
    private IProductService productService;
    @Resource
    private IOrderSettingService orderSettingService;
    @Resource
    private IOrderItemService orderItemService;
    @Resource
    private DistributionServiceImpl distributionService;
    @Resource
    private ICartItemService cartItemService;
    @Resource
    private WxPayService wxPayService;
    @Resource
    private IOrderReturnApplyService returnApplyService;
    @Resource
    private IWxPayInfoService wxPayInfoService;
    @Resource
    private IOrderVirtualService virtualService;
    @Resource
    private RemoteDiscountFeign discountFeign;
    @Resource
    private RemoteRabbitMqServiceFeign mqServiceFeign;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private WxMiniPayProperties properties;

    @Override
    public ConfirmOrderVO confirmProductOrder(ConfirmProductQuery query) {
        Integer productType = query.getProductType();
        ConfirmOrderVO vo = new ConfirmOrderVO();
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        List<OrderItemBO> orderItemBOS = productService.listProductOrderItem(Collections.singleton(query.getProductId()), productType);
        List<PayOrderItemVO> orderItemVOS = orderItemBOS.stream().map(t -> {
            PayOrderItemVO itemVO = BeanCopierUtils.generalCopyBean(t, PayOrderItemVO.class);
            itemVO.setProductQuantity(query.getQuantity());
            return itemVO;
        }).collect(toList());
        //商品类产品有自提和配送区分
        if (FALSE.getCode().equals(productType)) {
            vo.setDeliveryVO(confirmAddress(userId));
        }
        vo.setProductList(orderItemVOS);
        vo.setCalcAmountVO(calcOrderAmount(orderItemVOS));
        vo.setProductType(productType);
        if (FALSE.getCode().equals(productType)) {
            //判断购物车中商品是否都有库存
            checkStockStatus(orderItemBOS, orderItemVOS, query.getQuantity());
        }
        return vo;
    }

    @Override
    public ConfirmOrderVO confirmCartOrder(List<Integer> cartIds) {
        ConfirmOrderVO vo = new ConfirmOrderVO();
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        //查询购物车产品信息
        List<OrderItemBO> orderItemBOS = cartItemService.listProductByIds(cartIds);
        List<PayOrderItemVO> orderItemVOS = orderItemBOS.stream()
                .map(t -> BeanCopierUtils.generalCopyBean(t, PayOrderItemVO.class)
                ).collect(toList());
        //商品类产品有自提和配送区分
        vo.setDeliveryVO(confirmAddress(userId));
        vo.setProductList(orderItemVOS);
        vo.setCalcAmountVO(calcOrderAmount(orderItemVOS));
        //判断购物车中商品是否都有库存
        checkCartStockStatus(orderItemBOS, orderItemVOS);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreateOrderVO createProductOrder(CreateProductOrderModel model) {
        boolean locked = false;
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        Integer productId = model.getProductId();
        //购买数量
        Integer quantity = model.getQuantity();
        //商品类型
        Integer productType = model.getProductType();
        try {
            //加锁
            locked = lock(CREATE_ORDER_LOCK, productId, userId);
            //查询原始商品或虚拟服务
            List<OrderItemBO> itemBoList = productService.listProductOrderItem(Collections.singleton(productId), productType);
            if (CollectionUtils.isEmpty(itemBoList)) {
                throw ClientServiceException.wrap(PRODUCT_LACK);
            }
            itemBoList.forEach(t -> t.setProductQuantity(quantity));
            if (Objects.equals(FALSE.getCode(), productType)) {
                //判断购物车中商品是否都有库存
                if (!hasStock(itemBoList, quantity)) {
                    throw ClientServiceException.wrap(STOCK_LACK);
                }
            }
            //进行库存锁定
            lockStock(productId, quantity, null, productType);
            //创建订单
            OrderInfo orderInfo = assembleOrder(userId, model, itemBoList, model.getFansReceiveAddressId());
            //生成支付单
            WxPaymentVO wxPaymentVO = wxPay(orderInfo);
            baseMapper.insertDynamic(orderInfo);
            List<OrderItem> itemList = itemBoList.stream().map(t -> {
                OrderItem orderItem = BeanCopierUtils.generalCopyBean(t, OrderItem.class);
                orderItem.setOrderId(orderInfo.getId());
                orderItem.setOrderSn(orderInfo.getOrderSn());
                return orderItem;
            }).collect(toList());
            orderItemService.saveBatch(itemList);
            //保存预付单信息
            wxPayInfoService.save(wxPaymentVO);
            if (Objects.equals(TRUE.getCode(), productType)) {
                //虚拟服务售卖卡券
                soldCard(model, itemBoList, orderInfo);
            }
            CreateOrderVO vo = createVO(orderInfo, itemList, wxPaymentVO);
            //发送延迟消息取消订单
            sendOrderMessage(orderInfo.getId());
            return vo;
        } finally {
            if (locked) {
                log.info("【解锁成功】商品详情创建订单");
                unlock(CREATE_ORDER_LOCK, productId, userId);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreateOrderVO createCartOrder(CreateCartOrderModel model) {
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        List<OrderItemBO> orderItemBOS = cartItemService.listProductByIds(model.getCartIds());
        if (CollectionUtils.isEmpty(orderItemBOS)) {
            throw ClientServiceException.wrap(CART_DATA_ERROR);
        }
        //判断购物车中商品是否都有库存
        if (!hasCartStock(orderItemBOS)) {
            throw ClientServiceException.wrap(STOCK_LACK);
        }
        //进行库存锁定
        lockStock(null, null, orderItemBOS, model.getProductType());
        //创建订单
        OrderInfo orderInfo = assembleOrder(userId, model, orderItemBOS, model.getFansReceiveAddressId());
        //删除购物车中的下单商品
        cartItemService.delete(model.getCartIds());
        //生成支付单
        WxPaymentVO wxPaymentVO = wxPay(orderInfo);
        baseMapper.insertDynamic(orderInfo);
        List<OrderItem> itemList = orderItemBOS.stream().map(t -> {
            OrderItem orderItem = BeanCopierUtils.generalCopyBean(t, OrderItem.class);
            orderItem.setProductQuantity(t.getProductQuantity());
            orderItem.setOrderId(orderInfo.getId());
            orderItem.setOrderSn(orderInfo.getOrderSn());
            return orderItem;
        }).collect(toList());
        orderItemService.saveBatch(itemList);
        //保存预付单信息
        wxPayInfoService.save(wxPaymentVO);
        CreateOrderVO vo = createVO(orderInfo, itemList, wxPaymentVO);
        //发送延迟消息取消订单
        sendOrderMessage(orderInfo.getId());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String wxNotify(HttpServletRequest request, HttpServletResponse response) {
        try {
            String xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
            log.info("微信回调结果：{}", xmlResult);
            WxPayOrderNotifyResult result = wxPayService.parseOrderNotifyResult(xmlResult);
            if (!Objects.equals(WxPayConstants.ResultCode.SUCCESS, result.getReturnCode())) {
                return WxPayNotifyResponse.fail(result.getReturnMsg());
            }
            // 加入自己处理订单的业务逻辑，需要判断订单是否已经支付过，否则可能会重复调用
            String orderId = result.getOutTradeNo();
            String tradeNo = result.getTransactionId();
            //本次支付的订单
            OrderInfo orderInfo = ChainWrappers.lambdaQueryChain(baseMapper).eq(OrderInfo::getOrderSn, orderId).one();
            if (Objects.isNull(orderInfo)) {
                throw ClientServiceException.wrap(ORDER_DATA_ERROR);
            }
            //订单状态
            int status = orderInfo.getStatus().intValue();
            if (Objects.equals(PAY_PENDING.getCode(), status)) {
                //0->商品 1->虚拟服务
                int productType = orderInfo.getProductType().intValue();
                //0->自提 1->配送
                int deliveryType = orderInfo.getDeliveryType().intValue();
                //商品
                if (Objects.equals(FALSE.getCode(), productType)) {
                    //自提
                    if (Objects.equals(FALSE.getCode(), deliveryType)) {
                        orderInfo.setStatus(HAS_SHIP.getCode().byteValue());
                        //配送
                    } else {
                        orderInfo.setStatus(SHIP_PENDING.getCode().byteValue());
                    }
                    //虚拟服务
                } else {
                    orderInfo.setStatus(FINISH.getCode().byteValue());
                }
                Date payTime = Date.from(java.time.LocalDateTime.parse(result.getTimeEnd(), DateTimeFormatter.ofPattern("yyyyMMddHHmmss")).atZone(ZoneOffset.ofHours(8)).toInstant());
                orderInfo.setPaymentTime(payTime);
                orderInfo.setOutOrderNo(tradeNo);
                baseMapper.updateByPrimaryKeySelective(orderInfo);
            }
            return WxPayNotifyResponse.success("处理成功!");
        } catch (Exception e) {
            log.error("支付回调结果异常", e);
            return WxPayNotifyResponse.fail(e.getMessage());
        } finally {
            try {
                // 处理业务完毕
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.print("success");
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                log.error("支付回调响应异常", e);
            }
        }
    }

    @Override
    public List<WxPayOrderQueryResult> queryPayOrder(Collection<Integer> orderIds) {
        List<OrderInfo> list = ChainWrappers.lambdaQueryChain(baseMapper)
                .in(OrderInfo::getId, orderIds).eq(OrderInfo::getStatus, PAY_PENDING.getCode()).list();
        List<WxPayOrderQueryResult> queryVOS = Lists.newArrayListWithCapacity(orderIds.size());
        for (OrderInfo orderInfo : list) {
            WxPayOrderQueryResult queryResult;
            try {
                queryResult = wxPayService.queryOrder(null, orderInfo.getOrderSn());
                queryVOS.add(queryResult);
            } catch (WxPayException e) {
                log.error("微信支付查询失败！订单号：{},原因:{}", orderInfo.getOrderSn(), e.getMessage());
                throw ClientServiceException.wrap(CB_QUERY_ERROR);
            }
        }
        return queryVOS;
    }

    @Override
    public PageInfo<OrderFrontVO> orderList(MyOrderQuery query) {
        Page<OrderInfo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
        List<OrderInfo> list = ChainWrappers.lambdaQueryChain(baseMapper)
                .select(OrderInfo::getId, OrderInfo::getTotalAmount, OrderInfo::getPayAmount, OrderInfo::getStatus, OrderInfo::getCrtTime, OrderInfo::getProductType)
                .eq(Objects.nonNull(query.getStatus()), OrderInfo::getStatus, query.getStatus())
                .eq(OrderInfo::getDeleteStatus, FALSE.getCode()).orderByDesc(OrderInfo::getCrtTime).list();
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        Set<Integer> orderIds = list.stream().map(OrderInfo::getId).collect(toSet());
        List<OrderItem> orderItems = orderItemService.listByOrderIds(orderIds);
        List<OrderFrontVO> result = assembleFrontOrder(list, orderItems);
        PageInfo<OrderFrontVO> pageInfo = new PageInfo<>(result);
        pageInfo.setPageNum(page.getPageNum());
        pageInfo.setTotal(page.getTotal());
        return pageInfo;
    }

    @Override
    public WxOrderPayVO payQuery(Integer orderId) {
        List<WxPayOrderQueryResult> results = queryPayOrder(Collections.singleton(orderId));
        WxOrderPayVO vo = new WxOrderPayVO();
        if (CollectionUtils.isNotEmpty(results)) {
            WxPayOrderQueryResult result = results.get(0);
            //    SUCCESS--支付成功
            //    REFUND--转入退款
            //    NOTPAY--未支付
            //    CLOSED--已关闭
            //    REVOKED--已撤销(刷卡支付)
            //    USERPAYING--用户支付中
            //    PAYERROR--支付失败(其他原因，如银行返回失败)
            //    ACCEPT--已接收，等待扣款
            String tradeState = result.getTradeState();
            if (Objects.equals("SUCCESS", tradeState)) {
                vo.setPayStatus(0);
            }
            if (Objects.equals("NOTPAY", tradeState) || Objects.equals("USERPAYING", tradeState)
                    || Objects.equals("ACCEPT", tradeState)) {
                vo.setPayStatus(1);
            }
            if (Objects.equals("CLOSED", tradeState) || Objects.equals("REVOKED", tradeState)
                    || Objects.equals("PAYERROR", tradeState)) {
                vo.setPayStatus(2);
            }
            if (Objects.equals("REFUND", tradeState)) {
                vo.setPayStatus(3);
            }
        }
        return vo;
    }

    @Override
    public OrderDetailVO orderDetail(Integer orderId) {
        OrderDetailVO vo = new OrderDetailVO();
        OrderInfo orderInfo = getById(orderId);
        vo.setOrderVO(assembleOrderDetail(orderInfo));
        List<OrderItem> orderItems = orderItemService.listByOrderIds(Collections.singleton(orderId));
        if (CollectionUtils.isNotEmpty(orderItems)) {
            List<PayOrderItemVO> collect = orderItems.stream().map(t -> BeanCopierUtils.generalCopyBean(t, PayOrderItemVO.class)).collect(toList());
            vo.setItemVO(collect);
        }
        PayReceiveAddressVO addressVO = BeanCopierUtils.generalCopyBean(orderInfo, PayReceiveAddressVO.class);
        addressVO.setDetailAddress(orderInfo.getReceiverDetailAddress());
        addressVO.setDeliveryType(orderInfo.getDeliveryType());
        vo.setAddressVO(addressVO);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayOrderVO confirmDelivery(Integer orderId) {
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        boolean locked = false;
        try {
            //加锁
            locked = lock(CONFIRM_ORDER_LOCK, orderId, userId);
            OrderInfo orderInfo = baseMapper.selectByPrimaryKey(orderId);
            if (Objects.isNull(orderInfo)) {
                throw ClientServiceException.wrap(ORDER_ERROR);
            }
            if (!Objects.equals(userId, orderInfo.getFansId()) && BaseContextHandler.getAuthorization().startsWith("mini")) {
                throw ClientServiceException.wrap(ORDER_CONFIRM_ERROR);
            }
            if (!Objects.equals(HAS_SHIP.getCode(), orderInfo.getStatus().intValue())) {
                throw ClientServiceException.wrap(ORDER_CONFIRM_STATUS_ERROR);
            }
            orderInfo.setStatus(FINISH.getCode().byteValue());
            orderInfo.setConfirmStatus(TRUE.getCode().byteValue());
            Date date = new Date();
            orderInfo.setReceiveTime(date);
            orderInfo.setUpdTime(date);
            orderInfo.setUpdId(userId);
            baseMapper.updateByPrimaryKeySelective(orderInfo);
            PayOrderVO payOrderVO = BeanCopierUtils.generalCopyBean(orderInfo, PayOrderVO.class);
            payOrderVO.setOrderId(orderInfo.getId());
            payOrderVO.setOrderDate(orderInfo.getCrtTime());
            payOrderVO.setPayDate(orderInfo.getPaymentTime());
            return payOrderVO;
        } finally {
            if (locked) {
                log.info("【解锁成功】确认收货");
                unlock(CONFIRM_ORDER_LOCK, orderId, userId);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayOrderVO applyRefund(OrderRefundApplyModel model) {
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        boolean locked = false;
        try {
            //加锁
            locked = lock(REFUND_ORDER_LOCK, model.getOrderId(), userId);
            OrderInfo orderInfo = getById(model.getOrderId());
            if (Objects.isNull(orderInfo)) {
                throw ClientServiceException.wrap(ORDER_ERROR);
            }
            if (!Objects.equals(userId, orderInfo.getFansId())) {
                throw ClientServiceException.wrap(ORDER_REFUND_ERROR);
            }
            if (!Lists.newArrayList(SHIP_PENDING.getCode(), HAS_SHIP.getCode(), FINISH.getCode())
                    .contains(orderInfo.getStatus().intValue())) {
                throw ClientServiceException.wrap(ORDER_REFUND_STATUS_ERROR);
            }
            returnApplyService.refundApply(orderInfo, model);
            orderInfo.setStatus(APPLY_REFUND.getCode().byteValue());
            Date date = new Date();
            orderInfo.setUpdTime(date);
            orderInfo.setUpdId(userId);
            baseMapper.updateByPrimaryKeySelective(orderInfo);
            PayOrderVO payOrderVO = BeanCopierUtils.generalCopyBean(orderInfo, PayOrderVO.class);
            payOrderVO.setOrderId(orderInfo.getId());
            payOrderVO.setOrderDate(orderInfo.getCrtTime());
            payOrderVO.setPayDate(orderInfo.getPaymentTime());
            payOrderVO.setOrderStatus(model.getStatus());
            payOrderVO.setReturnReason(model.getRefundReason());
            return payOrderVO;
        } finally {
            if (locked) {
                log.info("【解锁成功】退款");
                unlock(REFUND_ORDER_LOCK, model.getOrderId(), userId);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Integer orderId) {
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        OrderInfo orderInfo = getById(orderId);
        checkOrder(userId, orderInfo, ORDER_CANCEL_ERROR, ORDER_CANCEL_STATUS_ERROR, PAY_PENDING.getCode());
        orderInfo.setStatus(CLOSE.getCode().byteValue());
        Date date = new Date();
        orderInfo.setUpdTime(date);
        orderInfo.setUpdId(userId);
        baseMapper.updateByPrimaryKeySelective(orderInfo);
        List<OrderItem> orderItems = orderItemService.listByOrderIds(Collections.singleton(orderId));
        //商店放回购物车或下订单页面
        addCart(orderInfo, orderItems);
        //释放库存
        freeStock(orderInfo, orderItems);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer orderId) {
        OrderInfo orderInfo = getById(orderId);
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        checkOrder(userId, orderInfo, ORDER_DELETE_ERROR, ORDER_DELETE_STATUS_ERROR, CLOSE.getCode());
        removeById(orderId);
        orderItemService.delete(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelRefund(Integer orderId) {
        OrderInfo orderInfo = getById(orderId);
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        checkOrder(userId, orderInfo, ORDER_CANCEL_REFUND_ERROR, ORDER_CANCEL_REFUND_STATUS_ERROR, APPLY_REFUND.getCode());
        OrderReturnApply apply = returnApplyService.queryRefund(orderId);
        if (!Objects.equals(HANDLE_PENDING.getCode(), apply.getHandleStatus())) {
            throw ClientServiceException.wrap(ORDER_REFUND_FINISH);
        }
        orderInfo.setStatus(apply.getPreStatus().byteValue());
        Date date = new Date();
        orderInfo.setUpdTime(date);
        orderInfo.setUpdId(userId);
        baseMapper.updateByPrimaryKeySelective(orderInfo);
        //删除订单退款申请
        returnApplyService.removeById(apply.getId());
    }

    @Override
    public WxPaymentVO continuePay(Integer orderId) {
        OrderInfo orderInfo = getById(orderId);
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        checkOrder(userId, orderInfo, ORDER_PAY_ERROR, ORDER_PAY_STATUS_ERROR, PAY_PENDING.getCode());
        return wxPayInfoService.getWxPay(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refund(OrderRefundModel model) {
        OrderInfo orderInfo;
        orderInfo = getById(model.getOrderId());
        if (Objects.isNull(orderInfo)) {
            throw ClientServiceException.wrap(ORDER_ERROR);
        }
        if (!Objects.equals(APPLY_REFUND.getCode(), orderInfo.getStatus().intValue())) {
            throw ClientServiceException.wrap(ORDER_REFUND_STATUS_ERROR);
        }
        returnApplyService.refund(orderInfo, model);
    }

    @Override
    public String wxRefundNotify(HttpServletRequest request, HttpServletResponse response) {
        try {
            String xmlResult = IOUtils.toString(request.getInputStream(), request.getCharacterEncoding());
            log.info("微信退款回调结果：{}", xmlResult);
            WxPayRefundNotifyResult result = wxPayService.parseRefundNotifyResult(xmlResult);
            if (!Objects.equals(WxPayConstants.ResultCode.SUCCESS, result.getReturnCode())) {
                return WxPayNotifyResponse.fail(result.getReturnMsg());
            }
            WxPayRefundNotifyResult.ReqInfo reqInfo = result.getReqInfo();
            // 微信订单号
            String tradeNo = reqInfo.getTransactionId();
            // 订单号
            String orderSn = reqInfo.getOutTradeNo();
            //退款状态 SUCCESS-退款成功  CHANGE-退款异常  REFUNDCLOSE—退款关闭
            String refundStatus = reqInfo.getRefundStatus();
            //退款成功时间
            String successTime = reqInfo.getSuccessTime();
            //本次支付的订单
            OrderInfo orderInfo = ChainWrappers.lambdaQueryChain(baseMapper).eq(OrderInfo::getOrderSn, orderSn).one();
            if (Objects.isNull(orderInfo)) {
                throw ClientServiceException.wrap(ORDER_DATA_ERROR);
            }
            OrderReturnApply apply = returnApplyService.queryRefund(orderInfo.getId());
            if (Objects.equals(refundStatus, "SUCCESS")) {
                java.time.LocalDateTime refundTime = java.time.LocalDateTime.parse(successTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                apply.setUpdTime(refundTime);
                apply.setRefundStatus(FALSE.getCode());
                orderInfo.setStatus(REFUND_SUCCESS.getCode().byteValue());
                orderInfo.setUpdTime(DateUtil.localDateTimeToDate(refundTime));
            } else {
                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                apply.setRefundStatus(TRUE.getCode());
                apply.setUpdTime(now);
                apply.setHandleNote("微信退款失败");
                orderInfo.setStatus(apply.getPreStatus().byteValue());
                orderInfo.setUpdTime(DateUtil.localDateTimeToDate(now));
            }
            returnApplyService.updateById(apply);
            baseMapper.updateByPrimaryKeySelective(orderInfo);
            return WxPayNotifyResponse.success("处理成功!");
        } catch (Exception e) {
            log.error("退款回调结果异常", e);
            return WxPayNotifyResponse.fail(e.getMessage());
        } finally {
            try {
                // 处理业务完毕
                ServletOutputStream outputStream = response.getOutputStream();
                outputStream.print("success");
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                log.error("退款回调响应异常", e);
            }
        }
    }

    @Override
    public void handleDelayPay(Integer orderId) {
        OrderInfo orderInfo = getById(orderId);
        if (Objects.isNull(orderInfo)) {
            throw ClientServiceException.wrap(ORDER_ERROR);
        }
        // 订单状态 0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->申请退款
        Integer status = orderInfo.getStatus().intValue();
        if (Objects.equals(PAY_PENDING.getCode(), status)) {
            orderInfo.setStatus(CLOSE.getCode().byteValue());
            orderInfo.setUpdTime(new Date());
            baseMapper.updateByPrimaryKeySelective(orderInfo);
        }
    }

    @Override
    public PayOrderVO refundDetail(Integer orderId) {
        OrderInfo orderInfo = getById(orderId);
        PayOrderVO vo = BeanCopierUtils.generalCopyBean(orderInfo, PayOrderVO.class);
        vo.setOrderId(orderInfo.getId());
        vo.setOrderDate(orderInfo.getCrtTime());
        vo.setPayDate(orderInfo.getPaymentTime());
        OrderReturnApply apply = returnApplyService.queryRefund(orderId);
        if (Objects.nonNull(apply)) {
            vo.setOrderStatus(apply.getDeliveryStatus());
            vo.setReturnReason(apply.getReason());
            if (Objects.equals(REFUND_REFUSE.getCode(), apply.getHandleStatus())
                    || Objects.equals(TRUE.getCode(), apply.getRefundStatus())) {
                vo.setMchReply(apply.getHandleNote());
            }
        }
        return vo;
    }

    @Override
    public OrderVirtualDetailVO virtualOrderDetail(Integer orderId) {
        OrderVirtualDetailVO vo = new OrderVirtualDetailVO();
        OrderInfo orderInfo = getById(orderId);
        vo.setOrderVO(assembleOrderDetail(orderInfo));

        List<OrderItem> orderItems = orderItemService.listByOrderIds(Collections.singleton(orderId));
        if (CollectionUtils.isNotEmpty(orderItems)) {
            List<PayOrderItemVO> collect = orderItems.stream().map(t -> BeanCopierUtils.generalCopyBean(t, PayOrderItemVO.class)).collect(toList());
            vo.setItemVO(collect);
        }
//        vo.setAddressVO(addressVO);
        return vo;
    }

    private PayOrderVO assembleOrderDetail(OrderInfo orderInfo) {
        PayOrderVO payOrderVO = BeanCopierUtils.generalCopyBean(orderInfo, PayOrderVO.class);
        payOrderVO.setOrderId(orderInfo.getId());
        payOrderVO.setOrderDate(orderInfo.getCrtTime());
        payOrderVO.setPayDate(orderInfo.getPaymentTime());
        payOrderVO.setProductType(orderInfo.getProductType());
        if (Objects.equals(PAY_PENDING.getCode(),orderInfo.getStatus().intValue())) {
            long seconds = Duration.between(java.time.LocalDateTime.now(), DateUtil.dateToLocalDateTime(orderInfo.getCrtTime()).plusMinutes(15)).getSeconds();
            if (seconds > 0) {
                String positive = String.format("%02d:%02d", (seconds % 3600) / 60, seconds % 60);
                payOrderVO.setRemainDate(positive);
            }
        }
        OrderReturnApply apply = returnApplyService.queryRefund(orderInfo.getId());
        if (Objects.nonNull(apply)) {
            payOrderVO.setOrderStatus(apply.getDeliveryStatus());
            payOrderVO.setReturnReason(apply.getReason());
            if (Objects.equals(REFUND_REFUSE.getCode(), apply.getHandleStatus())
                    || Objects.equals(TRUE.getCode(), apply.getRefundStatus())) {
                payOrderVO.setMchReply(apply.getHandleNote());
            }
        }
        if (Objects.equals(CLOSE.getCode(),orderInfo.getStatus().intValue())) {
            payOrderVO.setCloseDate(orderInfo.getUpdTime());
        }
        return payOrderVO;
    }

    private void checkOrder(Integer userId, OrderInfo orderInfo, IvyMiniError orderCancelError, IvyMiniError orderCancelStatusError,
                            Integer code) {
        if (Objects.isNull(orderInfo)) {
            throw ClientServiceException.wrap(ORDER_ERROR);
        }
        if (!Objects.equals(userId, orderInfo.getFansId())) {
            throw ClientServiceException.wrap(orderCancelError);
        }
        if (!Objects.equals(code, orderInfo.getStatus().intValue())) {
            throw ClientServiceException.wrap(orderCancelStatusError);
        }
    }

    private boolean lock(String keyPrefix, Object key, Object val) {
        String lockKey = Joiner.on(":").join(keyPrefix, key);
        String lockVal = String.valueOf(val);
        //加锁
        return redisUtils.setLock(lockKey, lockVal, MEDICAL_APPLY_LOCK_SEC, TimeUnit.SECONDS);
    }

    private void unlock(String keyPrefix, Object key, Object val) {
        String lockKey = Joiner.on(":").join(keyPrefix, key);
        String lockVal = String.valueOf(val);
        redisUtils.unlock(lockKey, lockVal);
    }

    private void addCart(OrderInfo orderInfo, List<OrderItem> orderItems) {
        if (FALSE.equals(orderInfo.getProductType().intValue())) {
            orderItems.forEach(t -> {
                AddCartModel addCartModel = new AddCartModel();
                addCartModel.setProductId(t.getProductId());
                addCartModel.setProductName(t.getProductName());
                addCartModel.setQuantity(t.getProductQuantity());
                addCartModel.setProductPic(t.getProductPic());
                addCartModel.setProductPrice(t.getProductPrice());
                addCartModel.setProductCategoryId(t.getProductCategoryId());
                cartItemService.add(addCartModel);
            });
        }
    }

    private void freeStock(OrderInfo orderInfo, List<OrderItem> orderItems) {
        List<FreeStockForm> forms = orderItems.stream().map(t -> {
            FreeStockForm form = new FreeStockForm();
            form.setProductId(t.getProductId());
            form.setQuantity(t.getProductQuantity());
            return form;
        }).collect(toList());
        productService.freeStock(forms, orderInfo.getProductType().intValue());
    }

    private List<OrderFrontVO> assembleFrontOrder(List<OrderInfo> list, List<OrderItem> orderItems) {
        Map<Integer, List<OrderItem>> orderItemMap = orderItems.stream().collect(groupingBy(OrderItem::getOrderId, toList()));
        return list.stream().filter(t -> orderItemMap.containsKey(t.getId())).map(t -> {
            List<OrderItem> itemList = orderItemMap.get(t.getId());
            //封面图片
            List<String> picList = itemList.stream().map(OrderItem::getProductPic)
                    .filter(StringUtils::isNotBlank).limit(3).collect(toList());
            Integer totalQuantity = itemList.stream().map(OrderItem::getProductQuantity).reduce(0, Integer::sum);
            OrderItem orderItem = Objects.equals(CollectionUtils.size(itemList), 1) ? itemList.get(0) : null;
            OrderFrontVO vo = new OrderFrontVO();
            vo.setOrderId(t.getId());
            vo.setOrderStatus(t.getStatus().intValue());
            vo.setProductPic(picList);
            vo.setOrderDate(DateUtil.format(t.getCrtTime(), "yyyyMMddHHmm"));
            vo.setPayAmount(t.getPayAmount());
            vo.setTotalAmount(t.getTotalAmount());
            vo.setTotalQuantity(totalQuantity);
            vo.setProductPieces(itemList.size());
            vo.setProductPrice(Objects.isNull(orderItem) ? null : orderItem.getProductPrice());
            vo.setProductName(Objects.isNull(orderItem) ? null : orderItem.getProductName());
            vo.setProductType(t.getProductType());
            return vo;
        }).collect(toList());
    }

    private WxPaymentVO wxPay(OrderInfo orderInfo) {
        if (Objects.isNull(orderInfo)) {
            throw ClientServiceException.wrap(ORDER_ERROR);
        }
        //微信支付
        WxPayUnifiedOrderRequest miniPayRequest = assemblePayModel(orderInfo);
        try {
            WxPayMpOrderResult result = wxPayService.createOrder(miniPayRequest);
            return BeanCopierUtils.generalCopyBean(result, WxPaymentVO.class);
        } catch (WxPayException e) {
            log.error("微信支付失败！订单号：{},原因:{}", orderInfo.getOrderSn(), e.getMessage());
            throw ClientServiceException.wrap(CB_PAY_ERROR);
        }
    }


    private WxPayUnifiedOrderRequest assemblePayModel(OrderInfo orderInfo) {
        String openId = BaseContextHandler.getOpenId();
        LocalDateTime now = LocalDateTime.fromDateFields(orderInfo.getCrtTime());
        WxPayUnifiedOrderRequest request = new WxPayUnifiedOrderRequest();
        request.setBody("艾维商城");
        request.setOutTradeNo(orderInfo.getOrderSn());
        request.setTotalFee(BaseWxPayRequest.yuanToFen(orderInfo.getPayAmount().toPlainString()));
        request.setOpenid(openId);
        request.setSpbillCreateIp("127.0.0.1");
        request.setTimeStart(now.toString("yyyyMMddHHmmss"));
        request.setTimeExpire(now.plusMinutes(15).toString("yyyyMMddHHmmss"));
        return request;
    }

    private WxPayRefundRequest assembleRefundModel(OrderInfo orderInfo, OrderReturnApply apply) {
        String openId = BaseContextHandler.getOpenId();
        LocalDateTime now = LocalDateTime.fromDateFields(orderInfo.getCrtTime());
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

    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now.toString("yyyyMMddHHmmss"));
        String yyyyMMddHHmmss = now.plusMinutes(30).toString("yyyyMMddHHmmss");
        System.out.println(yyyyMMddHHmmss);
    }

    /**
     * 判断下单商品是否都有库存
     */
    private boolean hasStock(List<OrderItemBO> list, Integer quantity) {
        for (OrderItemBO orderItemBO : list) {
            if (orderItemBO.getStock() - quantity < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断购物车商品是否都有库存
     */
    private boolean hasCartStock(List<OrderItemBO> list) {
        for (OrderItemBO orderItemBO : list) {
            if (orderItemBO.getStock() - orderItemBO.getProductQuantity() < 0) {
                return false;
            }
        }
        return true;
    }

    private void checkStockStatus(List<OrderItemBO> list, List<PayOrderItemVO> orderItemVOS, Integer quantity) {
        Map<Integer, Integer> map = list.stream().collect(toMap(OrderItemBO::getProductId, OrderItemBO::getStock));
        orderItemVOS.stream()
                .filter(t -> map.containsKey(t.getProductId()) && map.get(t.getProductId()) - quantity >= 0)
                .forEach(t -> t.setStock(true));
    }

    private void checkCartStockStatus(List<OrderItemBO> list, List<PayOrderItemVO> orderItemVOS) {
        Map<Integer, OrderItemBO> map = list.stream().collect(toMap(OrderItemBO::getProductId, Function.identity()));
        Predicate<OrderItemBO> predicate = (t) -> t.getStock() - t.getProductQuantity() >= 0;
        orderItemVOS.stream()
                .filter(t -> map.containsKey(t.getProductId()) && predicate.test(map.get(t.getProductId())))
                .forEach(t -> t.setStock(true));
    }

    /**
     * 计算总金额
     */
    private BigDecimal calcTotalAmount(List<OrderItemBO> boList) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemBO bo : boList) {
            totalAmount = totalAmount.add(bo.getProductPrice().multiply(new BigDecimal(bo.getProductQuantity())));
        }
        return totalAmount;
    }

    /**
     * 生成18位订单编号:8位日期+2位平台号码+2位支付方式+6位以上自增id
     */
    private String generateOrderSn(OrderInfo order) {
        StringBuilder sb = new StringBuilder();
        String date = DateUtil.format(LocalDate.now(), "yyyyMMdd");
        String key = Joiner.on(":").join(ORDER_ID_GENERATE, date);
        Long increment = redisUtils.incr(key, 1);
        sb.append(date);
        sb.append(String.format("%02d", order.getSourceType()));
        sb.append(String.format("%02d", order.getPayType()));
        String incrementStr = increment.toString();
        if (incrementStr.length() <= 6) {
            sb.append(String.format("%06d", increment));
        } else {
            sb.append(incrementStr);
        }
        return sb.toString();
    }

    private CreateOrderVO createVO(OrderInfo orderInfo, List<OrderItem> itemList, WxPaymentVO wxPaymentVO) {
        CreateOrderVO vo = new CreateOrderVO();
        PayOrderVO payOrderVO = BeanCopierUtils.generalCopyBean(orderInfo, PayOrderVO.class);
        payOrderVO.setOrderId(orderInfo.getId());
        payOrderVO.setOrderDate(orderInfo.getCrtTime());
        payOrderVO.setPayDate(orderInfo.getPaymentTime());
        payOrderVO.setRemainDate("15:00");
        vo.setOrderVO(payOrderVO);
        List<PayOrderItemVO> collect = itemList.stream().map(t -> BeanCopierUtils.generalCopyBean(t, PayOrderItemVO.class)).collect(toList());
        vo.setItemVO(collect);
        PayReceiveAddressVO addressVO = BeanCopierUtils.generalCopyBean(orderInfo, PayReceiveAddressVO.class);
        addressVO.setDetailAddress(orderInfo.getReceiverDetailAddress());
        addressVO.setDeliveryType(orderInfo.getDeliveryType());
        vo.setAddressVO(addressVO);
        vo.setPaymentVO(wxPaymentVO);
        return vo;
    }

    private CalcAmountVO calcOrderAmount(List<PayOrderItemVO> orderItemVOS) {
        CalcAmountVO calcAmountVO = new CalcAmountVO();
        Distribution distribution = distributionService.findList();
        //运费
        BigDecimal freightAmount = Objects.nonNull(distribution) ? distribution.getSendingPrice() : BigDecimal.ZERO;
        BigDecimal startSendingPrice = Objects.nonNull(distribution) ? distribution.getStartSendingPrice() : BigDecimal.ZERO;
        //总价
        BigDecimal totalAmount = orderItemVOS.stream()
                .map(t -> t.getProductPrice().multiply(BigDecimal.valueOf(t.getProductQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        calcAmountVO.setFreightAmount(freightAmount);
        calcAmountVO.setTotalAmount(totalAmount);
        calcAmountVO.setPayAmount(totalAmount);
        calcAmountVO.setStartSendingPrice(startSendingPrice);
        return calcAmountVO;
    }

    private DeliveryOrderVO confirmAddress(Integer userId) {
        DeliveryOrderVO deliveryOrderVO = new DeliveryOrderVO();
        FansAddressBO pickUp = productService.getAddress(userId, null, FALSE.getCode());
        FansAddressBO address = productService.getAddress(userId, null, TRUE.getCode());
        //自提
        PayReceiveAddressVO pickVO = BeanCopierUtils.generalCopyBean(pickUp, PayReceiveAddressVO.class);
        pickVO.setReceiverName(pickUp.getName());
        pickVO.setReceiverPhone(pickUp.getPhoneNumber());
        deliveryOrderVO.setPickUp(pickVO);
        //配送
        PayReceiveAddressVO addressVO = BeanCopierUtils.generalCopyBean(address, PayReceiveAddressVO.class);
        addressVO.setReceiverName(address.getName());
        addressVO.setReceiverPhone(address.getPhoneNumber());
        deliveryOrderVO.setDelivery(addressVO);

        return deliveryOrderVO;
    }

    private OrderInfo assembleOrder(Integer userId, CreateOrderBaseModel model, List<OrderItemBO> itemBoList, Integer fansReceiveAddressId) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setFansId(userId);
        orderInfo.setPayType(model.getPayType().byteValue());
        orderInfo.setTotalAmount(calcTotalAmount(itemBoList));
        orderInfo.setPayAmount(calcTotalAmount(itemBoList));
        orderInfo.setSourceType((byte) 1);
        //订单状态（0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->申请退款）
        orderInfo.setStatus((byte) 0);
        orderInfo.setOrderType((byte) 0);
        //商品类型 0-商品 1-虚拟服务
        orderInfo.setProductType(model.getProductType().byteValue());
        //配送方式：0->自提 1->配送
        orderInfo.setDeliveryType(model.getDeliveryType().byteValue());
        orderInfo.setRemark(model.getRemark());
        FansAddressBO address;
        //商品类产品有自提和配送区分
        if (FALSE.getCode().equals(model.getProductType())) {
            //收货人信息：姓名、电话、邮编、地址
            address = productService.getAddress(null, fansReceiveAddressId, model.getDeliveryType());
            orderInfo.setReceiverName(address.getName());
            orderInfo.setReceiverPhone(address.getPhoneNumber());
            orderInfo.setReceiverPostCode(address.getPostCode());
            orderInfo.setReceiverProvince(address.getProvince());
            orderInfo.setReceiverCity(address.getCity());
            orderInfo.setReceiverRegion(address.getRegion());
            orderInfo.setReceiverDetailAddress(address.getDetailAddress());
            if (FALSE.equals(model.getDeliveryType())) {
                orderInfo.setReceiverDetailAddress(model.getLocationAddress());
            }
        }
        //0->未确认；1->已确认
        orderInfo.setConfirmStatus((byte) 0);
        orderInfo.setDeleteStatus((byte) 0);
        //生成订单号
        orderInfo.setOrderSn(generateOrderSn(orderInfo));
        orderInfo.setCrtTime(new Date());
        return orderInfo;
    }

    private void lockStock(Integer productId, Integer quantity, List<OrderItemBO> orderItemBOS, Integer productType) {
        List<LockStockForm> list = Lists.newArrayList();
        if (Objects.nonNull(productId) && Objects.nonNull(quantity)) {
            LockStockForm form = new LockStockForm();
            form.setProductId(productId);
            form.setQuantity(quantity);
            list.add(form);
        }
        if (CollectionUtils.isNotEmpty(orderItemBOS)) {
            List<LockStockForm> lockStockForms = orderItemBOS.stream().map(t -> {
                LockStockForm form;
                form = new LockStockForm();
                form.setProductId(t.getProductId());
                form.setQuantity(t.getProductQuantity());
                return form;
            }).collect(toList());
            list.addAll(lockStockForms);
        }
        productService.lockProductStock(list, productType);
    }

    void soldCard(CreateProductOrderModel model, List<OrderItemBO> itemBoList, OrderInfo orderInfo){
        String username = BaseContextHandler.getName();
        String openId = BaseContextHandler.getOpenId();
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        CardSaleQuery query = new CardSaleQuery();
        query.setCouponId(model.getProductId());
        query.setOrgId(21);
        query.setCardStatsList(Collections.singletonList(0));
        query.setPageNum(1);
        query.setPageSize(model.getQuantity());
        PageInfo<CardSalePageVo> data = discountFeign.getCardSalePageVo(query).getData();
        if (!Objects.equals(data.getSize(), model.getQuantity())) {
            throw ClientServiceException.wrap(CARD_SOLD_LACK);
        }
        List<Integer> cardIds = data.getList().stream().map(CardSalePageVo::getId).collect(toList());
        OrderItemBO orderItemBO = itemBoList.get(0);
        CardSoldForm form = new CardSoldForm();
        form.setCardIds(cardIds);
        form.setSoldTarget(username);
        form.setSoldPhoneNumber(openId);
        form.setSoldType(0);
        form.setSendText(0);
        form.setSoldAndPay(1);
        form.setPayId(1);
        form.setRemark("小程序虚拟服务售卖");
        form.setSoldWay(0);
        form.setCouponType(orderItemBO.getCouponType());
        form.setCouponName(orderItemBO.getProductName());
        discountFeign.soldCard(form);
        OrderVirtual virtual = new OrderVirtual();
        virtual.setOrderId(orderInfo.getId());
        virtual.setFansId(orderInfo.getFansId());
        virtual.setCardId(Joiner.on(",").join(cardIds));
        virtual.setSoldMobile(openId);
        virtual.setSoldDate(java.time.LocalDateTime.now());
        virtual.setCrtId(userId);
        virtual.setUpdId(userId);
        virtualService.save(virtual);
    }

    private void sendOrderMessage(Integer orderId) {
        MessageOrderModel messageModel = new MessageOrderModel();
        Map<String, Object> map = Maps.newHashMap();
        map.put("order_id", orderId);
        messageModel.setParamMap(map);
        mqServiceFeign.sendOrderDirectMessage(messageModel);
    }
}
