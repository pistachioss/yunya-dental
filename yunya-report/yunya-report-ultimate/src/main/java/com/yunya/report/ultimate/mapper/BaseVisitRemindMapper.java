package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.VisitAndRemindCompletedInfoQuery;
import com.yunya.feign.report.domain.vo.VisitAndRemindCompletedInfoVO;
import com.yunya.models.report.BaseVisitRemind;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface BaseVisitRemindMapper extends Mapper<BaseVisitRemind> {

  /**
   * 根据条件查询随访（提醒）完成信息
   *
   * @param query 查询条件
   * @return VisitAndRemindCompletedInfoVO
   */
  VisitAndRemindCompletedInfoVO selectVisitAndRemindCompletedInfo(
     @Param("query") VisitAndRemindCompletedInfoQuery query);
}
