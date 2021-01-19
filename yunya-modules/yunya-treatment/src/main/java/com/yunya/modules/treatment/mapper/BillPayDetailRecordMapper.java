package com.yunya.modules.treatment.mapper;

import com.yunya.feign.clinic_base.domain.query.BusinessGoalCompletedInfoQuery;
import com.yunya.feign.patient_central.domain.query.CashReceiptOrRefundQuery;
import com.yunya.feign.treatment.domain.vo.BillPayDetailRecordVO;
import com.yunya.models.treatment.BillPayDetailRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;

public interface BillPayDetailRecordMapper extends Mapper<BillPayDetailRecord> {

  /**
   * 根据收费记录ID查询收费明细列表
   *
   * @param billPayRecordId 收费记录ID
   * @param inservice 是否有效
   * @return
   */
  List<BillPayDetailRecordVO> selectBillPayDetailRecord(
      @Param("billPayRecordId") Integer billPayRecordId, @Param("inservice") Boolean inservice);

  /**
   * 账单异常处理数据详情记录ID查询异常处理记录
   *
   * @param id 账单异常处理数据详情记录ID
   * @param inservice 是否有效
   * @return
   */
  BillPayDetailRecordVO selectPreBillPayDetailRecord(
      @Param("id") Integer id, @Param("inservice") Boolean inservice);

  /**
   * 根据支付方式统计账单的入账金额
   *
   * @param query
   * @return
   */
  BigDecimal sumBillPayAmount(@Param("query") CashReceiptOrRefundQuery query);

  /**
   * 根据条件查询门诊完成工作量
   *
   * @param query 查询条件
   * @return BigDecimal
   */
  BigDecimal selectBusinessWorkloadCompletedCount(
      @Param("query") BusinessGoalCompletedInfoQuery query);
}
