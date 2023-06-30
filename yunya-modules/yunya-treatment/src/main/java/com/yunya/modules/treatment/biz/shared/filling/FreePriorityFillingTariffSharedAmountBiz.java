package com.yunya.modules.treatment.biz.shared.filling;

import com.yunya.feign.treatment.domain.vo.BillPayShareDetailVO;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.treatment.BillPayShareDetail;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 项目金额分摊：
 *  免单优先填充价目：
 *      1、免单金额优先往价目上填充，实收金额优先往商品上填充，
 *      2、当项目已填满则不参与分摊，
 *      2、当优先项目全部填充完仍有剩余时，则剩余金额去填充非优先项目
 * @author: chenlin
 * @date: 2023/4/6 9:50
 * @description:
 * @since: 1.0.0
 */
@Service
public class FreePriorityFillingTariffSharedAmountBiz extends AbstractFillingSharedAmountBiz {

    @Override
    public Collection<BillPayShareDetail> generateSharedDetails(BigDecimal thisFreeAmount, BigDecimal thisReceivedAmount, List<BillPayShareDetailVO> itemPayDetails, Date payDate) {
        int size = itemPayDetails.size();
        Map<Integer, BillPayShareDetail> result = new HashMap<>(size);
        // 【本次收费中的免单，本次收费中的实收，项目欠费，价目已收，商品已收】
        BigDecimal[] payment = {thisFreeAmount, thisReceivedAmount, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};

        // 优先填充
        for (int i=0,j=size-1; i < size; i++,j--) {
            BillPayShareDetailVO tariffDetail = itemPayDetails.get(i);
            BillPayShareDetail tariffShared = priorityFilling(tariffDetail, payment);
            computeIfAbsent(result, i, tariffShared, payDate);

            BillPayShareDetailVO oralDetail = itemPayDetails.get(j);
            BillPayShareDetail oralShared = priorityFilling(oralDetail, payment);
            computeIfAbsent(result, j, oralShared, payDate);
        }

        // 过剩填充
        for (int i=size-1; i >=0; i--) {
            BillPayShareDetail detail = residualFilling(itemPayDetails.get(i), payment);
            computeIfAbsent(result, i, detail, payDate);
        }

        return result.values();
    }

    /**
     * 费用过剩填充：当优先项目全部填充完仍有剩余时，则剩余金额去填充非优先项目
     *
     * @param detail
     * @param payment
     * @return
     */
    private BillPayShareDetail residualFilling(BillPayShareDetailVO detail, BigDecimal[] payment) {
        BigDecimal tariffRecTmp = payment[3];
        BigDecimal oralRecTmp = payment[4];
        Byte itemType = detail.getItemType();
        BigDecimal actualAmount = detail.getActualReceivable();
        BigDecimal totalReceived = detail.getItemRecAmount().add(detail.getItemFreeAmount());
        // 项目缺口(欠费)
        BigDecimal gap = actualAmount.subtract(totalReceived);
        if (StringHelper.eqZero(gap)) {
            // 统计已分摊的商品和价目总额
            if (itemType == 0) {
                payment[3] = tariffRecTmp.add(totalReceived);
            } else {
                payment[4] = oralRecTmp.add(totalReceived);
            }
            // 已填满的项目不再进行分摊
            return buildSharedDetail(detail, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        BigDecimal freeShared = BigDecimal.ZERO;
        BigDecimal receivedShared = BigDecimal.ZERO;
        payment[2] = gap;
        if (StringHelper.eq(tariffRecTmp, detail.getTariffActualAmount())
                && StringHelper.gtZero(payment[0])) {
            // 所有价目已填满且免单过剩，用剩余免单填充商品
            freeShared = sharedAmount(payment, 0);
        }
        if (StringHelper.eq(oralRecTmp, detail.getOralActualAmount())
                && StringHelper.gtZero(payment[1])) {
            // 所有商品已填满且实收过剩，用剩余实收填充价目
            receivedShared = sharedAmount(payment, 1);
        }
        detail.setItemRecAmount(detail.getItemRecAmount().add(receivedShared));
        detail.setItemFreeAmount(detail.getItemFreeAmount().add(freeShared));
        return buildSharedDetail(detail, freeShared, receivedShared);
    }

    /**
     * 费用优先填充：免单金额优先往价目上填充，实收金额优先往商品上填充，
     *
     * @param detail
     * @param payment
     * @return
     */
    @Override
    public BillPayShareDetail priorityFilling(BillPayShareDetailVO detail, BigDecimal[] payment) {
        Byte itemType = detail.getItemType();
        BigDecimal actualAmount = detail.getActualReceivable();
        BigDecimal totalReceived = detail.getItemRecAmount().add(detail.getItemFreeAmount());
        // 项目缺口(欠费)
        BigDecimal gap = actualAmount.subtract(totalReceived);
        if (StringHelper.eqZero(gap)) {
            // 已填满的项目不再进行分摊
            return buildSharedDetail(detail, BigDecimal.ZERO, BigDecimal.ZERO);
//            return null;
        }
        BigDecimal freeShared = BigDecimal.ZERO;
        BigDecimal receivedShared = BigDecimal.ZERO;
        payment[2] = gap;
        if (itemType == 0) {
            // 免单对价目缺口进行填充
            freeShared = sharedAmount(payment, 0);
        } else {
            // 实收对商品缺口进行填充
            receivedShared = sharedAmount(payment, 1);
        }
        detail.setItemRecAmount(detail.getItemRecAmount().add(receivedShared));
        detail.setItemFreeAmount(detail.getItemFreeAmount().add(freeShared));
        return buildSharedDetail(detail, freeShared, receivedShared);
    }

    @Deprecated
    public List<BillPayShareDetail> generateSharedDetails0(BigDecimal thisFreeAmount, BigDecimal thisReceivedAmount, Integer billPayId, List<BillPayShareDetailVO> itemPayDetails) {
        List<BillPayShareDetail> result = new ArrayList<>();
        BigDecimal[] payments = {thisFreeAmount, thisReceivedAmount, BigDecimal.ZERO};
        for (BillPayShareDetailVO detail : itemPayDetails) {
            BigDecimal actualAmount = detail.getActualReceivable();
            BigDecimal receivedAmount = detail.getItemRecAmount();
            // 项目欠费（缺口）
            BigDecimal gap = actualAmount.subtract(receivedAmount);
            if (StringHelper.eqZero(gap)) {
                // 已收满的不在进行分摊
                continue;
            }
            payments[3] = gap;
            // 免单填充缺口
            BigDecimal freeShared = sharedAmount(payments, 0);
            BigDecimal receivedShared = BigDecimal.ZERO;
            if (StringHelper.eqZero(payments[0])) {
                // 本次收费的免单部分已分摊完，分摊实收部分
                // 实收填充缺口
                receivedShared = sharedAmount(payments, 1);
            }
            BillPayShareDetail shareDetail = new BillPayShareDetail();
            shareDetail.setBillPayId(billPayId);
            shareDetail.setItemType(detail.getItemType());
            shareDetail.setItemId(detail.getItemId());
            shareDetail.setOrderRecordId(detail.getOrderRecordId());
            shareDetail.setFreeAmount(freeShared);
            shareDetail.setReceivedAmount(receivedShared);
//            shareDetail.setExecutorId(executorId);
//            shareDetail.setPayDate(payDate);
            result.add(shareDetail);
        }
        return result;
    }
}
