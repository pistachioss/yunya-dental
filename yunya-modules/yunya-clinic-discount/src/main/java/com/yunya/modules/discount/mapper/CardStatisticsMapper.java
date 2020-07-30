package com.yunya.modules.discount.mapper;

import com.yunya.models.discount.CardStatistics;
import com.yunya.modules.discount.vo.CardStatisticsVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CardStatisticsMapper extends Mapper<CardStatistics> {
    /**
     * 获取VO列表
     *
     * @param name
     * @param types
     * @return
     */
    List<CardStatisticsVO> selectVOByNameAndTypes(@Param("name") String name, @Param("list") List<Integer> types);
}