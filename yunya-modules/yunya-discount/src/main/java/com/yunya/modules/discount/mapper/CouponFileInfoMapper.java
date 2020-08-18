package com.yunya.modules.discount.mapper;

import com.yunya.models.discount.CouponFileInfo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CouponFileInfoMapper extends Mapper<CouponFileInfo> {

   int insertAll(List<CouponFileInfo> list);

}