package com.yunya.modules.emr.mapper;


import com.yunya.models.emr.ApprovalRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ApprovalRecordMapper extends tk.mybatis.mapper.common.Mapper<ApprovalRecord> {

    /**
     * 根据条件查询审批记录
     * @param eventId
     * @param eventType
     * @param status
     * @return
     */
    int countByEventIdAndType(@Param("eventId") Integer eventId, @Param("eventType") Integer eventType,
                             @Param("status") Integer status);
}