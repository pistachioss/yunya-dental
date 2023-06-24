package com.yunya.modules.treatment.biz.shared.ratio;

import com.yunya.feign.treatment.domain.vo.BillPayShareDetailVO;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.treatment.BillPayShareDetail;
import com.yunya.modules.treatment.biz.shared.filling.AbstractFillingSharedAmountBiz;
import com.yunya.modules.treatment.biz.shared.IItemPaySharedAmount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author: chenlin
 * @date: 2023/6/21 11:22
 * @description:
 * @since: 1.0.0
 */
public abstract class AbstractRatioSharedAmountBiz extends AbstractFillingSharedAmountBiz implements IItemPaySharedAmount {

    abstract BillPayShareDetail priorityRatio(BillPayShareDetailVO detail, BigDecimal[] payment);

    /**
     * 对最后一个项目的免单和实收进行多退少补，因为按应收占比分摊时，由于精度问题会导致分摊到项目免单（实收）之和不等于本次收费免单金额（本次收费实收金额）
     *
     * @param thisFreeAmount
     * @param thisReceivedAmount
     * @param result
     */
    protected void itemRefundSurplus(BigDecimal thisFreeAmount, BigDecimal thisReceivedAmount, Map<Integer, BillPayShareDetail> result) {
        BigDecimal[] total = {BigDecimal.ZERO, BigDecimal.ZERO};
        // 最后一项免单分摊、最后一项实收分摊所在位置和正确金额（多退少补原则）
        Integer[] lastIndex = {null, null};
        BigDecimal[] lastShared = {null, null};
        result.forEach((ind, shareDetail)->{
            BigDecimal freeAmount = shareDetail.getFreeAmount();
            if (StringHelper.gtZero(freeAmount)) {
                total[0] = total[0].add(freeAmount);
                BigDecimal deviation = thisFreeAmount.subtract(total[0]);
                if (!StringHelper.eqZero(deviation)) {
                    lastIndex[0] = ind;
                    lastShared[0] = deviation;
                } else {// 不需要多退少补
                    lastIndex[0] = null;
                    lastShared[0] = null;
                }
            }

            BigDecimal receivedAmount = shareDetail.getReceivedAmount();
            if (StringHelper.gtZero(receivedAmount)) {
                total[1] = total[1].add(receivedAmount);
                BigDecimal deviation = thisReceivedAmount.subtract(total[1]);
                if (!StringHelper.eqZero(deviation)) {
                    lastIndex[1] = ind;
                    lastShared[1] = deviation;
                } else {// 不需要多退少补
                    lastIndex[1] = null;
                    lastShared[1] = null;
                }
            }
        });
        if (!StringHelper.isNull(lastIndex[0]) && !StringHelper.isNull(lastShared[0])) {
            BillPayShareDetail shareDetail = result.get(lastIndex[0]);
            shareDetail.setFreeAmount(shareDetail.getFreeAmount().add(lastShared[0]));
        }
        if (!StringHelper.isNull(lastIndex[1]) && !StringHelper.isNull(lastShared[1])) {
            BillPayShareDetail shareDetail = result.get(lastIndex[1]);
            shareDetail.setReceivedAmount(shareDetail.getReceivedAmount().add(lastShared[1]));
        }
        System.out.println("本次免单费用：" + thisFreeAmount + "，本次免单分摊" +  total[0]);
        System.out.println("本次实收费用：" + thisReceivedAmount + "，本次实收分摊" +  total[1]);
    }

    /**
     *
     * 对项目利用顺序填充原则，进行多退少补
     *
     * @param payment
     * @param itemPayDetails
     * @param result
     */
    public Collection<BillPayShareDetail> itemRefundSurplus(BigDecimal[] payment, List<BillPayShareDetailVO> itemPayDetails, Map<Integer, BillPayShareDetail> result) {
        if (StringHelper.eqZero(payment[0]) && StringHelper.eqZero(payment[1])) {
            // 本次费用全部分摊完
            return result.values();
        }
        // 小数点后尾数补充
        itemPayDetails.forEach(detail->{
            Integer orderDetailId = detail.getOrderDetailId();
            BillPayShareDetail shareDetail = result.get(orderDetailId);
            if (StringHelper.isNull(shareDetail)) {
                return;
            }
            BigDecimal actualAmount = detail.getActualReceivable();
            BigDecimal totalReceived = detail.getItemFreeAmount().add(detail.getItemRecAmount());
            // 项目缺口(欠费)
            BigDecimal gap = actualAmount.subtract(totalReceived);
            if (StringHelper.leZero(gap)) {
                // 已填满的项目不再进行分摊
                return;
            }
            payment[2] = gap;
            BigDecimal freeShared = sharedAmount(payment, 0);
            BigDecimal receivedShared = BigDecimal.ZERO;
            if (StringHelper.leZero(payment[0])) {
                receivedShared = sharedAmount(payment, 1);
            }
            detail.setItemRecAmount(detail.getItemRecAmount().add(receivedShared));
            detail.setItemFreeAmount(detail.getItemFreeAmount().add(freeShared));
            shareDetail.setFreeAmount(shareDetail.getFreeAmount().add(freeShared));
            shareDetail.setReceivedAmount(shareDetail.getReceivedAmount().add(receivedShared));
        });

        // 最后一次收费（不欠费）的多退少补，因为之前收费的占比分摊+多退少补会导致部分项目超出应收，部分项目少于应收
        BigDecimal gap = BigDecimal.ZERO;
        // 多退
        for (BillPayShareDetailVO detail : itemPayDetails) {
            Integer orderDetailId = detail.getOrderDetailId();
            BillPayShareDetail shareDetail = result.get(orderDetailId);
            if (StringHelper.isNull(shareDetail)) {
                continue;
            }
            BigDecimal actualAmount = detail.getActualReceivable();
            BigDecimal totalReceived = detail.getItemFreeAmount().add(detail.getItemRecAmount());
            BigDecimal diff = actualAmount.subtract(totalReceived);
            if (StringHelper.ltZero(diff)) {
                shareDetail.setReceivedAmount(shareDetail.getReceivedAmount().add(diff));
//                detail.setItemRecAmount(detail.getItemRecAmount().add(diff));
                gap = gap.add(diff.abs());
            }
        }
        if (StringHelper.isNotNull(gap) && StringHelper.gtZero(gap)) {
            // 少补
            for (BillPayShareDetailVO detail : itemPayDetails) {
                Integer orderDetailId = detail.getOrderDetailId();
                BillPayShareDetail shareDetail = result.get(orderDetailId);
                if (StringHelper.isNull(shareDetail)) {
                    continue;
                }
                BigDecimal actualAmount = detail.getActualReceivable();
                BigDecimal totalReceived = detail.getItemFreeAmount().add(detail.getItemRecAmount());
                BigDecimal diff = actualAmount.subtract(totalReceived);
                if (StringHelper.gtZero(diff) && StringHelper.le(diff, gap)) {
                    shareDetail.setReceivedAmount(shareDetail.getReceivedAmount().add(diff));
//                    detail.setItemRecAmount(detail.getItemRecAmount().add(diff));
                    gap = gap.subtract(diff);
                }
            }
        }
        return result.values();
    }

    public BillPayShareDetail priorityFilling(BillPayShareDetailVO detail, BigDecimal[] payment) {
        return null;
    }

    /**
     * 金额分摊规则
     *
     * @param receivedAmount 本次收费（实收or免单）
     * @param itemActualReceivable 项目应收
     * @param billItemActualReceivable 账单总应收
     * @return
     */
    public BigDecimal sharedAmount(BigDecimal receivedAmount, BigDecimal itemActualReceivable, BigDecimal billItemActualReceivable) {
        return itemActualReceivable.divide(billItemActualReceivable,
                        // 保留8位小数，后一位四舍五入
                        8, RoundingMode.HALF_UP)
                .multiply(receivedAmount)
                // 保留4位小数后截断
                .setScale(4, RoundingMode.DOWN);
    }
}
