package com.yunya.modules.treatment.mapper;

import com.yunya.feign.report.domain.query.CategoryIncomeQuery;
import com.yunya.feign.report.domain.vo.BillItemAmountSharedVO;
import com.yunya.feign.treatment.domain.query.BillPayShareDetailQuery;
import com.yunya.feign.treatment.domain.vo.BillPayShareDetailVO;
import com.yunya.models.treatment.BillPayShareDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BillPayShareDetailMapper extends Mapper<BillPayShareDetail> {
    void batchSave(List<BillPayShareDetail> shareDetails);

    /**
     * 根据条件逻辑删除
     *
     * @param orderRecordId
     * @param billPayId
     * @param orderDetailId
     */
    void tombstoneByCombinationKey(
            @Param("orderRecordId") Integer orderRecordId,
            @Param("billPayId") Integer billPayId,
            @Param("orderDetailId") Integer orderDetailId);

    /**
     * 根据条件物理删除
     *
     * @param query
     */
    void removeByBillDateRange(@Param("query") BillPayShareDetailQuery query);


    /**
     * 根据订单id查询订单在billPayId截止之前的项目收费明细
     *
     * @param orderRecordId
     * @param billPayId 查询条件同时传递值到返回列表中
     * @return
     */
    List<BillPayShareDetailVO> selectItemPayDetailDeadlineBillPayId(
            @Param("orderRecordId") Integer orderRecordId,
            @Param("billPayId") Integer billPayId);

    /**
     * 查询账单项目免单总额，包含撤销收费的和调整入账方式的账单
     * @param query
     * @return
     */
    List<BillItemAmountSharedVO> selectBillItemFreeAmountInRevoked(@Param("query") CategoryIncomeQuery query);
}