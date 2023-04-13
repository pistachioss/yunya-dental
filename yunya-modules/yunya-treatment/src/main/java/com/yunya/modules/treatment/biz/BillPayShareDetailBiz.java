package com.yunya.modules.treatment.biz;

import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.treatment.domain.model.PaymentModel;
import com.yunya.feign.treatment.domain.vo.BillPayShareDetailVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.treatment.BillPayDetailRecord;
import com.yunya.models.treatment.BillPayShareDetail;
import com.yunya.modules.treatment.mapper.BillPayDetailRecordMapper;
import com.yunya.modules.treatment.mapper.BillPayShareDetailMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseBillPay;
import static com.yunya.framework.common.constant.BusinessConstants.FREE_PAYMENT_ID;

/**
 * @author: chenlin
 * @date: 2023/4/11 14:35
 * @description:
 * @since: 1.0.0
 */
@Slf4j
@Service
public class BillPayShareDetailBiz extends BaseBiz<BillPayShareDetailMapper, BillPayShareDetail> {

    /** 账单收费分摊明细 */
    @Autowired private IItemPaySharedAmount iItemPaySharedAmount;
    /** 订单项目收费明细 */
    @Autowired private OrderDetailPayRecordBiz orderDetailPayRecordBiz;
    /** 账单收费方式明细 */
    @Autowired private BillPayDetailRecordMapper billPayDetailRecordMapper;
    @Autowired private RemoteRabbitMqServiceFeign rabbitMqServiceFeign;

    /**
     * 提取其他入账方式中的免单总额
     *
     * @param totalCharge 总收费（含免单）
     * @param payments 其他入账方式（含免单）
     * @return
     */
    private BigDecimal extrationFreeAmount(BigDecimal totalCharge, Set<PaymentModel> payments) {
        BigDecimal freePayment = BigDecimal.ZERO;
        for (PaymentModel payment : payments) {
            Integer accountItemId = payment.getAccountItemId();
            if (FREE_PAYMENT_ID.contains(accountItemId)) {
                freePayment = freePayment.add(payment.getAmount());
            }
        }
        return freePayment;
    }

    /**
     * 保存本次收费的项目分摊明细
     *
     * @param totalCharge 付款总额
     * @param payments 其他入账方式
     * @param orderRecordId 订单id
     * @param billPayId 本次收费id
     */
    public void saveItemPaySharedAmount(BigDecimal totalCharge, Set<PaymentModel> payments, Integer orderRecordId, Integer billPayId) {
        BigDecimal freeAmount = extrationFreeAmount(totalCharge, payments);
        BigDecimal receivedAmount = totalCharge.subtract(freeAmount);
        saveItemPaySharedAmount(freeAmount, receivedAmount, orderRecordId, billPayId);
    }

    /**
     * 保存本次收费的项目分摊明细
     *
     * @param thisFreeAmount 本次收费的免单金额
     * @param thisReceivedAmount 本次收费的实收金额
     * @param orderRecordId 订单id
     * @param billPayId 收费id
     */
    public void saveItemPaySharedAmount(BigDecimal thisFreeAmount, BigDecimal thisReceivedAmount, Integer orderRecordId, Integer billPayId) {
        Integer optId = Integer.parseInt(BaseContextHandler.getUserID());
        // 查询本次收费之前的项目实收和免单
        List<BillPayShareDetailVO> itemPayDetails = orderDetailPayRecordBiz.findItemPayDetailDeadlineBillPayId(orderRecordId, billPayId);
        // 根据分摊规则生成项目的实收和免单分摊数据
        BillPayShareDetail[] result = iItemPaySharedAmount.generateSharedDetails(thisFreeAmount, thisReceivedAmount, billPayId, itemPayDetails, optId);
        List<BillPayShareDetail> shareDetails = Arrays.stream(result)
                .filter(vo-> StringHelper.isNotNull(vo) && (StringHelper.gtZero(vo.getFreeAmount()) || StringHelper.gtZero(vo.getReceivedAmount())))
                .collect(Collectors.toList());
        if(StringHelper.isEmpty(shareDetails)) {
            log.error("账单收费：{}没有可分摊的项目", billPayId);
            return;
        }
        // 批量保存
        mapper.batchSave(shareDetails);
        // 更新项目已收和免单分摊总计
        orderDetailPayRecordBiz.statOrderDetailPayItemTotal(orderRecordId);
    }

    /**
     * 洗牌并生成项目分摊数据
     *
     * @param orderRecordId
     */
    public void shullfeItemPaySharedDetail(Integer orderRecordId) {
        shullfeItemPaySharedDetail(orderRecordId, null);
    }

    /**
     * 通过组合键删除
     *
     * @param orderRecordId
     * @param billPayId
     * @param orderDetailId
     */
    public void removeByCombinationKey(Integer orderRecordId, Integer billPayId, Integer orderDetailId) {
//        if (StringHelper.isAllNull(orderRecordId, billPayId, orderDetailId)) {
//            throw new ClientServiceException(INTERNAL_SERVER_ERROR);
//        }
        mapper.removeByCombinationKey(orderRecordId, billPayId, orderDetailId);
    }

    /**
     * 洗牌并生成项目分摊数据
     *
     * @param orderRecordId
     * @param billPayId
     */
    public void shullfeItemPaySharedDetail(Integer orderRecordId, Integer billPayId) {
        removeByCombinationKey(orderRecordId, billPayId, null);
        List<BillPayDetailRecord> details = billPayDetailRecordMapper.selectBillPayDetailList(orderRecordId, billPayId);
        Map<String, BigDecimal[]> map = new LinkedHashMap<>(16);
        for (BillPayDetailRecord vo : details) {
            BigDecimal amount = vo.getAmount();
            String key = StringHelper.joinWith(",", vo.getOrderRecordId(), vo.getBillPayRecordId());
            BigDecimal[] amounts = map.computeIfAbsent(key, k -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO});
            Integer accountItemId = vo.getAccountItemId();
            if (FREE_PAYMENT_ID.contains(accountItemId)) {
                amounts[0] = amounts[0].add(amount);
            } else {
                amounts[1] = amounts[1].add(amount);
            }
        }
        map.forEach((key, amounts)->{
            String[] keys = StringHelper.split(key, ",");
            int billPayRecordId = Integer.parseInt(keys[1]);
            saveItemPaySharedAmount(amounts[0], amounts[1], Integer.parseInt(keys[0]), billPayRecordId);
            rabbitMqServiceFeign.sendMessage(billPayRecordId, 0, BaseBillPay);
        });
    }
}
