package com.yunya.modules.treatment.biz.shared.filling;

import com.yunya.feign.treatment.domain.vo.BillPayShareDetailVO;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.treatment.BillPayShareDetail;

import java.math.BigDecimal;
import java.util.*;

/**
 * 项目金额分摊：
 *  顺序填充：按项目顺序，填充完后填充下一个
 *
 * @author: chenlin
 * @date: 2023/4/6 9:50
 * @description:
 * @since: 1.0.0
 */
//@Service
public class ItemFillingSharedAmountBiz extends AbstractFillingSharedAmountBiz {

    @Override
    public Collection<BillPayShareDetail> generateSharedDetails(BigDecimal thisFreeAmount, BigDecimal thisReceivedAmount, List<BillPayShareDetailVO> itemPayDetails, Date payDate) {
        int size = itemPayDetails.size();
        Map<Integer, BillPayShareDetail> result = new HashMap<>(size);
        // 【本次收费中的免单，本次收费中的实收，项目欠费】
        BigDecimal[] payment = {thisFreeAmount, thisReceivedAmount, BigDecimal.ZERO};

        // 顺序填充
        for (int i=0; i < size; i++) {
            BillPayShareDetailVO tariffDetail = itemPayDetails.get(i);
            BillPayShareDetail tariffShared = priorityFilling(tariffDetail, payment);
            computeIfAbsent(result, i, tariffShared, payDate);
        }
        return result.values();
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
        BigDecimal actualAmount = detail.getActualReceivable();
        BigDecimal totalReceived = detail.getItemRecAmount().add(detail.getItemFreeAmount());
        // 项目缺口(欠费)
        BigDecimal gap = actualAmount.subtract(totalReceived);
        if (StringHelper.eqZero(gap)) {
            // 已填满的项目不再进行分摊
            return buildSharedDetail(detail, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        payment[2] = gap;
        BigDecimal freeShared = sharedAmount(payment, 0);
        BigDecimal receivedShared = BigDecimal.ZERO;
        if (StringHelper.leZero(payment[0])) {
            receivedShared = sharedAmount(payment, 1);
        }
        detail.setItemRecAmount(detail.getItemRecAmount().add(receivedShared));
        detail.setItemFreeAmount(detail.getItemFreeAmount().add(freeShared));
        return buildSharedDetail(detail, freeShared, receivedShared);
    }
}
