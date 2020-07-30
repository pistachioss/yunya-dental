package com.clinic.discount.mapper;

import com.clinic.discount.entity.CardClinic;
import com.clinic.discount.vo.CardDistributionVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CardClinicMapper extends Mapper<CardClinic> {
    /**
     * 批量新增
     *
     * @param cardClinics
     */
    void batchInsert(@Param("list") List<CardClinic> cardClinics);

    /**
     * 获取优惠活动计划配给列表
     *
     * @param status
     * @param relevanceId
     * @return
     */
    List<CardDistributionVO> selectVOsByTypeAndRelevanceId(@Param("status") Integer status,
                                                           @Param("type") Integer type, @Param("relevanceId") Integer relevanceId);

    void updateByTypeAndRelevanceId(@Param("status") Integer status,
                                    @Param("type") Integer type, @Param("relevanceId") Integer relevanceId, @Param("revision") Integer revision);

    /**
     * 获取已经配给的总数
     *
     * @param type
     * @param relevanceId
     * @return
     */
    Integer selectSumCount(@Param("type") Integer type, @Param("relevanceId") Integer relevanceId);

    /**
     * 更新段号
     *
     * @param clinics
     */
    void batchUpdateNumber(@Param("list") List<CardClinic> clinics);
}