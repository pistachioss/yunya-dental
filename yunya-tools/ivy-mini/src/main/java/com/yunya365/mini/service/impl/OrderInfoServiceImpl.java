package com.yunya365.mini.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Joiner;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.ivy_mini.domain.model.CreateGoodsOrderModel;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.tariff.BaseOralTariff;
import com.yunya365.mini.entity.OrderInfo;
import com.yunya365.mini.mapper.OrderInfoMapper;
import com.yunya365.mini.service.IOrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static com.yunya.framework.common.constant.BusinessConstants.*;
import static com.yunya365.mini.enums.IvyMiniError.*;

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
    private RedisUtils redisUtils;

    @Override
    public void createGoodsOrder(CreateGoodsOrderModel model) {
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
            //库存数量
            BaseOralTariff tariff = treatmentServiceFeign.findBaseOralTariffById(productId);
            if (tariff.getStock() < quantity) {
                throw ClientServiceException.wrap(STOCK_LACK);
            }
        } finally {
            if (locked) {
                log.info("【解锁成功】");
                redisUtils.unlock(lockKey, lockVal);
            }
        }
    }

    @Override
    public void paySuccess() {

    }

    /**
     * 生成18位订单编号:8位日期+2位平台号码+2位支付方式+6位以上自增id
     */
//    private String generateOrderSn(OmsOrder order) {
//        StringBuilder sb = new StringBuilder();
//        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
//        String key = REDIS_DATABASE+":"+ REDIS_KEY_ORDER_ID + date;
//        Long increment = redisService.incr(key, 1);
//        sb.append(date);
//        sb.append(String.format("%02d", order.getSourceType()));
//        sb.append(String.format("%02d", order.getPayType()));
//        String incrementStr = increment.toString();
//        if (incrementStr.length() <= 6) {
//            sb.append(String.format("%06d", increment));
//        } else {
//            sb.append(incrementStr);
//        }
//        return sb.toString();
//    }
}
