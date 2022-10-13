package com.yunya365.mini.mapper;

import com.yunya365.mini.entity.ExpertType;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ExpertTypeMapper extends Mapper<ExpertType> {

    void BatchInsert(List<ExpertType> List);

}