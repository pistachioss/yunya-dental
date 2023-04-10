package com.yunya.modules.treatment.biz;

import com.yunya.feign.treatment.domain.vo.BillPayShareDetailVO;
import com.yunya.models.treatment.BillPayShareDetail;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: chenlin
 * @date: 2023/4/4 18:00
 * @description:
 * @since: 1.0.0
 */
public interface IItemPaySharedAmount {

    /**
     * 生成项目金额分摊明细
     *
     * @param thisFreeAmount 本次收费免单金额
     * @param thisReceivedAmount 本次收费实收金额
     * @param billPayId 本次收费id
     * @param itemPayDetails 本次收费之前的项目的收费分摊明细列表
     * @param optId 本次收费操作人
     * @return
     */
    BillPayShareDetail[] generateSharedDetails(BigDecimal thisFreeAmount, BigDecimal thisReceivedAmount, Integer billPayId, List<BillPayShareDetailVO> itemPayDetails, Integer optId);
}
