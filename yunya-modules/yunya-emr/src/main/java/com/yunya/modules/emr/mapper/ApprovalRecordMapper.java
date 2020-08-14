package com.yunya.modules.emr.mapper;


import com.yunya.models.emr.*;
import org.apache.ibatis.annotations.*;

import java.util.*;

@Mapper
public interface ApprovalRecordMapper extends tk.mybatis.mapper.common.Mapper<ApprovalRecord> {

    /**
     * 查询草稿病例是否已经存在审批记录
     * @param eventId
     * @return
     */
    int countDraftByEventId(@Param("eventId") Integer eventId);

    /**
     * 查询新增变更待审批数量
     * @param eventId eventId
     * @return count
     */
    int countToAuditChangeByEventId(@Param("eventId") Integer eventId);

    /**
     * 查询草稿审批历史
     * @param eventId
     * @return
     */
    ApprovalRecord findNewestDraft(@Param("eventId") Integer eventId);

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
                                          @Param("loginUserId") Integer loginUserId, @Param("eventType") Integer eventType,
                                          @Param("auditStatus") Integer auditStatus);

    /**
     * 根据草稿病例提交时间查询电子病例id集合
     * @param submitTime
     * @param loginUserId
     * @return
     */
    List<Integer> listMedicalIdsByCrtTime(@Param("submitTime") String submitTime, @Param("loginUserId") Integer loginUserId);

    /**
     * 查询草稿审批通过数量
     * @param eventId
     * @return
     */
    int countDraftPassByEventId(@Param("eventId") Integer eventId);

    /**
     * 查询草稿变更通过数量
     * @param eventId
     * @return
     */
    int countChangePassByEventId(@Param("eventId") Integer eventId);
}