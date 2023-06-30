package com.yunya.modules.treatment.biz.shared.ratio;

import com.yunya.feign.treatment.domain.vo.BillPayShareDetailVO;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.treatment.BillPayShareDetail;

import java.math.BigDecimal;
import java.util.*;

/**
 * 项目金额分摊：
 *  按项目应收占比分摊：
 *
 * @author: chenlin
 * @date: 2023/4/6 10:02
 * @description:
 * @since: 1.0.0
 */
//@Service
public class ItemRatioSharedAmountBiz extends AbstractRatioSharedAmountBiz{

    @Override
    public Collection<BillPayShareDetail> generateSharedDetails(BigDecimal thisFreeAmount, BigDecimal thisReceivedAmount, List<BillPayShareDetailVO> itemPayDetails, Date payDate) {
        int size = itemPayDetails.size();
        Map<Integer, BillPayShareDetail> result = new HashMap<>(size);
        // 【本次费用分摊后免单剩余，本次费用分摊后实收剩余，项目欠费】
        BigDecimal[] payments = {thisFreeAmount, thisReceivedAmount, BigDecimal.ZERO};
        BigDecimal[] thisCharge = {thisFreeAmount, thisReceivedAmount};
        // 项目分摊
        for (int i=0; i < size; i++) {
            BillPayShareDetailVO detail = itemPayDetails.get(i);
            BillPayShareDetail shareDetail = priorityRatio(detail, thisCharge);

            if (StringHelper.isNotNull(shareDetail)) {
                Integer orderDetailId = shareDetail.getOrderDetailId();
                BillPayShareDetail o = result.computeIfAbsent(orderDetailId, k->shareDetail);
                o.setPayDate(payDate);
                payments[0] = payments[0].subtract(o.getFreeAmount());
                payments[1] = payments[1].subtract(o.getReceivedAmount());
            }
        }

        return itemRefundSurplus(payments, itemPayDetails, result);
    }

    /**
     * 费用优先分摊：免单金额优先往价目上进行占比分摊，实收金额优先往商品上进行占比分摊，
     *
     * @param detail
     * @param thisCharge
     * @return
     */
    public BillPayShareDetail priorityRatio(BillPayShareDetailVO detail, BigDecimal[] thisCharge) {
        BigDecimal billActualAmount = detail.getTariffActualAmount().add(detail.getOralActualAmount());
        BigDecimal itemActualAmount = detail.getActualReceivable();
        BigDecimal totalReceived = detail.getItemRecAmount().add(detail.getItemFreeAmount());
        // 项目缺口(欠费)
        BigDecimal gap = itemActualAmount.subtract(totalReceived);
        if (StringHelper.eqZero(gap)) {
            // 已填满的项目不再进行分摊
            return buildSharedDetail(detail, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        BigDecimal freeShared = sharedAmount(thisCharge[0], itemActualAmount, billActualAmount);
        BigDecimal receivedShared = sharedAmount(thisCharge[1], itemActualAmount, billActualAmount);
        detail.setItemRecAmount(detail.getItemRecAmount().add(receivedShared));
        detail.setItemFreeAmount(detail.getItemFreeAmount().add(freeShared));
        return buildSharedDetail(detail, freeShared, receivedShared);
    }
}
