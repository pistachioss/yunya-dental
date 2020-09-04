package com.yunya.modules.treatment.biz;

import com.yunya.feign.tariff.domain.vo.BaseTariffHistoryVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.tariff.BaseTariffHistory;
import com.yunya.modules.treatment.mapper.BaseTariffHistoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 描述: 价目表变更记录业务层
 *
 * @author GaoLuding
 * @create 2020-05-20 10:07
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseTariffHistoryBiz extends BaseBiz<BaseTariffHistoryMapper, BaseTariffHistory> {

  /**
   * 根据价目表项目ID查询变更历史记录
   *
   * @param tariffId 价目表项目ID
   * @return
   */
  public List<BaseTariffHistoryVO> findList(Integer tariffId) {
    List<BaseTariffHistoryVO> resultList = mapper.selectBaseTariffHistoryList(tariffId);
    return resultList;
  }
}
