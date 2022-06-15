package com.yunya365.mini.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.ivy_mini.domain.bo.OrderItemBO;
import com.yunya.feign.ivy_mini.domain.model.CreateGoodsOrderModel;
import com.yunya.feign.ivy_mini.domain.model.CreateVirtualOrderModel;
import com.yunya.feign.ivy_mini.domain.vo.*;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya365.mini.entity.*;
import com.yunya365.mini.mapper.OrderInfoMapper;
import com.yunya365.mini.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.yunya.framework.common.constant.BusinessConstants.*;
import static com.yunya.framework.common.constant.RedisConstants.*;
import static com.yunya365.mini.enums.IvyMiniError.*;
import static com.yunya365.mini.enums.TrueFalseEnum.*;
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
    private IFansReceiveAddressService receiveAddressService;
    @Resource
    private IOrderSettingService orderSettingService;
    @Resource
    private IOrderItemService orderItemService;
    @Resource
    private RedisUtils redisUtils;

    @Override
    public CreateOrderVO createGoodsOrder(CreateGoodsOrderModel model) {
        boolean locked = false;
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        Integer productId = model.getProductId();
        //购买数量
        Integer quantity = model.getQuantity();
        String lockKey = Joiner.on(":").join(RedisConstants.CREATE_ORDER_LOCK, productId);
        String lockVal = String.valueOf(userId);
        try {
            //加锁
            locked = redisUtils.setLock(lockKey, lockVal, MEDICAL_APPLY_LOCK_SEC, TimeUnit.SECONDS);
            List<OrderItemBO> itemBoList = Lists.newArrayList();
            //原始商品集合(商品)
            if (FALSE.equals(model.getProductType())) {
                itemBoList = productService.listGoodsOrderItem(Collections.singleton(productId));
            }
            //原始商品集合(虚拟服务)
            if (TRUE.equals(model.getProductType())) {
                itemBoList = productService.listVirtualOrderItem(Collections.singleton(productId));
            }
            //判断购物车中商品是否都有库存
            if (!hasStock(itemBoList, quantity)) {
                throw ClientServiceException.wrap(STOCK_LACK);
            }
            //进行库存锁定 todo

            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setFansId(userId);
            orderInfo.setPayType(model.getPayType());
            orderInfo.setTotalAmount(calcTotalAmount(itemBoList));
            orderInfo.setPayAmount(calcTotalAmount(itemBoList));
            orderInfo.setSourceType((byte) 1);
            //订单状态（0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->申请退款）
            orderInfo.setStatus((byte) 0);
            orderInfo.setOrderType((byte) 0);
            //配送方式：0->自提 1->配送
            orderInfo.setDeliveryType(model.getDeliveryType());
            orderInfo.setRemark(model.getRemark());
            //收货人信息：姓名、电话、邮编、地址
            FansReceiveAddress address = receiveAddressService.getById(model.getFansReceiveAddressId());
            orderInfo.setReceiverName(address.getName());
            orderInfo.setReceiverPhone(address.getPhoneNumber());
            orderInfo.setReceiverPostCode(address.getPostCode());
            orderInfo.setReceiverProvince(address.getProvince());
            orderInfo.setReceiverCity(address.getCity());
            orderInfo.setReceiverRegion(address.getRegion());
            orderInfo.setReceiverDetailAddress(address.getDetailAddress());
            //0->未确认；1->已确认
            orderInfo.setConfirmStatus((byte) 0);
            orderInfo.setDeleteStatus((byte) 0);
            //生成订单号
            orderInfo.setOrderSn(generateOrderSn(orderInfo));
            baseMapper.insert(orderInfo);
            List<OrderItem> itemList = itemBoList.stream().map(t -> {
                OrderItem orderItem = BeanCopierUtils.generalCopyBean(t, OrderItem.class);
                orderItem.setProductQuantity(quantity);
                orderItem.setOrderId(orderInfo.getId());
                orderItem.setOrderSn(orderInfo.getOrderSn());
                return orderItem;
            }).collect(toList());
            orderItemService.saveBatch(itemList);
            //todo 发送延迟消息取消订单
            return createVO(orderInfo, itemList, address);
        } finally {
            if (locked) {
                log.info("【解锁成功】");
                redisUtils.unlock(lockKey, lockVal);
            }
        }
    }

    @Override
    public CreateOrderVO createVirtualOrder(CreateVirtualOrderModel model) {
        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
        Integer productId = model.getProductId();
        //购买数量
        Integer quantity = model.getQuantity();
        List<OrderItemBO> itemBoList = productService.listGoodsOrderItem(Collections.singleton(productId));
        return null;
    }


    @Override
    public void paySuccess() {

    }

    /**
     * 判断下单商品是否都有库存
     */
    private boolean hasStock(List<OrderItemBO> list, Integer quantity) {
        for (OrderItemBO orderItemBO : list) {
            if (Objects.isNull(orderItemBO) || orderItemBO.getStock() - quantity < 0) {
                return false;
            }
        }
        return true;
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

    private CreateOrderVO createVO(OrderInfo orderInfo, List<OrderItem> itemList, FansReceiveAddress address) {
        CreateOrderVO vo = new CreateOrderVO();
        PayOrderVO payOrderVO = BeanCopierUtils.generalCopyBean(orderInfo, PayOrderVO.class);
        payOrderVO.setOrderId(orderInfo.getId());
        payOrderVO.setOrderDate(orderInfo.getCrtTime());
        vo.setOrderVO(payOrderVO);
        List<PayOrderItemVO> collect = itemList.stream().map(t -> BeanCopierUtils.generalCopyBean(t, PayOrderItemVO.class)).collect(toList());
        vo.setItemVO(collect);
        PayReceiveAddressVO addressVO = BeanCopierUtils.generalCopyBean(address, PayReceiveAddressVO.class);
        vo.setAddressVO(addressVO);
        return vo;
    }

//    private boolean lock(Integer productId, Integer quantity) {
//        Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
//        String lockKey = Joiner.on(":").join(RedisConstants.CREATE_ORDER_LOCK, productId);
//        String lockVal = String.valueOf(userId);
//    }
}
