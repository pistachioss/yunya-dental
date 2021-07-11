package com.yunya.middletable.service.credits_shop;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: yunya-dental
 * @description: 账单支付记录回调
 * @author: LHB
 * @create: 2021-07-01 17:08
 **/
@Component
public interface BillCreditsCallback {
    /**
     * 收费完成，增加积分
     * @param billId 账单ID
     * @return
     */
    public void baseBillBizHandlerFinish(Integer billId);

    /**
     * 撤销收费积分处理逻辑
     * @param baseBillPayId 支付记录ID
     */
    public void scrapCredits(Integer baseBillPayId);

    /**
     * 退费积分处理逻辑
     * @param refundId 退费记录ID
     * @param billId  账单ID
     */
    public void refundCredits(Integer refundId,Integer billId);


}
