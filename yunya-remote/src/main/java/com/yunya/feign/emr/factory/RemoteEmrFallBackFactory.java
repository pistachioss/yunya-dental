package com.yunya.feign.emr.factory;

import com.yunya.feign.emr.RemoteEmrServiceFeign;
import com.yunya.feign.emr.domain.model.TreatPlanDetailWriteoffModel;

import javax.validation.Valid;

/**
 * @author xiangyang
 * @date 2020/8/31
 */
public class RemoteEmrFallBackFactory implements RemoteEmrServiceFeign {

    @Override
    public void recalculatePlanStatusById(Integer planId, Integer crtId) {

    }

    @Override
    public void treatPlanWriteOffQunatity(@Valid TreatPlanDetailWriteoffModel models) {

    }
}
