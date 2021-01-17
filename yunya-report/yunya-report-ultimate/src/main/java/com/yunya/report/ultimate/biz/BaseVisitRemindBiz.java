package com.yunya.report.ultimate.biz;

import com.yunya.feign.report.domain.query.VisitAndRemindCompletedInfoQuery;
import com.yunya.feign.report.domain.vo.VisitAndRemindCompletedInfoVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.models.report.BaseVisitRemind;
import com.yunya.report.ultimate.mapper.BaseVisitRemindMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 简介: 随访提醒业务层
 *
 * @author: chow
 * @date: 2021/1/15 17:02
 * @description:
 * @since: 1.0.0
 */
@Service
public class BaseVisitRemindBiz extends BaseBiz<BaseVisitRemindMapper, BaseVisitRemind> {

  /**
   * 根据条件查询随访（提醒）完成信息
   *
   * @param query 查询条件
   * @return VisitAndRemindCompletedInfoVO
   */
  public VisitAndRemindCompletedInfoVO findVisitAndRemindCompletedInfo(
      VisitAndRemindCompletedInfoQuery query) {
    VisitAndRemindCompletedInfoVO resultData = mapper.selectVisitAndRemindCompletedInfo(query);
    Integer waitingForCompletedCount = resultData.getWaitingForCompletedCount();
    Integer completedCount = resultData.getCompletedCount();
    if (0 != waitingForCompletedCount) {
      resultData.setCompletedPercentage(
          BigDecimal.valueOf(completedCount / waitingForCompletedCount)
              .multiply(new BigDecimal(100))
              .setScale(2, BigDecimal.ROUND_HALF_UP));
    }
    return resultData;
  }
}
