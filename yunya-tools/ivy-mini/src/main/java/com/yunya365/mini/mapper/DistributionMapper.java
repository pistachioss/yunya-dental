package com.yunya365.mini.mapper;

import com.yunya365.mini.entity.Distribution;
import tk.mybatis.mapper.common.Mapper;

public interface DistributionMapper extends Mapper<Distribution> {

    int deleteAll();
}