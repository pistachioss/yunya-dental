package com.yunya.modules.tariff.biz;

import com.yunya.feign.treatment.domain.vo.BaseOralTariffHistoryVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.tariff.BaseOralTariffHistory;
import com.yunya.modules.tariff.mapper.BaseOralTariffHistoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 描述: 商品项目变更记录业务层
 *
 * @author GaoLuding
 * @create 2020-05-20 11:36
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseOralTariffHistoryBiz
    extends BaseBiz<BaseOralTariffHistoryMapper, BaseOralTariffHistory> {

  /**
   * 根据商品项目ID查询变更历史记录
   *
   * @param oralTariffId 商品项目ID
   * @return
   */
  public List<BaseOralTariffHistoryVO> findList(Integer oralTariffId) {
    List<BaseOralTariffHistoryVO> resultList = mapper.selectBaseOralTariffHistoryList(oralTariffId);
    return resultList;
  }
}
