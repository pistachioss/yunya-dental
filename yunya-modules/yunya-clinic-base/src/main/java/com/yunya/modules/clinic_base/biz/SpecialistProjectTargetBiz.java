package com.yunya.modules.clinic_base.biz;

import com.yunya.feign.clinic_base.domain.model.SpecialistProjectTargetModel;
import com.yunya.feign.clinic_base.domain.model.TargetOfMonthModel;
import com.yunya.feign.clinic_base.domain.query.SpecialistProjectTargetQuery;
import com.yunya.feign.clinic_base.domain.vo.TargetOfMonthVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.clinic_base.SpecialistBusinessTarget;
import com.yunya.modules.clinic_base.mapper.SpecialistBusinessTargetMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.yunya.framework.common.constant.OperationCodeConstants.PARAMETERS_IS_ILLEGAL;

/**
 * 简介: 专科数量管理业务层
 *
 * @author: chow
 * @date: 2020/12/23 10:59
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SpecialistProjectTargetBiz
    extends BaseBiz<SpecialistBusinessTargetMapper, SpecialistBusinessTarget> {

  /**
   * 根据条件查询专科数量目标
   *
   * @param query 查询条件
   * @return List<TargetOfMonthVO>
   */
  public List<TargetOfMonthVO> findSpecialistProjectTargetList(SpecialistProjectTargetQuery query) {
    List<TargetOfMonthVO> resultList = mapper.selectSpecialistProjectTargetList(query);
    return resultList;
  }

  /**
   * 保存专科数量目标
   *
   * @param model 新增模型
   */
  public void saveOrUpdate(SpecialistProjectTargetModel model) {
    Set<TargetOfMonthModel> monthModelList = model.getSpecialistProjectTargetOfMonthModels();
    if (StringHelper.isEmpty(monthModelList)) {
      throw new ClientServiceException("新增失败，月目标不能为空！", PARAMETERS_IS_ILLEGAL);
    }
    Byte belongType = model.getBelongType();
    Integer specialistProjectId = model.getSpecialistProjectId();
    String businessYear = model.getBusinessYear();
    String unit = model.getUnit();
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    int orgId = Integer.parseInt(BaseContextHandler.getOrgId());
    String name = BaseContextHandler.getName();
    SpecialistBusinessTarget specialistBusinessTarget = new SpecialistBusinessTarget();
    specialistBusinessTarget.setSpecialistProjectId(specialistProjectId);
    specialistBusinessTarget.setBelongType(belongType);
    specialistBusinessTarget.setBelongId(belongType == 0 ? orgId : userId);
    specialistBusinessTarget.setBusinessYear(businessYear);
    mapper.delete(specialistBusinessTarget);
    monthModelList.forEach(
        monthModel -> {
          Byte monthNum = monthModel.getMonthNum();
          if (null != monthNum) {
            SpecialistBusinessTarget target = new SpecialistBusinessTarget();
            target.setBelongType(belongType);
            target.setBelongId(belongType == 0 ? orgId : userId);
            target.setSpecialistProjectId(specialistProjectId);
            target.setBusinessGoal(monthModel.getBusinessGoal());
            target.setBusinessYear(businessYear);
            target.setBusinessMonth(monthNum.toString());
            String suffix = String.format("%02d", monthNum);
            target.setBusinessDate(businessYear + "-" + suffix);
            target.setUnit(unit);
            target.setCrtId(userId);
            target.setCrtName(name);
            target.setUpdId(userId);
            target.setUpdName(name);
            mapper.insertSelective(target);
          }
        });
  }
}
