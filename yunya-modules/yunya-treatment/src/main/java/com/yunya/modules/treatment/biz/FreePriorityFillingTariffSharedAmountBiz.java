package com.yunya.modules.treatment.biz;

import com.yunya.feign.treatment.domain.vo.BillPayShareDetailVO;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.treatment.BillPayShareDetail;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
public class FreePriorityFillingTariffSharedAmountBiz implements IItemPaySharedAmount{

    @Override
    public BillPayShareDetail[] generateSharedDetails(BigDecimal thisFreeAmount, BigDecimal thisReceivedAmount, Integer billPayId, List<BillPayShareDetailVO> itemPayDetails, Integer optId) {
        Date now = new Date(System.currentTimeMillis());
        int size = itemPayDetails.size();
        BillPayShareDetail[] result = new BillPayShareDetail[size];
        // 【本次收费中的免单，本次收费中的实收，项目欠费，价目已收，商品已收】
        BigDecimal[] payment = {thisFreeAmount, thisReceivedAmount, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};

        // 优先填充
        for (int i=0,j=size-1; i < size; i++,j--) {
            BillPayShareDetailVO tariffDetail = itemPayDetails.get(i);
            BillPayShareDetailVO oralDetail = itemPayDetails.get(j);
            BillPayShareDetail tariffShared = priorityFilling(tariffDetail, payment);
            if (StringHelper.isNotNull(tariffShared)) {
                computeIfAbsent(result, i, tariffShared, billPayId, optId, now);
            }
            BillPayShareDetail oralShared = priorityFilling(oralDetail, payment);
            if (StringHelper.isNotNull(oralShared)) {
                computeIfAbsent(result, j, oralShared, billPayId, optId, now);
            }
        }

        // 过剩填充
        for (int j=size-1; j >=0; j--) {
            BillPayShareDetailVO itemPayDetail = itemPayDetails.get(j);
            BillPayShareDetail detail = residualFilling(itemPayDetail, payment);
            if (StringHelper.isNotNull(detail)) {
                computeIfAbsent(result, j, detail, billPayId, optId, now);
            }
        }

        return result;
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
        BigDecimal totalReceived = detail.getReceivedAmount().add(detail.getFreeAmount());
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
            return null;
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
        detail.setReceivedAmount(detail.getReceivedAmount().add(receivedShared));
        detail.setFreeAmount(detail.getFreeAmount().add(freeShared));
        BillPayShareDetail shareDetail = new BillPayShareDetail();
        shareDetail.setItemType(itemType);
        shareDetail.setItemId(detail.getItemId());
        shareDetail.setOrderRecordId(detail.getOrderRecordId());
        shareDetail.setFreeAmount(freeShared);
        shareDetail.setReceivedAmount(receivedShared);
        shareDetail.setInservice(true);
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
            shareDetail.setBillPayId(billPayId);
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
     * 费用优先填充：免单金额优先往价目上填充，实收金额优先往商品上填充，
     *
     * @param detail
     * @param payment
     * @return
     */
    private BillPayShareDetail priorityFilling(BillPayShareDetailVO detail, BigDecimal[] payment) {
        Byte itemType = detail.getItemType();
        BigDecimal actualAmount = detail.getActualReceivable();
        BigDecimal totalReceived = detail.getReceivedAmount().add(detail.getFreeAmount());
        // 项目缺口(欠费)
        BigDecimal gap = actualAmount.subtract(totalReceived);
        if (StringHelper.eqZero(gap)) {
            // 已填满的项目不再进行分摊
            return null;
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
        detail.setReceivedAmount(detail.getReceivedAmount().add(receivedShared));
        detail.setFreeAmount(detail.getFreeAmount().add(freeShared));
        BillPayShareDetail shareDetail = new BillPayShareDetail();
        shareDetail.setItemType(itemType);
        shareDetail.setItemId(detail.getItemId());
        shareDetail.setOrderRecordId(detail.getOrderRecordId());
        shareDetail.setFreeAmount(freeShared);
        shareDetail.setReceivedAmount(receivedShared);
        shareDetail.setInservice(true);
        return shareDetail;
    }

    /**
     * 金额分摊规则
     *
     * @param payments 本次收费列表
     * @param index 收费单元所在位置
     * @return
     */
    public BigDecimal sharedAmount(BigDecimal[] payments, Integer index) {
        BigDecimal thisPayAmount = payments[index];
        BigDecimal sharedAmount = BigDecimal.ZERO;
        BigDecimal gap = payments[2];
        BigDecimal filling = gap.subtract(thisPayAmount);
        if (StringHelper.geZero(filling)) {
            sharedAmount = thisPayAmount;
            thisPayAmount = BigDecimal.ZERO;
            // 缺口更新
            gap = filling;
        } else {
            sharedAmount = gap;
            // 填充后的剩余
            thisPayAmount = filling.abs();
            // 缺口更新
            gap = BigDecimal.ZERO;
        }
        payments[2] = gap;
        payments[index] = thisPayAmount;
        return sharedAmount;
    }


    @Deprecated
    public List<BillPayShareDetail> generateSharedDetails0(BigDecimal thisFreeAmount, BigDecimal thisReceivedAmount, Integer billPayId, List<BillPayShareDetailVO> itemPayDetails) {
        List<BillPayShareDetail> result = new ArrayList<>();
        Integer optId = Integer.parseInt(BaseContextHandler.getUserID());
        Date now = new Date(System.currentTimeMillis());
        BigDecimal[] payments = {thisFreeAmount, thisReceivedAmount, BigDecimal.ZERO};
        for (BillPayShareDetailVO detail : itemPayDetails) {
            BigDecimal actualAmount = detail.getActualReceivable();
            BigDecimal receivedAmount = detail.getReceivedAmount();
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
            shareDetail.setInservice(true);
            shareDetail.setCrtId(optId);
            shareDetail.setCrtTime(now);
            shareDetail.setUptId(optId);
            shareDetail.setUptTime(now);
            result.add(shareDetail);
        }
        return result;
    }
}
