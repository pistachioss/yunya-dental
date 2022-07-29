package com.yunya365.mini.mapper;


import com.yunya365.mini.entity.recommed;
import tk.mybatis.mapper.common.Mapper;

public interface RecommedMapper extends Mapper<recommed> {
    int deleteAll();
}