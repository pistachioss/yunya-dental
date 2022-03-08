package com.yunya.modules.treatment.other.mapper;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.treatment_other.domain.form.ResetVisitingRemindForm;
import com.yunya.feign.treatment_other.domain.query.VisitingRemindQuery;
import com.yunya.feign.treatment_other.domain.vo.NextVisitingRecordVo;
import com.yunya.models.treatment_other.VisitingRecord;
import com.yunya.models.treatment_other.VisitingRemind;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

public interface VisitingRemindMapper extends Mapper<VisitingRemind> {
  /**
   * 根据患者id、提醒日期，提醒时间查询随访提醒
   *
   * @param patientId 患者id
   * @param date 提醒日期
   * @param time 提醒时间
   * @return 提醒列表
   */
  List<VisitingRemind> findVisitingRemindByPatientIdAndDateTime(
      @Param("patientId") Integer patientId, @Param("date") Date date, @Param("time") String time);

  /**
   * 根据条件查询随访提醒记录
   *
   * @param query 查询条件
   * @return 提醒列表
   */
  List<VisitingRemind> findVisitingRemindByCondition(@Param("query") VisitingRemindQuery query);

  /**
   * 查询患者后续提醒
   *
   * @param patientIds 患者ID列表
   * @param currentDate 查询日期
   * @return
   */
  List<NextVisitingRecordVo> countNextVisitingListByIds(
      @Param("patientIds") List<Integer> patientIds, @Param("currentDate") String currentDate);

  /**
   * 查询提醒数据
   * @return
   */
  List<VisitingRemind>selectList(PullForm pullForm);

  /**
   * 批量修改提醒时间
   * @param form
   * @return
   */
  Integer resetVisitingRemindBatch(@Param("userId") Integer userId,@Param("updName") String updName,@Param("form") ResetVisitingRemindForm form);

  Integer batchIntert(List<VisitingRemind>list);
}
