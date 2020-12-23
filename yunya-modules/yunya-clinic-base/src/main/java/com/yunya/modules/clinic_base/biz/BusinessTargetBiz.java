package com.yunya.modules.clinic_base.biz;

import com.yunya.feign.clinic_base.domain.model.BusinessTargetModel;
import com.yunya.feign.clinic_base.domain.model.BusinessTargetOfMonthModel;
import com.yunya.feign.clinic_base.domain.query.BusinessTargetQuery;
import com.yunya.feign.clinic_base.domain.vo.BusinessTargetOfMonthVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.clinic_base.BusinessTarget;
import com.yunya.modules.clinic_base.mapper.BusinessTargetMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static com.yunya.framework.common.constant.OperationCodeConstants.PARAMETERS_IS_ILLEGAL;

/**
 * 简介: 业务目标业务层
 *
 * @author: chow
 * @date: 2020/12/22 16:09
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BusinessTargetBiz extends BaseBiz<BusinessTargetMapper, BusinessTarget> {

  /**
   * 根据条件查询业务目标
   *
   * @param query 查询条件
   * @return List<BusinessTargetOfMonthVO>
   */
  public List<BusinessTargetOfMonthVO> findBusinessTargetList(BusinessTargetQuery query) {
    List<BusinessTargetOfMonthVO> resultList = mapper.selectBusinessTargetList(query);
    return resultList;
  }

  /**
   * 新增业务目标
   *
   * @param model 新增参数
   */
  public void saveOrUpdate(BusinessTargetModel model) {
    Set<BusinessTargetOfMonthModel> monthModelList = model.getBusinessTargetOfMonthModels();
    if (StringHelper.isEmpty(monthModelList)) {
      throw new ClientServiceException("新增失败，月目标不能为空！", PARAMETERS_IS_ILLEGAL);
    }
    Byte belongType = model.getBelongType();
    Byte businessType = model.getBusinessType();
    String businessYear = model.getBusinessYear();
    String unit = model.getUnit();
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    int orgId = Integer.parseInt(BaseContextHandler.getOrgId());
    String name = BaseContextHandler.getName();
    BusinessTarget businessTarget = new BusinessTarget();
    businessTarget.setBusinessType(businessType);
    businessTarget.setBelongType(belongType);
    businessTarget.setBelongId(belongType == 0 ? orgId : userId);
    businessTarget.setBusinessYear(businessYear);
    mapper.delete(businessTarget);
    monthModelList.forEach(
        monthModel -> {
          Byte monthNum = monthModel.getMonthNum();
          BigDecimal businessGoal = monthModel.getBusinessGoal();
          if (null != monthNum && null != businessGoal) {
            BusinessTarget target = new BusinessTarget();
            target.setBelongType(belongType);
            target.setBelongId(belongType == 0 ? orgId : userId);
            target.setBusinessType(businessType);
            target.setBusinessGoal(businessGoal);
            target.setBusinessYear(businessYear);
            target.setBusinessMonth(monthNum.toString());
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
