package com.yunya.feign.clinic_base.factory;

import com.yunya.feign.clinic_base.RemoteClinicBaseServiceFeign;
import com.yunya.feign.clinic_base.domain.query.BusinessGoalCompletedInfoQuery;
import com.yunya.feign.clinic_base.domain.vo.BusinessGoalCompletedInfoVO;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2021/1/18 17:00
 * @description:
 * @since: 1.0.0
 */
public class RemoteClinicBaseServiceFactory implements RemoteClinicBaseServiceFeign {

    @Override
    public BusinessGoalCompletedInfoVO businessGoalCompletedInfo(BusinessGoalCompletedInfoQuery query) {
    return null;
    }
}