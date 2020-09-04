package com.yunya.modules.tariff.mapper;

import com.yunya.feign.treatment.domain.vo.BaseOralTariffHistoryVO;
import com.yunya.models.tariff.BaseOralTariffHistory;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseOralTariffHistoryMapper extends Mapper<BaseOralTariffHistory> {

  /**
   * 根据商品项目ID查询变更历史记录
   *
   * @param oralTariffId 商品项目ID
   * @return
   */
  List<BaseOralTariffHistoryVO> selectBaseOralTariffHistoryList(
      @Param("oralTariffId") Integer oralTariffId);
}
