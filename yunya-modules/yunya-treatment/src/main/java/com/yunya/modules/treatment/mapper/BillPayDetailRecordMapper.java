package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.vo.BillPayDetailRecordVO;
import com.yunya.models.treatment.BillPayDetailRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BillPayDetailRecordMapper extends Mapper<BillPayDetailRecord> {

  /**
   * 根据收费记录ID查询收费明细列表
   *
   * @param billPayRecordId 收费记录ID
   * @return
   */
  List<BillPayDetailRecordVO> selectBillPayDetailRecord(
      @Param("billPayRecordId") Integer billPayRecordId);
}
