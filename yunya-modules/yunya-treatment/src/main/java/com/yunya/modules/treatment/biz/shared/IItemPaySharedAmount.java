package com.yunya.modules.treatment.biz.shared;

import com.yunya.feign.treatment.domain.vo.BillPayShareDetailVO;
import com.yunya.framework.common.utils.BeanUtil;
import com.yunya.models.treatment.BillPayShareDetail;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
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
     * @param thisFreeAmount     本次收费免单金额
     * @param thisReceivedAmount 本次收费实收金额
     * @param itemPayDetails     本次收费之前的项目的收费分摊明细列表
     * @param payDate
     * @return
     */
    Collection<BillPayShareDetail> generateSharedDetails(BigDecimal thisFreeAmount, BigDecimal thisReceivedAmount, List<BillPayShareDetailVO> itemPayDetails, Date payDate);

    /**
     * 构建分摊明细实体
     *
     * @param detail
     * @param freeShared
     * @param receivedShared
     * @return
     */
    default BillPayShareDetail buildSharedDetail(BillPayShareDetailVO detail, BigDecimal freeShared, BigDecimal receivedShared) {
        BillPayShareDetail shareDetail = new BillPayShareDetail();
        BeanUtil.copyProperties(detail, shareDetail);
        shareDetail.setFreeAmount(freeShared);
        shareDetail.setReceivedAmount(receivedShared);
        return shareDetail;
    }
}
