package com.yunya.modules.treatment.mapper;

import com.yunya.models.treatment.OrderRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;

public interface OrderRecordMapper extends Mapper<OrderRecord> {
  /**
   * 生成订单编号
   *
   * @param orgId 组织ID
   * @param date 开单日期
   * @return
   */
  String selectOrderNumberByOrgId(@Param("orgId") Integer orgId, @Param("date") Date date);
}
