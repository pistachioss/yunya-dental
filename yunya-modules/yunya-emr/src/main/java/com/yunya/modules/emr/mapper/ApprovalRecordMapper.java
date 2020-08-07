package com.yunya.modules.emr.mapper;


import com.yunya.models.emr.*;
import org.apache.ibatis.annotations.*;

import java.util.*;

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


    /**
     * 查询草稿审批历史
     * @param eventId
     * @return
     */
    ApprovalRecord findNewestReject(@Param("eventId") Integer eventId);

    /**
     * 查询新增病例变更申请的审批记录
     * @param treatmentId
     * @return
     */
    ApprovalRecord findTreatmentRecord(@Param("treatmentId") Integer treatmentId);

    /**
     * 查询修改病例变更申请的最新记录
     * @param medicalId
     * @return
     */
    ApprovalRecord findMedicalChangeRecord(@Param("medicalId") Integer medicalId);

    /**
     * 根据条件查询就诊审批记录
     * @param treatmentId
     * @param eventType
     * @param status
     * @return
     */
    int countByTreatmentId(@Param("treatmentId") Integer treatmentId, @Param("eventType") Integer eventType,
                              @Param("status") Integer status);

    /**
     * 根据条件查询病例审核数据集合
     * @param medicalIds
     * @param submitTime
     * @return
     */
    List<ApprovalRecord> listMedicalByParam(@Param("medicalIds") List<Integer> medicalIds, @Param("submitTime") String submitTime,
                                          @Param("loginUserId") Integer loginUserId);

    /**
     * 根据草稿病例提交时间查询电子病例id集合
     * @param submitTime
     * @param loginUserId
     * @return
     */
    List<Integer> listMedicalIdsByCrtTime(@Param("submitTime") String submitTime, @Param("loginUserId") Integer loginUserId);
}