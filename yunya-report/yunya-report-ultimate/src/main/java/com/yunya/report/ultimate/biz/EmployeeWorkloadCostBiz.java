package com.yunya.report.ultimate.biz;

import cn.hutool.core.date.DateTime;
import com.yunya.feign.report.domain.model.EmployeeWorkloadCostModel;
import com.yunya.feign.report.domain.query.ClinicEmployeeWorkloadQuery;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.report.EmployeeWorkloadCost;
import com.yunya.report.ultimate.mapper.EmployeeWorkloadCostMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.PARAMETERS_IS_ILLEGAL;

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
    if (model.getDateType() != 1) {
      throw new ClientServiceException("按日查询不允许修改员工工作量费用！", PARAMETERS_IS_ILLEGAL);
    }
    if (!model.getEntryStartMonth().equals(model.getEntryEndMonth())) {
      throw new ClientServiceException("跨月查询不允许修改员工工作量费用！", PARAMETERS_IS_ILLEGAL);
    }
    EmployeeWorkloadCost employeeWorkloadCost = new EmployeeWorkloadCost();
    DateTime dateTime = new DateTime(model.getEntryStartMonth(), "yyyy-MM");
    Integer employeeId = model.getEmployeeId();
    employeeWorkloadCost.setOrgId(model.getOrgId());
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

  /**
   * 查询员工的加工费、正畸加工费、大额材料费
   *
   * @param query
   * @return
   */
  public List<EmployeeWorkloadCost> findClinicEmployeeWorkCost(ClinicEmployeeWorkloadQuery query) {
    return mapper.selectClinicEmployeeWorkCost(query);
  }
}
