package com.yunya.middletable.dao.treatment;

import com.yunya.feign.treatment_other.domain.query.VisitingRemindQuery;
import com.yunya.models.treatment_other.VisitingRemind;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

@Repository
public interface VisitingRemindMapper extends Mapper<VisitingRemind> {
    /**
     * 根据患者id、提醒日期，提醒时间查询随访提醒
     * @param patientId 患者id
     * @param date 提醒日期
     * @param time 提醒时间
     * @return 提醒列表
     */
    List<VisitingRemind> findVisitingRemindByPatientIdAndDateTime(
            @Param("patientId") Integer patientId,
            @Param("date") Date date,
            @Param("time") String time);

    /**
     * 根据条件查询随访提醒记录
     * @param query 查询条件
     * @return 提醒列表
     */
    List<VisitingRemind> findVisitingRemindByCondition(@Param("query") VisitingRemindQuery query);
}