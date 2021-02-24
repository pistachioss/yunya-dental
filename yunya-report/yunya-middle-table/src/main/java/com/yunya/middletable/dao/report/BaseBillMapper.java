package com.yunya.middletable.dao.report;

import com.yunya.models.report.BaseBill;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Set;

public interface BaseBillMapper extends Mapper<BaseBill> {

  /**
   * 批量插入中间表账单记录
   *
   * @param baseBills 账单列表
   */
  void batchInsertSelective(@Param("baseBills") Set<BaseBill> baseBills);
}
