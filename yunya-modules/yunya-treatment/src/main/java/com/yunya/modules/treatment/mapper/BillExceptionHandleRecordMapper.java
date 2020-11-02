package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.vo.BillHandleRecordVO;
import com.yunya.models.treatment.BillExceptionHandleRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BillExceptionHandleRecordMapper extends Mapper<BillExceptionHandleRecord> {
  /**
   * 根据就诊记录ID查询该就诊相关异常处理记录
   *
   * @param treatmentRecordId 就诊记录ID
   * @return
   */
  List<BillHandleRecordVO> selectBillExceptionHandleRecord(
      @Param("treatmentRecordId") Integer treatmentRecordId);

  /**
   * 根据账单收费记录ID和操作类型查询上次账单异常处理记录ID
   * @param billPayRecordId 账单收费记录ID
   * @param operateType 操作类型
   * @return
   */
  Integer selectPreExpectionHandleRecordId(@Param("billPayRecordId") Integer billPayRecordId, @Param("operateType") Byte operateType);
}
