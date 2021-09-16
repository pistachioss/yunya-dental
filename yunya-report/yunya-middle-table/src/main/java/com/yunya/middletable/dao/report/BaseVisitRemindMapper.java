package com.yunya.middletable.dao.report;

import com.yunya.models.report.BaseVisitRemind;
import com.yunya.models.treatment_other.VisitingRecord;
import com.yunya.models.treatment_other.VisitingRemind;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseVisitRemindMapper extends Mapper<BaseVisitRemind> {

    /**
     * 根据 操作id和类型进行删除
     * @param id 操作id
     * @param type 类型
     */
    void deleteByPrimaryKeyAndType(@Param("id") Integer id, @Param("type") Integer type);

    /**
     * 导入计划提醒时间
     * @return
     */
    int insertVisitingRemindTime(List<VisitingRemind>list);
    /**
     * 导入计划随访时间
     * @return
     */
    int insertVisitingRecordTime(List<VisitingRecord>list);

    /**
     * 批量插入
     * @param baseVisitReminds 集合
     */
    void insertList(@Param("list") List<BaseVisitRemind> baseVisitReminds);
}