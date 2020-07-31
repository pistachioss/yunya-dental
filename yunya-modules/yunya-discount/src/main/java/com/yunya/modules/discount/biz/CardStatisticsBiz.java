package com.yunya.modules.discount.biz;

import com.yunya.models.discount.CardStatistics;
import com.yunya.modules.discount.mapper.CardStatisticsMapper;
import com.yunya.modules.discount.vo.CardStatisticsVO;
import com.yunya.framework.common.biz.BaseBiz;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 描述:
 *
 * @author GaoLuding
 * @create 2020-07-20 13:28
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CardStatisticsBiz extends BaseBiz<CardStatisticsMapper, CardStatistics> {

    /**
     * 根据名称活类型获取列表
     *
     * @param name
     * @param types
     * @return
     */
    List<CardStatisticsVO> getVOByNameAndTypes(String name, List<Integer> types) {
        return mapper.selectVOByNameAndTypes(name, types);
    }
}
