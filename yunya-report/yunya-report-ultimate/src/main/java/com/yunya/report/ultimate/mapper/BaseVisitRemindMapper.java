package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.VisitAndRemindCompletedInfoQuery;
import com.yunya.feign.report.domain.vo.VisitAndRemindCompletedInfoVO;
import com.yunya.models.report.BaseVisitRemind;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Collection;
import java.util.List;

public interface BaseVisitRemindMapper extends Mapper<BaseVisitRemind> {

  /**
   * 根据条件查询随访（提醒）完成信息
   *
   * @param query 查询条件
   * @return VisitAndRemindCompletedInfoVO
   */
  VisitAndRemindCompletedInfoVO selectVisitAndRemindCompletedInfo(
     @Param("query") VisitAndRemindCompletedInfoQuery query);

  /**
   * 根据patientId查询随访提醒列表
   *
   * @param patientIds
   * @param type
   * @param orgId
   * @return
   */
  List<BaseVisitRemind> findVisitRemindListInPatientId(Collection<Integer> patientIds, Integer type, Integer orgId);
}
