package com.clinic.discount.mapper;

import com.clinic.discount.entity.CardStatistics;
import com.clinic.discount.vo.CardStatisticsVO;
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