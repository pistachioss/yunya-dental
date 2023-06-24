package com.yunya.modules.treatment.biz.shared.filling;

import com.yunya.feign.treatment.domain.vo.BillPayShareDetailVO;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.treatment.BillPayShareDetail;
import com.yunya.modules.treatment.biz.shared.IItemPaySharedAmount;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author: chenlin
 * @date: 2023/6/21 11:28
 * @description:
 * @since: 1.0.0
 */
public abstract class AbstractFillingSharedAmountBiz implements IItemPaySharedAmount {

    protected abstract BillPayShareDetail priorityFilling(BillPayShareDetailVO detail, BigDecimal[] payment);

    /**
     * 金额分摊规则
     *
     * @param payments 本次收费列表
     * @param index 收费单元所在位置
     * @return
     */
    public BigDecimal sharedAmount(BigDecimal[] payments, Integer index) {
        BigDecimal thisPayAmount = payments[index];
        BigDecimal sharedAmount;
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
        return StringHelper.defaultBigDecimal(sharedAmount);
    }

    /**
     * 如果列表指定index上对象不存在，则将新对象添加，否则更新费用
     *
     * @param result      列表
     * @param index       位置
     * @param shareDetail 新对象
     * @param payDate
     */
    public void computeIfAbsent(Map<Integer, BillPayShareDetail> result, int index, BillPayShareDetail shareDetail, Date payDate) {
        if (StringHelper.isNotNull(shareDetail)) {
            BillPayShareDetail o = result.get(index);
            if (StringHelper.isNull(o)) {
                shareDetail.setPayDate(payDate);
                result.put(index, shareDetail);
            } else {
                o.setFreeAmount(o.getFreeAmount().add(shareDetail.getFreeAmount()));
                o.setReceivedAmount(o.getReceivedAmount().add(shareDetail.getReceivedAmount()));
            }
        }
    }
}
