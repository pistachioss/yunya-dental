package com.yunya365.mini.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yunya365.mini.entity.OrderInfo;
import com.yunya365.mini.entity.OrderSetting;
import com.yunya365.mini.mapper.OrderSettingMapper;
import com.yunya365.mini.service.IOrderInfoService;
import com.yunya365.mini.service.IOrderSettingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static com.yunya.framework.common.enums.TrueFalseEnum.*;
import static com.yunya365.mini.enums.OrderStatusEnum.*;

/**
 * <p>
 * 订单设置表 服务实现类
 * </p>
 *
 * @author xiangyang
 * @since 2022-06-10
 */
@Service
@Slf4j
public class OrderSettingServiceImpl extends ServiceImpl<OrderSettingMapper, OrderSetting> implements IOrderSettingService {

    @Resource
    private IOrderInfoService orderInfoService;

    @Override
    public void autoConfirm() {
        List<OrderSetting> list = super.list();
        if (CollectionUtils.isNotEmpty(list)) {
            OrderSetting orderSetting = list.get(0);
            log.info("订单设置：{}", orderSetting);
            List<OrderInfo> orderInfos = orderInfoService.listShipped(orderSetting.getConfirmOvertime());
            orderInfos.forEach(t -> {
                t.setStatus(FINISH.getCode().byteValue());
                t.setConfirmStatus(TRUE.getCode().byteValue());
                t.setReceiveTime(new Date());
            });
            orderInfoService.updateBatchById(orderInfos);
        }
    }
}
