package com.yunya.modules.sms.mapper;

import com.yunya.feign.sms.vo.SmsOrgStatisticsVO;
import com.yunya.models.sms.SmsOrgStatistics;
import tk.mybatis.mapper.common.Mapper;

public interface SmsOrgStatisticsMapper extends Mapper<SmsOrgStatistics> {
    /**
     * 根据orgId查询短信统计详情
     *
     * @param orgId 门诊id
     * @return
     */
    SmsOrgStatisticsVO findSmsOrgStatisticsByOrgId(Integer orgId);
}