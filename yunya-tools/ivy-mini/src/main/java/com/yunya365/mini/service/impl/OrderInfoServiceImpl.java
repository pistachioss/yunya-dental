package com.yunya365.mini.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.discount.domain.form.LockStockForm;
import com.yunya.feign.ivy_mini.domain.bo.FansAddressBO;
import com.yunya.feign.ivy_mini.domain.bo.OrderItemBO;
import com.yunya.feign.ivy_mini.domain.model.*;
import com.yunya.feign.ivy_mini.domain.query.ConfirmProductQuery;
import com.yunya.feign.ivy_mini.domain.vo.*;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.*;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya365.mini.config.CaiBaoMiniProperties;
import com.yunya365.mini.config.WxMiniProperties;
import com.yunya365.mini.entity.*;
import com.yunya365.mini.mapper.OrderInfoMapper;
import com.yunya365.mini.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.yunya.framework.common.constant.BusinessConstants.*;
import static com.yunya.framework.common.constant.RedisConstants.*;
import static com.yunya.framework.common.enums.TrueFalseEnum.*;
import static com.yunya365.mini.enums.IvyMiniError.*;
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
    private RemoteTreatmentServiceFeign treatmentServiceFeign;
    @Resource
    private RemoteDiscountFeign discountFeign;
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
    private CaiBaoApi caiBaoApi;
    @Resource
    private CaiBaoMiniProperties caiBaoMiniProperties;
    @Resource
    private WxMiniProperties wxMiniProperties;
    @Resource
    private RedisUtils redisUtils;

    @Override
    public ConfirmOrderVO confirmProductOrder(ConfirmProductQuery query) {
        ConfirmOrderVO vo = new ConfirmOrderVO();
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        List<OrderItemBO> orderItemBOS = productService.listProductOrderItem(Collections.singleton(query.getProductId()), query.getProductType());
        //判断购物车中商品是否都有库存
        if (hasStock(orderItemBOS, query.getQuantity())) {
            throw ClientServiceException.wrap(STOCK_LACK);
        }
        List<PayOrderItemVO> orderItemVOS = orderItemBOS.stream().map(t -> {
            PayOrderItemVO itemVO = BeanCopierUtils.generalCopyBean(t, PayOrderItemVO.class);
            itemVO.setProductQuantity(query.getQuantity());
            return itemVO;
        }).collect(toList());
        //商品类产品有自提和配送区分
        if (FALSE.getCode().equals(query.getProductType())) {
            vo.setDeliveryVO(confirmAddress(userId));
        }
        vo.setProductList(orderItemVOS);
        vo.setCalcAmountVO(calcOrderAmount(orderItemVOS));
        return vo;
    }

    @Override
    public ConfirmOrderVO confirmCartOrder(List<Integer> cartIds) {
        ConfirmOrderVO vo = new ConfirmOrderVO();
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        //查询购物车产品信息
        List<OrderItemBO> orderItemBOS = cartItemService.listProductByIds(cartIds);
        //判断购物车中商品是否都有库存
        if (hasCartStock(orderItemBOS)) {
            throw ClientServiceException.wrap(STOCK_LACK);
        }
        List<PayOrderItemVO> orderItemVOS = orderItemBOS.stream()
                .map(t -> BeanCopierUtils.generalCopyBean(t, PayOrderItemVO.class)
                ).collect(toList());
        //商品类产品有自提和配送区分
        vo.setDeliveryVO(confirmAddress(userId));
        vo.setProductList(orderItemVOS);
        vo.setCalcAmountVO(calcOrderAmount(orderItemVOS));
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
        String lockKey = Joiner.on(":").join(RedisConstants.CREATE_ORDER_LOCK, productId);
        String lockVal = String.valueOf(userId);
        try {
            //加锁
            locked = redisUtils.setLock(lockKey, lockVal, MEDICAL_APPLY_LOCK_SEC, TimeUnit.SECONDS);
            //查询原始商品或虚拟服务
            List<OrderItemBO> itemBoList = productService.listProductOrderItem(Collections.singleton(productId), productType);
            itemBoList.forEach(t -> t.setProductQuantity(quantity));
            //判断购物车中商品是否都有库存
            if (hasStock(itemBoList, quantity)) {
                throw ClientServiceException.wrap(STOCK_LACK);
            }
            //进行库存锁定
            lockStock(productId, quantity, null, productType);
            //创建订单
            OrderInfo orderInfo = assembleOrder(userId, model, itemBoList);
            //生成支付单
            WxPaymentVO wxPaymentVO = wxPay(orderInfo, itemBoList);
            baseMapper.insert(orderInfo);
            List<OrderItem> itemList = itemBoList.stream().map(t -> {
                OrderItem orderItem = BeanCopierUtils.generalCopyBean(t, OrderItem.class);
                orderItem.setOrderId(orderInfo.getId());
                orderItem.setOrderSn(orderInfo.getOrderSn());
                return orderItem;
            }).collect(toList());
            orderItemService.saveBatch(itemList);
            //todo 发送延迟消息取消订单
            return createVO(orderInfo, itemList, wxPaymentVO);
        } finally {
            if (locked) {
                log.info("【解锁成功】");
                redisUtils.unlock(lockKey, lockVal);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreateOrderVO createCartOrder(CreateCartOrderModel model) {
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        List<OrderItemBO> orderItemBOS = cartItemService.listProductByIds(model.getCartIds());
        //判断购物车中商品是否都有库存
        if (hasCartStock(orderItemBOS)) {
            throw ClientServiceException.wrap(STOCK_LACK);
        }
        //进行库存锁定
        lockStock(null, null, orderItemBOS, model.getProductType());
        //创建订单
        OrderInfo orderInfo = assembleOrder(userId, model, orderItemBOS);
        //删除购物车中的下单商品
        cartItemService.delete(model.getCartIds());
        //生成支付单
        WxPaymentVO wxPaymentVO = wxPay(orderInfo, orderItemBOS);
        baseMapper.insert(orderInfo);
        List<OrderItem> itemList = orderItemBOS.stream().map(t -> {
            OrderItem orderItem = BeanCopierUtils.generalCopyBean(t, OrderItem.class);
            orderItem.setProductQuantity(t.getProductQuantity());
            orderItem.setOrderId(orderInfo.getId());
            orderItem.setOrderSn(orderInfo.getOrderSn());
            return orderItem;
        }).collect(toList());
        orderItemService.saveBatch(itemList);
        //todo 发送延迟消息取消订单
        return createVO(orderInfo, itemList, wxPaymentVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cbNotify(HttpServletRequest request, HttpServletResponse response) {
        try {
            String result = parseNotify(request);
            //回调结果验签
            Map<String, Object> data = JSONObject.parseObject(result, Map.class);
            String sign = Objects.toString(data.remove("sign"));
            if (!caiBaoApi.verifySign(data, sign)) {
                throw ClientServiceException.wrap(CB_NOTIFY_ERROR);
            }
            String localOrderNo = Objects.toString(data.get("appOrderNo"));
            //本次支付的订单
            OrderInfo orderInfo = ChainWrappers.lambdaQueryChain(baseMapper).eq(OrderInfo::getOrderSn, localOrderNo).one();
            if (Objects.isNull(orderInfo)) {
                throw ClientServiceException.wrap(ORDER_DATA_ERROR);
            }
            //订单状态
            int status = orderInfo.getStatus().intValue();
            //0->商品 1->虚拟服务
            int productType = orderInfo.getProductType().intValue();
            //0->自提 1->配送
            int deliveryType = orderInfo.getDeliveryType().intValue();
            if (Objects.equals(PAY_PENDING.getCode(), status)) {
//                WAIT_PAY	等待付款
//                PAY_SUC	付款成功
//                PART_REFUND	部分退款成功
//                ALL_REFUND	退款成功
//                REVERSED	订单撤销成功
//                CLOSED	订单撤销成功
//                CANCEL	已取消 (历史状态，已废弃，新接入用户不用考虑)
//                PAY_FAIL	付款失败
                String orderStatus = Objects.toString(data.get("orderStatus"));
                if (Objects.equals("PAY_SUC", orderStatus)) {
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
                    orderInfo.setPaymentTime(new Date(Long.parseLong(Objects.toString(data.get("cbOrderNo")))));
                }
                orderInfo.setCbOrderNo(Objects.toString(data.get("cbOrderNo")));
                orderInfo.setCbOrderNo(Objects.toString(data.get("outOrderNo")));
                baseMapper.updateByPrimaryKeySelective(orderInfo);
            }
        } catch (Exception e) {
            log.error("支付回调结果异常", e);
            throw ClientServiceException.wrap(CB_NOTIFY_ERROR, e);
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
    public List<CBQueryVO> queryPayOrder(Collection<Integer> orderIds) {
        List<OrderInfo> list = ChainWrappers.lambdaQueryChain(baseMapper)
                .in(OrderInfo::getId, orderIds).eq(OrderInfo::getStatus, PAY_PENDING.getCode()).list();
        List<String> cbNos = list.stream().map(OrderInfo::getCbOrderNo).collect(toList());
        List<CBMiniQueryModel> queryModels = assembleQueryModel(cbNos);
        List<CBQueryVO> queryVOS = Lists.newArrayListWithCapacity(orderIds.size());
        for (CBMiniQueryModel queryModel : queryModels) {
            CBQueryVO cbQueryVO = caiBaoApi.cbPostFormObject(convertFromMap(queryModel), CBQueryVO.class);
            CBQueryDataVO data = cbQueryVO.getData();
            String sign = cbQueryVO.getSign();
            if (!caiBaoApi.verifySign(BeanUtil.toMap(data), sign)) {
                throw ClientServiceException.wrap(CB_QUERY_ERROR);
            }
            queryVOS.add(cbQueryVO);
        }
        return queryVOS;
    }

    private String parseNotify(HttpServletRequest request) throws IOException {
        InputStream inStream = null;
        ByteArrayOutputStream outSteam = null;
        String result = null;
        try {
            inStream = request.getInputStream();
            outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            // 获取采宝调的notify_url的返回信息
            result = outSteam.toString("utf-8");
            log.info("采宝支付回调结果：{}", result);
        } catch (Exception e) {
            log.error("采宝支付回调结果解析失败：{}", result, e);
        } finally {
            assert inStream != null;
            assert outSteam != null;
            inStream.close();
            outSteam.close();
        }
        return result;
    }

    private WxPaymentVO wxPay(OrderInfo orderInfo, List<OrderItemBO> itemBoList) {
        if (Objects.isNull(orderInfo)) {
            throw ClientServiceException.wrap(ORDER_ERROR);
        }
        List<CBGoodsListModel> goodsListModels = itemBoList.stream().map(t -> {
            CBGoodsListModel goodsListModel = new CBGoodsListModel();
            goodsListModel.setGoodsId(t.getProductId().toString());
            goodsListModel.setGoodsNum(t.getProductSn());
            goodsListModel.setGoodsName(t.getProductName());
            goodsListModel.setSellAmount(t.getProductQuantity().toString());
            goodsListModel.setGoodsPrice(t.getProductPrice().toPlainString());
            return goodsListModel;
        }).collect(toList());
        //采宝支付
        CBMiniPayModel miniPayModel = assemblePayModel(orderInfo, goodsListModels);
        CBWxPayVO cbPayVO = caiBaoApi.cbPostFormObject(convertFromMap(miniPayModel), CBWxPayVO.class);
        CBWxPayDataVO data = cbPayVO.getData();
        String sign = cbPayVO.getSign();
        if (!caiBaoApi.verifySign(BeanUtil.toMap(data), sign)) {
            throw ClientServiceException.wrap(CB_PAY_ERROR);
        }
        WxPaymentVO paymentVO = new WxPaymentVO();
        paymentVO.setAppId(data.getAppId());
        paymentVO.set_package("prepay_id=" + data.getPrepayId());
        paymentVO.setNonceStr(data.getNonceStr());
        paymentVO.setSignType(data.getSignType());
        paymentVO.setTimeStamp(data.getTimestamp());
        paymentVO.setPaySign(caiBaoApi.generateSign(BeanUtil.toMap(paymentVO)));
        orderInfo.setCbOrderNo(data.getCbOrderNo());
        return paymentVO;
    }

    private CBMiniPayModel assemblePayModel(OrderInfo orderInfo, List<CBGoodsListModel> goodsListModels) {
        String openId = BaseContextHandler.getOpenId();
        CBMiniPayModel miniPayModel = new CBMiniPayModel();
        miniPayModel.setCommand("open.api.mini.pay");
        miniPayModel.setApp(caiBaoMiniProperties.getAppId());
        miniPayModel.setOperatorId(caiBaoMiniProperties.getOperatorId());
        miniPayModel.setVersion("2.0");
        miniPayModel.setSignType("MD5");
        miniPayModel.setRequestId(UUID.randomUUID().toString());
        miniPayModel.setRequestTime(LocalDateTime.now().toString("yyyyMMddHHmmss"));
        miniPayModel.setLocalOrderNo(orderInfo.getOrderSn());
//        miniPayModel.setAmount(orderInfo.getPayAmount().multiply(BigDecimal.valueOf(100)).longValue());
        miniPayModel.setAmount(BigDecimal.valueOf(0.01).multiply(BigDecimal.valueOf(100)).longValue());
        miniPayModel.setRemark(orderInfo.getRemark());
        miniPayModel.setGoodsList(JSONArray.toJSONString(goodsListModels));
        miniPayModel.setNotifyUrl(caiBaoMiniProperties.getNotifyUrl());
        miniPayModel.setPaymentChannel(caiBaoMiniProperties.getPaymentChannel());
        miniPayModel.setSubAppId(wxMiniProperties.getAppId());
        miniPayModel.setOpenId(openId);
        miniPayModel.setSign(caiBaoApi.generateSign(JSONObject.parseObject(JSONObject.toJSONString(miniPayModel), Map.class)));
        return miniPayModel;
    }

    private List<CBMiniQueryModel> assembleQueryModel(List<String> cbNos) {
        List<CBMiniQueryModel> list = Lists.newArrayListWithCapacity(cbNos.size());
        CBMiniQueryModel queryModel;
        for (String cbNo : cbNos) {
            queryModel = new CBMiniQueryModel();
            queryModel.setCommand("open.api.query");
            queryModel.setApp(caiBaoMiniProperties.getAppId());
            queryModel.setOperatorId(caiBaoMiniProperties.getOperatorId());
            queryModel.setVersion("2.0");
            queryModel.setSignType("MD5");
            queryModel.setRequestId(UUID.randomUUID().toString());
            queryModel.setRequestTime(LocalDateTime.now().toString("yyyyMMddHHmmss"));
            queryModel.setCbOrderNo(cbNo);
            list.add(queryModel);
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private MultiValueMap<String, String> convertFromMap(CBPublicModel miniPayModel) {
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        Map<String, Object> beanMap = JSONObject.parseObject(JSONObject.toJSONString(miniPayModel), Map.class);
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            if (StringUtils.isNotBlank(entry.getKey()) && Objects.nonNull(entry.getValue())) {
                param.add(entry.getKey(), entry.getValue().toString());
            }
        }
        return param;
    }

    /**
     * 判断下单商品是否都有库存
     */
    private boolean hasStock(List<OrderItemBO> list, Integer quantity) {
        for (OrderItemBO orderItemBO : list) {
            if (Objects.isNull(orderItemBO) || orderItemBO.getStock() - quantity < 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断购物车商品是否都有库存
     */
    private boolean hasCartStock(List<OrderItemBO> list) {
        for (OrderItemBO orderItemBO : list) {
            if (Objects.isNull(orderItemBO) || orderItemBO.getStock() - orderItemBO.getProductQuantity() < 0) {
                return true;
            }
        }
        return false;
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
        vo.setOrderVO(payOrderVO);
        List<PayOrderItemVO> collect = itemList.stream().map(t -> BeanCopierUtils.generalCopyBean(t, PayOrderItemVO.class)).collect(toList());
        vo.setItemVO(collect);
        PayReceiveAddressVO addressVO = BeanCopierUtils.generalCopyBean(orderInfo, PayReceiveAddressVO.class);
        vo.setAddressVO(addressVO);
        vo.setPaymentVO(wxPaymentVO);
        return vo;
    }

    //    private boolean lock(Integer productId, Integer quantity) {
//        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
//        String lockKey = Joiner.on(":").join(RedisConstants.CREATE_ORDER_LOCK, productId);
//        String lockVal = String.valueOf(userId);
//    }
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
        address.setDetailAddress(address.getProvince() + address.getCity() + address.getRegion() + address.getDetailAddress());
        deliveryOrderVO.setPickUp(BeanCopierUtils.generalCopyBean(pickUp, PayReceiveAddressVO.class));
        deliveryOrderVO.setDelivery(BeanCopierUtils.generalCopyBean(address, PayReceiveAddressVO.class));
        return deliveryOrderVO;
    }

    private OrderInfo assembleOrder(Integer userId, CreateOrderBaseModel model, List<OrderItemBO> itemBoList) {
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
        FansAddressBO address = null;
        //商品类产品有自提和配送区分
        if (FALSE.getCode().equals(model.getProductType())) {
            //收货人信息：姓名、电话、邮编、地址
            address = productService.getAddress(null, model.getFansReceiveAddressId(), model.getDeliveryType());
            orderInfo.setReceiverName(address.getName());
            orderInfo.setReceiverPhone(address.getPhoneNumber());
            orderInfo.setReceiverPostCode(address.getPostCode());
            orderInfo.setReceiverProvince(address.getProvince());
            orderInfo.setReceiverCity(address.getCity());
            orderInfo.setReceiverRegion(address.getRegion());
            orderInfo.setReceiverDetailAddress(address.getDetailAddress());
        }
        //0->未确认；1->已确认
        orderInfo.setConfirmStatus((byte) 0);
        orderInfo.setDeleteStatus((byte) 0);
        //生成订单号
        orderInfo.setOrderSn(generateOrderSn(orderInfo));
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
                LockStockForm form = new LockStockForm();
                form = new LockStockForm();
                form.setProductId(t.getProductId());
                form.setQuantity(t.getProductQuantity());
                return form;
            }).collect(toList());
            list.addAll(lockStockForms);
        }
        productService.lockProductStock(list, productType);
    }
}
