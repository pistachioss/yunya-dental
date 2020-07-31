package com.yunya.modules.discount.mapper;

import com.yunya.models.discount.Voucher;
import com.yunya.modules.discount.vo.DiscountVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

public interface VoucherMapper extends Mapper<Voucher> {
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