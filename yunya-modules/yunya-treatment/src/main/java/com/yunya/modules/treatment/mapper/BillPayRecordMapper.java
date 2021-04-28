package com.yunya.modules.treatment.mapper;

import com.yunya.feign.report.domain.query.CurrentMonthBillInfoQuery;
import com.yunya.feign.report.domain.vo.CurrentMonthBillCollectionDebtVO;
import com.yunya.feign.report.domain.vo.CurrentMonthBillPayRecordVO;
import com.yunya.models.treatment.BillPayRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BillPayRecordMapper extends Mapper<BillPayRecord> {

  /**
   * 根据账单记录ID查询最早的账单支付记录
   *
   * @param billRecordId 账单记录ID
   * @return
   */
  BillPayRecord selectEarliestBillPayRecord(@Param("billPayRecordId") Integer billRecordId);

  /**
   * 根据条件导出门诊本月收欠费（使用优惠）账单列表
   *
   * @param query 查询条件
   * @return list
   */
  List<CurrentMonthBillCollectionDebtVO> selectCurrentMonthBillCollectionDebtList(
      @Param("query") CurrentMonthBillInfoQuery query);

  /**
   * 根据条件查询本门诊本月收费记录列表
   *
   * @param query 查询条件
   * @return list
   */
  List<CurrentMonthBillPayRecordVO> selectCurrentMonthBillPayRecord(
      @Param("query") CurrentMonthBillInfoQuery query);
}
