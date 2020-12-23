package com.yunya.modules.treatment.other.mapper;

import com.yunya.feign.treatment_other.domain.query.VisitingContentAfterCurrentQuery;
import com.yunya.feign.treatment_other.domain.query.VisitingRecordQuery;
import com.yunya.feign.treatment_other.domain.vo.NextVisitingRecordVo;
import com.yunya.feign.treatment_other.domain.vo.VisitingForMonthVo;
import com.yunya.feign.treatment_other.domain.vo.VisitingRecordVo;
import com.yunya.models.treatment_other.VisitingRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

public interface VisitingRecordMapper extends Mapper<VisitingRecord> {

    /**
     * 根据随访id查询随访记录（附带后续随访个数）
     * @return  随访记录
     */
    VisitingRecordVo findVisitingRecordById(@Param("id") Integer id);

    /**
     * 根据条件查询随访列表
     * @param query 查询条件
     * @return List<VisitingRecordVo>
     */
    List<VisitingRecordVo> findVisitingRecordByCondition(@Param("query") VisitingRecordQuery query);

    /**
     * 根据患者id、时间查询随访列表
     * @param query 查询参数
     * @return  随访列表
     */
    List<VisitingRecord> findAfterVisitingContentByPatientIdAndDate(
            @Param("query")VisitingContentAfterCurrentQuery query);

    /**
     * 根据就诊记录ID删除随访
     * @param treatmentId 就诊记录ID
     * @return
     */
    void deleteVisitingRecordByTreatmentId(@Param("treatmentId") Integer treatmentId);

    /**
     * 批量保存随访记录（就诊服务调用）
     * @param visitingRecords 实体对象列表
     */
    void insertEntitys(@Param("visitingRecords") List<VisitingRecord> visitingRecords);

    /**
     * 根据医生ID,开始时间，结束时间查询
     * @param dentistId 医生ID
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 实体列表
     */
    List<VisitingForMonthVo> findVisitingForMonth(
            @Param("dentistId") Integer dentistId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("orgId") Integer orgId);

    /**
     * 统计后续随访个数
     * @param patientId 患者ID
     * @param regDate 挂号时间
     * @return 返回统计个数
     */
    Integer countNextVisiting(@Param("patientId") Integer patientId,@Param("regDate") String regDate);

    /**
     * 根据患者ID集合查询患者后续随访信息列表
     * @param patientIds 患者ID接合
     * @param regDate  当前时间
     * @return 后续随访列表信息
     */
    List<NextVisitingRecordVo> countNextVisitingListByIds(@Param("patientIds") List<Integer> patientIds,
                                                          @Param("regDate") String regDate);

}