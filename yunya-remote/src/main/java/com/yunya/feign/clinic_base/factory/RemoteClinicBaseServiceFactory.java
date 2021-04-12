package com.yunya.feign.clinic_base.factory;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.clinic_base.RemoteClinicBaseServiceFeign;
import com.yunya.feign.clinic_base.domain.query.BusinessGoalCompletedInfoQuery;
import com.yunya.feign.clinic_base.domain.query.SpecialistProjectQuery;
import com.yunya.feign.clinic_base.domain.vo.BusinessGoalCompletedInfoVO;
import com.yunya.feign.clinic_base.domain.vo.BusinessGoalVO;
import com.yunya.feign.clinic_base.domain.vo.SpecialistProjectTargetVO;
import com.yunya.feign.clinic_base.domain.vo.SpecialistProjectVO;

import java.util.List;

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

    @Override
    public List<BusinessGoalVO> businessGoalList(BusinessGoalCompletedInfoQuery query) {
        return null;
    }

    @Override
    public List<SpecialistProjectTargetVO> specialProjectAndGoalsList(Byte dateType, List<String> dateRange, Integer[] orgIds) {
        return null;
    }

    @Override
    public PageInfo<SpecialistProjectVO> specialProjectList(SpecialistProjectQuery query) {
        return null;
    }
}