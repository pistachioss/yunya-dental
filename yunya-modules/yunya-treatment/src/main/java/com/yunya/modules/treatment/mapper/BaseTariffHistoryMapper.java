package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.vo.BaseTariffHistoryVO;
import com.yunya.models.tariff.BaseTariffHistory;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseTariffHistoryMapper extends Mapper<BaseTariffHistory> {

  /**
   * 根据基础价目表ID查询变更历史记录
   *
   * @param tariffId 基础价目表ID
   * @return
   */
  List<BaseTariffHistoryVO> selectBaseTariffHistoryList(@Param("tariffId") Integer tariffId);

  /**
   * 批量插入基础价目表变更记录列表
   *
   * @param list 基础价目表变更记录列表
   */
  void insertBaseTariffHistory(@Param("list") List<BaseTariffHistory> list);
}
