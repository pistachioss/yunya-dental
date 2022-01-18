package com.yunya.feign.emr;

import com.yunya.feign.emr.domain.model.TreatPlanDetailWriteoffModel;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@FeignClient(name = YunyaServiceNameConstants.YUNYA_EMR)
public interface RemoteEmrServiceFeign {

    @GetMapping("/api/emr/treatPlan/recalculate/{planId}")
    void recalculatePlanStatusById(@PathVariable(value = "planId") Integer planId, @RequestParam("userId") Integer userId);

    /**
     * 生成治疗计划核销表数据
     *
     * @param models
     */
    @PostMapping("/api/emr/writeOffQuantity")
    void treatPlanWriteOffQunatity(@RequestBody @Valid TreatPlanDetailWriteoffModel models);
}
