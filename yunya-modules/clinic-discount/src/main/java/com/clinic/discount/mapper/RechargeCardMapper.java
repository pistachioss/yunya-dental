package com.clinic.discount.mapper;

import com.clinic.discount.entity.RechargeCard;
import com.clinic.discount.vo.DiscountVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

public interface RechargeCardMapper extends Mapper<RechargeCard> {
    /**
     * 查询列表
     *
     * @param marketProductTypeId
     * @param name
     * @param startDate
     * @param endDate
     * @return
     */
    List<DiscountVO> selectVOs(@Param("marketProductTypeId") Integer marketProductTypeId, @Param("name") String name,
                               @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}