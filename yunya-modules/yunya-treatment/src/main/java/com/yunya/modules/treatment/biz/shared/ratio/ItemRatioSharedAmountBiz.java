package com.yunya.modules.treatment.biz.shared.ratio;

import com.yunya.feign.treatment.domain.vo.BillPayShareDetailVO;
import com.yunya.framework.common.utils.BeanUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.treatment.BillPayShareDetail;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
@Service
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
            return null;
        }
        BigDecimal freeShared = sharedAmount(thisCharge[0], itemActualAmount, billActualAmount);
        BigDecimal receivedShared = sharedAmount(thisCharge[1], itemActualAmount, billActualAmount);
        detail.setItemRecAmount(detail.getItemRecAmount().add(receivedShared));
        detail.setItemFreeAmount(detail.getItemFreeAmount().add(freeShared));
        BillPayShareDetail shareDetail = new BillPayShareDetail();
        BeanUtil.copyProperties(detail, shareDetail);
        shareDetail.setFreeAmount(freeShared);
        shareDetail.setReceivedAmount(receivedShared);
        return shareDetail;
    }

    public static void main(String[] args) {
        Integer t1 = 20;
        Integer t2 = 300;
        BigDecimal d1 = new BigDecimal(t1);
        BigDecimal d2 = new BigDecimal(t1 + t2);
        BigDecimal d3 = new BigDecimal("100");
        BigDecimal tarffiFree = d1.divide(d2, 8, RoundingMode.HALF_UP).multiply(d3).setScale(4, RoundingMode.DOWN);
        System.out.println("价目免单：" + tarffiFree);

        d1 = new BigDecimal(t1);
        d2 = new BigDecimal(t1 + t2);
        d3 = new BigDecimal("12");
        BigDecimal tarffiRec = d1.divide(d2, 8, RoundingMode.HALF_UP).multiply(d3).setScale(4, RoundingMode.DOWN);
        System.out.println("价目实收：" + tarffiRec);

        d1 = new BigDecimal(t2);
        d2 = new BigDecimal(t1 + t2);
        d3 = new BigDecimal("100");
        BigDecimal oralFree = d1.divide(d2, 8, RoundingMode.HALF_UP).multiply(d3).setScale(4, RoundingMode.DOWN);
        System.out.println("商品免单：" + oralFree);

        d1 = new BigDecimal(t2);
        d2 = new BigDecimal(t1 + t2);
        d3 = new BigDecimal("12");
        BigDecimal oralRec = d1.divide(d2, 8, RoundingMode.HALF_UP).multiply(d3).setScale(4, RoundingMode.DOWN);
        System.out.println("商品实收：" + oralRec);

        System.out.println("价目费用：" + (tarffiFree.add(tarffiRec)));
        System.out.println("商品费用：" + (oralFree.add(oralRec)));
        System.out.println("总费用：" + (tarffiFree.add(tarffiRec).add(oralFree).add(oralRec)));
    }
}
