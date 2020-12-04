package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.AssistantRefundDetailQuery;
import com.yunya.feign.report.domain.query.BillRefundRecordQuery;
import com.yunya.feign.report.domain.query.EmployeePersonalWorkloadDetailQuery;
import com.yunya.feign.report.domain.query.EmployeeRefundWorkloadDetailQuery;
import com.yunya.feign.report.domain.vo.AssistantRefundDetailVO;
import com.yunya.feign.report.domain.vo.BillOfRefundRecordVO;
import com.yunya.feign.report.domain.vo.EmployeePersonalRefundWorkloadDetailVO;
import com.yunya.feign.report.domain.vo.EmployeeRefundDetailWorkloadVO;
import com.yunya.models.report.BaseRefund;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseRefundMapper extends Mapper<BaseRefund> {

  /**
   * 根据条件查询账单退费记录列表
   *
   * @param query 查询条件
   * @return List<BillOfRefundRecordVO>
   */
  List<BillOfRefundRecordVO> selectBillRefundRecord(@Param("query") BillRefundRecordQuery query);

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
}
