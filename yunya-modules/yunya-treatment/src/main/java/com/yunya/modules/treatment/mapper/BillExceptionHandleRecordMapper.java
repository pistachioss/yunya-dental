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
}
