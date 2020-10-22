package com.yunya.middletable.dao.report;

import com.yunya.models.middletable.BaseVisitRemind;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface BaseVisitRemindMapper extends Mapper<BaseVisitRemind> {

    /**
     * 根据 操作id和类型进行删除
     * @param id 操作id
     * @param type 类型
     */
    void deleteByPrimaryKeyAndType(@Param("id") Integer id, @Param("type") Integer type);
}