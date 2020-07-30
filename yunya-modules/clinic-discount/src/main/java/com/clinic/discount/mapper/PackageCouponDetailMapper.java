package com.clinic.discount.mapper;

import com.clinic.discount.entity.PackageCouponDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PackageCouponDetailMapper extends Mapper<PackageCouponDetail> {
    /**
     * 批量新增
     *
     * @param packageCouponDetails
     */
    void batchInsert(@Param("list") List<PackageCouponDetail> packageCouponDetails);
}