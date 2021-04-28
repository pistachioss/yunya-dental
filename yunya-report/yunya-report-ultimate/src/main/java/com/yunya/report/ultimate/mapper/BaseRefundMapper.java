package com.yunya.report.ultimate.mapper;

import com.yunya.feign.patient_central.domain.query.ReceiverkLoadQuery;
import com.yunya.feign.patient_central.domain.vo.web.ReceivedWorkloadDetailsVo;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.models.report.BaseRefund;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;

public interface BaseRefundMapper extends Mapper<BaseRefund> {

  /**
   * 根据条件查询账单退费记录列表
   *
   * @param query 查询条件
   * @return List<BillOfRefundRecordVO>
   */
  List<BillOfRefundRecordInfoVO> selectBillRefundRecord(
      @Param("query") BillRefundRecordQuery query);

  /**
   * 根据条件查询员工退费工作量明细列表
   *
   * @param query 查询条件
   * @return List<EmployeePersonalRefundWorkloadDetailVO>
   */
  List<EmployeePersonalRefundWorkloadDetailVO> selectEmployeePersonalRefundWorkloadDetail(
      @Param("query") EmployeePersonalWorkloadDetailQuery query);

  /**
   * 根据条件查询员工退费工作量退费明细列表
   *
   * @param query 查询条件
   * @return List<EmployeeRefundDetailWorkloadVO>
   */
  List<EmployeeRefundDetailWorkloadVO> selectEmployeeRefundOrderDetailList(
      @Param("query") EmployeeRefundWorkloadDetailQuery query);

  /**
   * 根据条件查询助手配诊退费账单明细列表
   *
   * @param query 查询条件
   * @return List<AssistantActualWorkloadDetailVO>
   */
  List<AssistantRefundDetailVO> selectAssistantRefundDetailList(
      @Param("query") AssistantRefundDetailQuery query);

  /**
   * 根据条件查询门诊账单退费（当月）明细列表
   *
   * @param query 查询条件
   * @return List<StatementBillRefundDetailVO>
   */
  List<StatementBillRefundDetailVO> selectCurrentBillRefundDetailList(
      @Param("query") StatementBillRefundDetailInfoQuery query);

  /**
   * 根据条件查询门诊账单退费（非当月）明细列表
   *
   * @param query 查询条件
   * @return List<StatementBillRefundDetailVO>
   */
  List<StatementBillRefundDetailVO> selectOtherBillRefundDetailList(
      @Param("query") StatementBillRefundDetailInfoQuery query);

  /**
   * 根据条件查询退费工作量
   *
   * @param query 查询条件
   * @return BigDecimal
   */
  BigDecimal selectTotalRefundWorkload(@Param("query") DataStatisticsQuery query);

  /**
   * 根据推荐人id和退费时间查询患者退款总金额
   * @param originId 推荐人id
   * @param startDate 开始时间
   * @param endDate 结束时间
   * @param originType 推荐类型
   * @return 退款总金额
   */
    BigDecimal findRefundAmount(@Param("originId") Integer originId,@Param("startDate") String startDate,@Param("endDate") String endDate,@Param("originType") Integer originType);


  /**
   * 根据推荐人id 和 退费时间查询 退费id
   * @param query 条件
   * @return 退费id 集合
   */
  List<Integer> selectFundBillIdList(@Param("query") ReceiverkLoadQuery query);

  /**
   * 查询退费项目明细
   * @param refundId 退费id
   * @param originId 推荐人id
   * @param originType 推荐类型
   * @return 查询退费项目明细
   */
  List<ReceivedWorkloadDetailsVo> selectRefundDetail(@Param("refundId") Integer refundId,@Param("originId") Integer originId,@Param("originType") Integer originType);

  List<BillOfRefundWorkloadVO> groupTotalRefundWorkload(@Param("query") DataStatisticsQuery query);

  /**
   * 根据条件按月份分组求退费工作量
   *
   * @param query
   * @return
   */
  List<BillOfRefundWorkloadVO> selectTotalRefundWorkloadGroupByMonth(@Param("query") DataStatisticsQuery query);
}
