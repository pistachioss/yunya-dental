package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.models.report.BaseBill;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseBillMapper extends Mapper<BaseBill> {

  /**
   * 根据条件查询开单记录列表
   *
   * @param query 查询条件
   * @return List<BillOfOrderRecordVO>
   */
  List<BillOfOrderRecordVO> selectBillRecordOfOrderList(@Param("query") OrderRecordQuery query);

  /**
   * 欠费查询
   *
   * @param form 欠费查询
   * @param patientIds 患者id
   * @return List<ArrearsVo>
   */
  List<ArrearsVo> arrears(
      @Param("form") ArrearsQueryForm form, @Param("patientIds") List<Integer> patientIds);

  /**
   * 根据条件查询账单优惠明细列表
   *
   * @param query 查询条件
   * @return List<BillOfDiscountDetailVO>
   */
  List<BillOfDiscountDetailVO> selectBillDiscountDetailList(
      @Param("query") BillOfDiscountDetailQuery query);

  /**
   * 根据条件查询应收账款余额表
   *
   * @param query 查询条件
   * @return List<BillRestReceivableAmountVO>
   */
  List<BillRestReceivableAmountVO> selectBillReceivableAmountList(
      @Param("query") BillOfReceivableQuery query);

  ArrearsStatisticsVo selectArrears(@Param("orgId") Integer orgId);

  /**
   * 根据条件查询门诊账单数据总览
   *
   * @param query 查询条件
   * @return BillDataStatisticsVO
   */
  BillDataStatisticsVO selectClinicBillDataStatistic(@Param("query") DataStatisticsQuery query);
}
