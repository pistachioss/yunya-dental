package com.yunya.modules.treatment.biz;

import com.alibaba.fastjson.JSONObject;
import com.yunya.feign.treatment.domain.model.PaymentModel;
import com.yunya.feign.treatment.domain.query.BillPayShareDetailQuery;
import com.yunya.feign.treatment.domain.vo.BillPayDetailRecordVO;
import com.yunya.feign.treatment.domain.vo.BillPayShareDetailVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.treatment.BillPayRecord;
import com.yunya.models.treatment.BillPayShareDetail;
import com.yunya.modules.treatment.biz.shared.IItemPaySharedAmount;
import com.yunya.modules.treatment.mapper.BillPayDetailRecordMapper;
import com.yunya.modules.treatment.mapper.BillPayRecordMapper;
import com.yunya.modules.treatment.mapper.BillPayShareDetailMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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
    /** 账单收费记录 */
    @Autowired private BillPayRecordMapper billPayRecordMapper;

    /**
     * 提取其他入账方式中的免单总额
     *
     * @param payments 其他入账方式（含免单）
     * @return
     */
    private BigDecimal extrationFreeAmount(Set<PaymentModel> payments) {
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
     * @param billPay 本次收费记录
     */
    public void saveItemPaySharedAmount(BigDecimal totalCharge, Set<PaymentModel> payments, BillPayRecord billPay) {
        BigDecimal freeAmount = extrationFreeAmount(payments);
        BigDecimal receivedAmount = totalCharge.subtract(freeAmount);
        saveItemPaySharedAmount(freeAmount, receivedAmount, billPay.getOrderRecordId(), billPay.getId(), billPay.getCrtTime());
    }

    /**
     * 保存本次收费的项目分摊明细
     *
     * @param thisFreeAmount 本次收费的免单金额
     * @param thisReceivedAmount 本次收费的实收金额
     * @param orderRecordId 订单id
     * @param payDate 收费日期
     */
    public void saveItemPaySharedAmount(BigDecimal thisFreeAmount, BigDecimal thisReceivedAmount, Integer orderRecordId, Integer billPayId, Date payDate) {
        // 查询本次收费之前的项目实收和免单
        List<BillPayShareDetailVO> itemPayDetails = mapper.selectItemPayDetailDeadlineBillPayId(orderRecordId, billPayId);
        // 纠正账单在当时的各项目应收金额
        rectifyActualAmount(itemPayDetails, payDate);
        // 根据分摊规则生成项目的实收和免单分摊数据
        Collection<BillPayShareDetail> result = iItemPaySharedAmount.generateSharedDetails(thisFreeAmount, thisReceivedAmount, itemPayDetails, payDate);
        List<BillPayShareDetail> shareDetails = result.stream()
                .filter(vo->StringHelper.gtZero(vo.getFreeAmount()) || StringHelper.gtZero(vo.getReceivedAmount()) || StringHelper.gtZero(vo.getSwipeWorkload()))
                .collect(Collectors.toList());
        if (StringHelper.isEmpty(shareDetails)) {
            log.error("账单收费：{}没有可分摊的项目",  billPayId);
            return;
        }
        // 批量保存
        mapper.batchSave(shareDetails);
        // 更新项目已收和免单分摊总计
        orderDetailPayRecordBiz.statOrderDetailPayItemTotal(orderRecordId);
    }

    /**
     * 纠正账单明细的项目应收金额
     * 由于账单首次挂账0，在收欠费时使用优惠，导致应收变更，所以当费用对不上时，需要将应收回退到原价状态
     *
     * @param itemPayDetails
     * @param payDate
     */
    private void rectifyActualAmount(List<BillPayShareDetailVO> itemPayDetails, Date payDate) {
        itemPayDetails.forEach(vo -> {
            Date privilegeDate = vo.getPrivilegeDate();
            // 本次收费时的应收=原价
            if (StringHelper.isNotNull(privilegeDate) && payDate.before(privilegeDate)) {
                vo.setActualReceivable(vo.getReceivableAmount());
                vo.setTariffActualAmount(vo.getTariffReceivableAmount());
                vo.setOralActualAmount(vo.getOralReceivableAmount());
            }
        });
    }

    /**
     * 洗牌并生成项目分摊数据
     *
     * @param orderRecordId
     */
    public void shullfeItemPaySharedDetail(Integer orderRecordId) {
        BillPayShareDetailQuery query = new BillPayShareDetailQuery();
        query.setOrderRecordId(orderRecordId);
        removeByCombinationKey(query.getOrderRecordId(), query.getBillPayId(), null);
        shullfeItemPaySharedDetail(query);
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
        mapper.tombstoneByCombinationKey(orderRecordId, billPayId, orderDetailId);
        orderDetailPayRecordBiz.statOrderDetailPayItemTotal(orderRecordId);
    }

    /**
     * 对指定账单或指定收费进行洗牌，并重新生成项目分摊数据
     *
     * @param query
     */
    public void shullfeItemPaySharedDetail(BillPayShareDetailQuery query) {
        List<BillPayDetailRecordVO> details = billPayDetailRecordMapper.selectBillPayDetailList(query);
        // 按收费id + 收费时间分组
        Map<String, JSONObject> map = new LinkedHashMap<>(16);
        details.forEach(vo->{
            BigDecimal amount = vo.getAmount();
            Date crtTime = vo.getCrtTime();
            Integer inservice = vo.getInservice();
            String key = vo.getBillPayRecordId() + "," + inservice + "," + DateUtil.toDateTime(crtTime);
            JSONObject billPay = map.computeIfAbsent(key, k->{
                JSONObject obj = new JSONObject();
                obj.put("freeAmount", BigDecimal.ZERO);
                obj.put("receivedAmount", BigDecimal.ZERO);
                obj.put("payDate", crtTime);
                obj.put("orderRecordId", vo.getOrderRecordId());
                if (inservice == -1) {
                    obj.put("inservice", inservice);
                }
                return obj;
            });
            if (FREE_PAYMENT_ID.contains(vo.getAccountItemId())) {
                BigDecimal freeAmount = StringHelper.defaultBigDecimal(billPay.getBigDecimal("freeAmount"));
                billPay.put("freeAmount", freeAmount.add(amount));
            } else {
                BigDecimal receivedAmount = StringHelper.defaultBigDecimal(billPay.getBigDecimal("receivedAmount"));
                billPay.put("receivedAmount", receivedAmount.add(amount));
            }
        });
        map.forEach((key, billPay)->{
            Integer orderRecordId = billPay.getInteger("orderRecordId");
            String[] keys = StringHelper.split(key, ",");
            Integer billPayId = Integer.parseInt(keys[0]);
            if (StringHelper.isNotNull(billPay.getBoolean("inservice"))) {
                // 撤销收费或调整入账方式，需对billPayId的分摊明细作废
                removeByCombinationKey(orderRecordId, billPayId, null);
                return;
            }
            saveItemPaySharedAmount(billPay.getBigDecimal("freeAmount"),
                    billPay.getBigDecimal("receivedAmount"),
                    orderRecordId,
                    billPayId,
                    billPay.getDate("payDate"));
//            rabbitMqServiceFeign.sendMessage(billPayRecordId, 0, BaseBillPay);
        });
    }

    public void deleteByBillDateRange(BillPayShareDetailQuery query) {
        mapper.removeByBillDateRange(query);
    }
}
