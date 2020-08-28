package com.yunya.modules.treatment.mapper;

import com.yunya.models.treatment.BillRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;

public interface BillRecordMapper extends Mapper<BillRecord> {

  /**
   * 查询门诊某天最大的账单编号
   *
   * @param orgId 组织ID
   * @param date 日期
   * @return
   */
  String selectBillNumberByOrgId(@Param("orgId") Integer orgId, @Param("date") Date date);
}
