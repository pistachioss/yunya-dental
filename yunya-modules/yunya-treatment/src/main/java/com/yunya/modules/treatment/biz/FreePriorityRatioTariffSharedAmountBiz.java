package com.yunya.modules.treatment.biz;

import com.yunya.feign.treatment.domain.vo.BillPayShareDetailVO;
import com.yunya.framework.common.utils.BeanUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.treatment.BillPayShareDetail;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * 项目金额分摊：
 *  免单优先占比分摊价目：
 *      1、免单金额优先按价目占比分摊到各个价目上，实收金额优先按商品占比分摊到各个商品上，
 *      2、如果项目已填满则不参与分摊，
 *      4、当优先项目全部分摊满仍有剩余时，则剩余金额去分摊非优先项目
 * @author: chenlin
 * @date: 2023/4/6 10:02
 * @description:
 * @since: 1.0.0
 */
//@Service
public class FreePriorityRatioTariffSharedAmountBiz implements IItemPaySharedAmount{
    @Override
    public BillPayShareDetail[] generateSharedDetails(BigDecimal thisFreeAmount, BigDecimal thisReceivedAmount, Integer billPayId, List<BillPayShareDetailVO> itemPayDetails, Integer optId) {
        Date now = new Date(System.currentTimeMillis());
        int size = itemPayDetails.size();
        BillPayShareDetail[] result = new BillPayShareDetail[size];
        // 【本次收费中的免单，本次收费中的实收，免单剩余，价目剩余，当前已收免单、当前已收实体】
        BigDecimal[] payment = {thisFreeAmount, thisReceivedAmount, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};

        // 优先分摊
        for (int i=0,j=size-1; i < size; i++,j--) {
            BillPayShareDetailVO tariffDetail = itemPayDetails.get(i);
            BillPayShareDetail tariffShared = priorityRatio(tariffDetail, payment);
            if (StringHelper.isNotNull(tariffShared)) {
                computeIfAbsent(result, i, tariffShared, billPayId, optId, now);
            }
        }

        // 过剩分摊
        for (int j=size-1; j >=0; j--) {
            BillPayShareDetailVO itemPayDetail = itemPayDetails.get(j);
            BillPayShareDetail detail = residualRatio(itemPayDetail, payment);
            if (StringHelper.isNotNull(detail)) {
                computeIfAbsent(result, j, detail, billPayId, optId, now);
            }
        }

        return result;
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
            return null;
        }
        BigDecimal freeShared = BigDecimal.ZERO;
        BigDecimal receivedShared = BigDecimal.ZERO;
        if (StringHelper.eq(tariffRecTmp, detail.getTariffActualAmount())
                && StringHelper.gtZero(payment[2])) {
            // 所有价目已填满且免单过剩，用剩余免单对商品进行占比分摊
            freeShared = sharedAmount(payment, detail.getActualReceivable(), detail.getOralActualAmount(), 2);
        }
        if (StringHelper.eq(oralRecTmp, detail.getOralActualAmount())
                && StringHelper.gtZero(payment[3])) {
            // 所有商品已填满且实收过剩，用剩余实收对价目进行占比分摊
            receivedShared = sharedAmount(payment, detail.getActualReceivable(), detail.getTariffActualAmount(), 3);
        }
        detail.setItemRecAmount(detail.getItemRecAmount().add(receivedShared));
        detail.setItemFreeAmount(detail.getItemFreeAmount().add(freeShared));
        BillPayShareDetail shareDetail = new BillPayShareDetail();
        BeanUtil.copyProperties(detail, shareDetail);
        shareDetail.setFreeAmount(freeShared);
        shareDetail.setReceivedAmount(receivedShared);
        return shareDetail;
    }

    /**
     * 如果列表指定index上对象不存在，则将新对象添加，否则更新费用
     *
     * @param result 列表
     * @param index 位置
     * @param shareDetail 新对象
     * @param billPayId
     * @param optId
     * @param now
     */
    private void computeIfAbsent(BillPayShareDetail[] result, int index, BillPayShareDetail shareDetail, Integer billPayId, Integer optId, Date now) {
        BillPayShareDetail o = result[index];
        if (StringHelper.isNull(o)) {
            shareDetail.setCrtId(optId);
            shareDetail.setCrtTime(now);
            shareDetail.setUptId(optId);
            shareDetail.setUptTime(now);
            result[index] = shareDetail;
        } else {
            o.setFreeAmount(o.getFreeAmount().add(shareDetail.getFreeAmount()));
            o.setReceivedAmount(o.getReceivedAmount().add(shareDetail.getReceivedAmount()));
        }
    }

    /**
     * 费用优先分摊：免单金额优先往价目上进行占比分摊，实收金额优先往商品上进行占比分摊，
     *
     * @param detail
     * @param payment
     * @return
     */
    private BillPayShareDetail priorityRatio(BillPayShareDetailVO detail, BigDecimal[] payment) {
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
            return null;
        }
        BigDecimal freeShared = BigDecimal.ZERO;
        BigDecimal receivedShared = BigDecimal.ZERO;
        if (itemType == 0) {
            // 免单对价目进行分摊
            freeShared = sharedAmount(payment, detail.getActualReceivable(), detail.getTariffActualAmount(), 0);
        } else {
            // 实收对商品缺口进行分摊
            receivedShared = sharedAmount(payment, detail.getActualReceivable(), detail.getOralActualAmount(), 1);
        }
        detail.setItemRecAmount(detail.getItemRecAmount().add(receivedShared));
        detail.setItemFreeAmount(detail.getItemFreeAmount().add(freeShared));
        BillPayShareDetail shareDetail = new BillPayShareDetail();
        BeanUtil.copyProperties(detail, shareDetail);
        shareDetail.setFreeAmount(freeShared);
        shareDetail.setReceivedAmount(receivedShared);
        return shareDetail;
    }

    /**
     * 金额分摊规则
     *
     * @param payments 本次收费列表
     * @param actualReceivable 项目应收
     * @param typeActualReceivable 同项目类型下的总应收
     * @param index    收费单元所在位置
     * @return
     */
    public BigDecimal sharedAmount(BigDecimal[] payments, BigDecimal actualReceivable, BigDecimal typeActualReceivable, Integer index) {
        BigDecimal thisPayAmount = payments[index];
        BigDecimal surplus = thisPayAmount.subtract(typeActualReceivable);
        if (StringHelper.gtZero(surplus)) {
            thisPayAmount = typeActualReceivable;
            payments[index + 2] = surplus;
        }
        return actualReceivable.divide(typeActualReceivable, 8, RoundingMode.HALF_UP).multiply(thisPayAmount);
    }
}
