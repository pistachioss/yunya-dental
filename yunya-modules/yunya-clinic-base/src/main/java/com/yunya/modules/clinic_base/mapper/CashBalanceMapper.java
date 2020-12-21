package com.yunya.modules.clinic_base.mapper;

import com.yunya.feign.clinic_base.domain.query.CashBalanceQuery;
import com.yunya.feign.clinic_base.domain.vo.CashBalanceVO;
import com.yunya.models.clinic_base.CashBalance;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CashBalanceMapper extends Mapper<CashBalance> {

  /**
   * 查询最近的一条现金结存记录
   *
   * @return CashBalance
   * @param orgId 组织ID
   */
  CashBalance selectRecentCashBalance(@Param("orgId") Integer orgId);

  /**
   * 根据条件查询现金结存列表
   *
   * @param query 查询条件
   * @return List<CashBalanceVO>
   */
  List<CashBalanceVO> selectCashBalanceList(@Param("query") CashBalanceQuery query);
}
