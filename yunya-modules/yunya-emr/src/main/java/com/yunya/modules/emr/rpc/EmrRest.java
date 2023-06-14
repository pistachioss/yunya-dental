package com.yunya.modules.emr.rpc;

import com.yunya.feign.emr.domain.model.TreatPlanDetailWriteoffModel;
import com.yunya.modules.emr.biz.MedicalOrthodonticsRecordBiz;
import com.yunya.modules.emr.biz.TreatPlanDetailBiz;
import com.yunya.modules.emr.biz.TreatPlanRecordBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

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
  @Autowired private TreatPlanDetailBiz treatPlanDetailBiz;
  @Resource
  private MedicalOrthodonticsRecordBiz medicalOrthodonticsRecordBiz;

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

  /**
   * 根据orderDetailIds查找计划详情与订单详情映射关系
   *
   * @param orderDetailIds
   */
  @PostMapping("/treatPlan/orderWithPlanDetail")
  public Map<Integer, List<Integer>> findOrderWithPlanDetailById(@RequestBody List<Integer> orderDetailIds) {
    return treatPlanDetailBiz.findOrderWithPlanDetailById(orderDetailIds);
  }

  @GetMapping("/Orthodontics/patient")
  public List<Integer> listOrthodonticsPatient() {
    return medicalOrthodonticsRecordBiz.listOrthodonticsPatient();
  }
}
