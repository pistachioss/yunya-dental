package com.yunya.modules.discount.mapper;

import com.yunya.models.discount.VoucherDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface VoucherDetailMapper extends Mapper<VoucherDetail> {
    /**
     * 批量新增
     *
     * @param voucherDetails
     */
    void batchInsert(@Param("list") List<VoucherDetail> voucherDetails);
}