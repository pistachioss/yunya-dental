package com.yunya.modules.emr.rpc;

import com.yunya.feign.emr.domain.model.TreatPlanDetailWriteoffModel;
import com.yunya.modules.emr.biz.TreatPlanRecordBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 简介: 门诊基础数据接口暴露
 *
 * @author: chow
 * @date: 2021/1/18 16:51
 * @description:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("/api/emr")
public class EmrRest {

  @Autowired private TreatPlanRecordBiz treatPlanRecordBiz;

  @PostMapping("/treatPlan/recalculate/{userId}")
  public void recalculatePlanStatusById(@PathVariable(value = "userId") Integer userId, @RequestBody List<Integer> planIds) {
    treatPlanRecordBiz.recalculatePlanStatusById(userId, planIds);
  }

  /**
   * 生成治疗计划核销表数据
   *
   * @param model
   */
  @PostMapping("/treatPlan/writeOffQuantity")
  public void treatPlanWriteOffQunatity(@RequestBody @Valid TreatPlanDetailWriteoffModel model) {
    treatPlanRecordBiz.treatPlanDetailWriteoffQunatity(model);
  }
}
