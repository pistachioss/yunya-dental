package com.yunya.modules.tariff.mapper;

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
}
