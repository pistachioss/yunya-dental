package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.query.BillAdjustRecordQuery;
import com.yunya.feign.treatment.domain.query.BillPayRecordAdjustQuery;
import com.yunya.feign.treatment.domain.query.BillTollRevokeRecordQuery;
import com.yunya.feign.treatment.domain.query.CurrentMonthBillAdjustQuery;
import com.yunya.feign.treatment.domain.vo.*;
import com.yunya.models.treatment.BillExceptionHandleRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BillExceptionHandleRecordMapper extends Mapper<BillExceptionHandleRecord> {

  /**
   * 根据就诊记录ID查询该就诊相关异常处理记录
   *
   * @param treatmentRecordId 就诊记录ID
   * @return List<BillHandleRecordVO>
   */
  List<BillHandleRecordVO> selectBillExceptionHandleRecord(
      @Param("treatmentRecordId") Integer treatmentRecordId);

  /**
   * 根据账单收费记录ID和操作类型查询上次账单异常处理记录ID
   *
   * @param handledRecordId 被处理记录ID
   * @param operateType 操作类型 (0-调整收费方式；1-撤销收费；2-调整账单；3-退费)
   * @return Integer
   */
  Integer selectPreExceptionHandleRecordId(
      @Param("handledRecordId") Integer handledRecordId, @Param("operateType") Byte operateType);

  /**
   * 根据条件查询账单调整记录列表
   *
   * @param query 查询条件
   * @return List<BillOfAdjustRecordVO>
   */
  List<BillOfAdjustRecordVO> selectBillAdjustRecord(@Param("query") BillAdjustRecordQuery query);

  /**
   * 根据条件查询账单撤销
   *
   * @param query 查询条件
   * @return List<BillOfTollRevokeRecordVO>
   */
  List<BillOfTollRevokeRecordVO> selectBillRevokeRecordList(
      @Param("query") BillTollRevokeRecordQuery query);

  /**
   * 根据条件查询调整入账方式记录
   *
   * @param query 查询条件
   * @return List<BillOfPayRecordAdjustVO>
   */
  List<BillOfPayRecordAdjustVO> selectBillPayAdjustRecordList(
      @Param("query") BillPayRecordAdjustQuery query);

  /**
   * 根据条件查询门诊当月账单调整记录
   *
   * @param query 查询条件
   * @return List<CurrentMonthAdjustBillVO>
   */
  List<CurrentMonthAdjustBillVO> selectCurrentMonthAdjustBill(
      @Param("query") CurrentMonthBillAdjustQuery query);
}
