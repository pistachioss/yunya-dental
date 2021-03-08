package com.yunya.report.ultimate.biz;

import cn.hutool.core.date.DateTime;
import com.yunya.feign.report.domain.model.EmployeeWorkloadCostModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.models.report.EmployeeWorkloadCost;
import com.yunya.report.ultimate.mapper.EmployeeWorkloadCostMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 简介: 员工工作量消耗成本业务层
 *
 * @author: chow
 * @date: 2020/11/3 20:32
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class EmployeeWorkloadCostBiz
    extends BaseBiz<EmployeeWorkloadCostMapper, EmployeeWorkloadCost> {

  /**
   * 保存员工工作量成本消耗
   *
   * @param model 工作量消耗成本模型
   */
  public void addOrModifyCost(EmployeeWorkloadCostModel model) {
    EmployeeWorkloadCost employeeWorkloadCost = new EmployeeWorkloadCost();
    DateTime dateTime = new DateTime(model.getEntryMonth(), "yyyy-MM");
    Integer employeeId = model.getEmployeeId();
    employeeWorkloadCost.setOrgId(Integer.valueOf(BaseContextHandler.getOrgId()));
    employeeWorkloadCost.setEntryMonth(dateTime);
    employeeWorkloadCost.setUserId(employeeId);
    EmployeeWorkloadCost result = mapper.selectOne(employeeWorkloadCost);
    employeeWorkloadCost.setProcessingFee(model.getProcessingFee());
    employeeWorkloadCost.setMaterialFee(model.getMaterialFee());
    employeeWorkloadCost.setBaseWorkload(model.getBaseWorkload());
    employeeWorkloadCost.setOrthodonticsFee(model.getOrthodonticsFee());
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    if (result != null) {
      employeeWorkloadCost.setId(result.getId());
      employeeWorkloadCost.setUpdId(userId);
      mapper.updateByPrimaryKeySelective(employeeWorkloadCost);
    } else {
      employeeWorkloadCost.setCrtId(userId);
      mapper.insertSelective(employeeWorkloadCost);
    }
  }
}
