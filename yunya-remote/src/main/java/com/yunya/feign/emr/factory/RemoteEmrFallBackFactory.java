package com.yunya.feign.emr.factory;

import com.yunya.feign.emr.RemoteEmrServiceFeign;
import com.yunya.feign.emr.domain.model.TreatPlanDetailWriteoffModel;

import javax.validation.Valid;
import java.util.List;

/**
 * @author xiangyang
 * @date 2020/8/31
 */
public class RemoteEmrFallBackFactory implements RemoteEmrServiceFeign {

    @Override
    public void recalculatePlanStatusById(Integer userId, List<Integer> planIds) {

    }

    @Override
    public void treatPlanWriteOffQunatity(@Valid TreatPlanDetailWriteoffModel models) {

    }
}
