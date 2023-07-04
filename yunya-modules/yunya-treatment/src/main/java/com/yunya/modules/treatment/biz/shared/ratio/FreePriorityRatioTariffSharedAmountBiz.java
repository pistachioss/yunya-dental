package com.yunya.modules.treatment.biz.shared.ratio;

import com.yunya.feign.treatment.domain.vo.BillPayShareDetailVO;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.treatment.BillPayShareDetail;

import java.math.BigDecimal;
import java.util.*;

/**
 * 项目金额分摊：
 *  免单优先占比分摊价目：
 *      1、免单金额优先按价目占比分摊到各个价目上，实收金额优先按商品占比分摊到各个商品上，
 *      2、如果项目已填满则不参与分摊，
 *      4、当优先项目全部分摊满仍有剩余时，则剩余金额去分摊非优先项目
 *      耗时约：55070192 = 15.297h
 * @author: chenlin
 * @date: 2023/4/6 10:02
 * @description:
 * @since: 1.0.0
 */
//@Service
public class FreePriorityRatioTariffSharedAmountBiz extends AbstractRatioSharedAmountBiz {

    @Override
    public Collection<BillPayShareDetail> generateSharedDetails(BigDecimal thisFreeAmount, BigDecimal thisReceivedAmount, List<BillPayShareDetailVO> itemPayDetails, Date payDate) {
        int size = itemPayDetails.size();
        Map<Integer, BillPayShareDetail> result = new HashMap<>(size);
        // 【本次收费中的免单，本次收费中的实收，免单剩余（免单-商品应收），实收剩余（实收-价目应收），本次收费中已统计的免单、本次收费中已统计的实收】
        BigDecimal[] payment = {thisFreeAmount, thisReceivedAmount, thisFreeAmount, thisReceivedAmount, BigDecimal.ZERO, BigDecimal.ZERO};
        // 【本次收费中免单剩余，本次收费中实收剩余】
        BigDecimal[] surplus = {thisFreeAmount, thisReceivedAmount, BigDecimal.ZERO};

        // 优先分摊
        for (int i=0; i < size; i++) {
            BillPayShareDetail shareDetail = priorityRatio(itemPayDetails.get(i), payment);
            if (StringHelper.isNotNull(shareDetail)) {
                computeIfAbsent(result, shareDetail, payDate);
                surplus[0] = surplus[0].subtract(shareDetail.getFreeAmount());
                surplus[1] = surplus[1].subtract(shareDetail.getReceivedAmount());
            }
        }

        // 过剩分摊
        for (int i=size-1; i >=0; i--) {
            BillPayShareDetailVO detail = itemPayDetails.get(i);
            BillPayShareDetail shareDetail = residualRatio(detail, payment);
            if (StringHelper.isNotNull(shareDetail)) {
                computeIfAbsent(result, shareDetail, payDate);
                surplus[0] = surplus[0].subtract(shareDetail.getFreeAmount());
                surplus[1] = surplus[1].subtract(shareDetail.getReceivedAmount());
            }
        }

        return itemRefundSurplus(surplus, itemPayDetails, result);
    }

    /**
     * 如果列表指定index上对象不存在，则将新对象添加，否则更新费用
     *
     * @param result      列表
     * @param shareDetail 新对象
     * @param payDate
     */
    public void computeIfAbsent(Map<Integer, BillPayShareDetail> result, BillPayShareDetail shareDetail, Date payDate) {
        if (StringHelper.isNotNull(shareDetail)) {
            Integer orderDetailId = shareDetail.getOrderDetailId();
            BillPayShareDetail o = result.get(orderDetailId);
            if (StringHelper.isNull(o)) {
                shareDetail.setPayDate(payDate);
                result.put(orderDetailId, shareDetail);
            } else {
                o.setFreeAmount(o.getFreeAmount().add(shareDetail.getFreeAmount()));
                o.setReceivedAmount(o.getReceivedAmount().add(shareDetail.getReceivedAmount()));
            }
        }
    }

    /**
     * 费用过剩分摊：当优先项目全部填完仍有剩余时，则剩余金额去非优先项目进行占比分摊
     *
     * @param detail
     * @param payment
     * @return
     */
    private BillPayShareDetail residualRatio(BillPayShareDetailVO detail, BigDecimal[] payment) {
        BigDecimal tariffRecTmp = payment[4];
        BigDecimal oralRecTmp = payment[5];
        Byte itemType = detail.getItemType();
        BigDecimal actualAmount = detail.getActualReceivable();
        BigDecimal freeRecAmount = detail.getItemFreeAmount();
        BigDecimal receivedAmount = detail.getItemRecAmount();
        BigDecimal totalReceived = receivedAmount.add(freeRecAmount);
        // 项目缺口(欠费)
        BigDecimal gap = actualAmount.subtract(totalReceived);
        if (StringHelper.eqZero(gap)) {
            // 统计已分摊的商品和价目总额
            if (itemType == 0) {
                payment[4] = tariffRecTmp.add(totalReceived);
            } else {
                payment[5] = oralRecTmp.add(totalReceived);
            }
            // 已填满的项目不再进行分摊
            return buildSharedDetail(detail, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        BigDecimal freeShared = BigDecimal.ZERO;
        BigDecimal receivedShared = BigDecimal.ZERO;
        if (StringHelper.eq(tariffRecTmp, detail.getTariffActualAmount())
                && StringHelper.gtZero(payment[2])) {
            // 所有价目已填满且免单过剩，用剩余免单对商品进行占比分摊
            freeShared = sharedAmount(payment, gap, detail.getActualReceivable(), detail.getOralActualAmount(), 2);
        }
        if (StringHelper.eq(oralRecTmp, detail.getOralActualAmount())
                && StringHelper.gtZero(payment[3])) {
            // 所有商品已填满且实收过剩，用剩余实收对价目进行占比分摊
            receivedShared = sharedAmount(payment, gap, detail.getActualReceivable(), detail.getTariffActualAmount(), 3);
        }
        detail.setItemRecAmount(detail.getItemRecAmount().add(receivedShared));
        detail.setItemFreeAmount(detail.getItemFreeAmount().add(freeShared));
        return buildSharedDetail(detail, freeShared, receivedShared);
    }


    /**
     * 费用优先分摊：免单金额优先往价目上进行占比分摊，实收金额优先往商品上进行占比分摊，
     *
     * @param detail
     * @param payment
     * @return
     */
    @Override
    public BillPayShareDetail priorityRatio(BillPayShareDetailVO detail, BigDecimal[] payment) {
        Byte itemType = detail.getItemType();
        BigDecimal actualAmount = detail.getActualReceivable();
        BigDecimal totalReceived = detail.getItemRecAmount().add(detail.getItemFreeAmount());
        // 项目缺口(欠费)
        BigDecimal gap = actualAmount.subtract(totalReceived);
        if (StringHelper.eqZero(gap)) {
            // 费用接续
            payment[2] = payment[0];
            payment[3] = payment[1];
            // 已填满的项目不再进行分摊
            return buildSharedDetail(detail, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        BigDecimal freeShared = BigDecimal.ZERO;
        BigDecimal receivedShared = BigDecimal.ZERO;
        if (itemType == 0) {
            // 免单对价目进行分摊
            freeShared = sharedAmount(payment, gap, detail.getActualReceivable(), detail.getTariffActualAmount(), 0);
        } else {
            // 实收对商品缺口进行分摊
            receivedShared = sharedAmount(payment, gap, detail.getActualReceivable(), detail.getOralActualAmount(), 1);
        }
        detail.setItemRecAmount(detail.getItemRecAmount().add(receivedShared));
        detail.setItemFreeAmount(detail.getItemFreeAmount().add(freeShared));
        return buildSharedDetail(detail, freeShared, receivedShared);
    }

    /**
     * 金额分摊规则
     *
     * @param payments 本次收费列表
     * @param itemActualReceivable 项目应收
     * @param billItemActualReceivable 账单中价目or商品的总应收
     * @param index    收费单元所在位置
     * @return
     */
    public BigDecimal sharedAmount(BigDecimal[] payments, BigDecimal gap, BigDecimal itemActualReceivable, BigDecimal billItemActualReceivable, Integer index) {
        // 注意这里的payments[index]是收费，因为按占比分摊，所以这里不能分摊了一个项目后对更新其值。
        BigDecimal thisPayAmount = payments[index];
        BigDecimal surplus = thisPayAmount.subtract(billItemActualReceivable);
        if (StringHelper.gtZero(surplus)) {
            thisPayAmount = billItemActualReceivable;
            payments[index + 2] = surplus;
        }
        if (StringHelper.gt(thisPayAmount, gap)) {
            thisPayAmount = gap;
        }
        return sharedAmount(thisPayAmount, itemActualReceivable, billItemActualReceivable);
    }
}
