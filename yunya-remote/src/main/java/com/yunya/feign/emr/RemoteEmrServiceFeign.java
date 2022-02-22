package com.yunya.feign.emr;

import com.yunya.feign.emr.domain.model.TreatPlanDetailWriteoffModel;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@FeignClient(name = YunyaServiceNameConstants.YUNYA_EMR)
public interface RemoteEmrServiceFeign {

    @PostMapping("/api/emr/treatPlan/recalculate/{userId}")
    void recalculatePlanStatusById(@PathVariable(value = "userId") Integer userId, @RequestBody List<Integer> planIds);

    /**
     * 生成治疗计划核销表数据
     *
     * @param models
     */
    @PostMapping("/api/emr/treatPlan/writeOffQuantity")
    void treatPlanWriteOffQunatity(@RequestBody @Valid TreatPlanDetailWriteoffModel models);

    /**
     * 根据orderDetailIds查找计划详情与订单详情映射关系
     *
     * @param orderDetailIds
     */
    @PostMapping("/api/emr/treatPlan/orderWithPlanDetail")
    Map<Integer, List<Integer>> findOrderWithPlanDetailById(@RequestBody List<Integer> orderDetailIds);
}
