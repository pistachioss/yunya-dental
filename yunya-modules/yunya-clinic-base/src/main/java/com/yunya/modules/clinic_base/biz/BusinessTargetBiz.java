package com.yunya.modules.clinic_base.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.clinic_base.domain.model.BusinessTargetModel;
import com.yunya.feign.clinic_base.domain.model.TargetOfMonthModel;
import com.yunya.feign.clinic_base.domain.query.BusinessGoalCompletedInfoQuery;
import com.yunya.feign.clinic_base.domain.query.BusinessTargetQuery;
import com.yunya.feign.clinic_base.domain.query.BusinessWorkGoalQuery;
import com.yunya.feign.clinic_base.domain.vo.*;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.query.CompletedWorkGoalQuery;
import com.yunya.feign.treatment.domain.vo.BusinessCompletedWorkGoalVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.clinic_base.BusinessTarget;
import com.yunya.modules.clinic_base.mapper.BusinessTargetMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

  /** 就诊服务调用 */
  private final RemoteTreatmentServiceFeign treatmentServiceFeign;

  public BusinessTargetBiz(RemoteTreatmentServiceFeign treatmentServiceFeign) {
    this.treatmentServiceFeign = treatmentServiceFeign;
  }

  /**
   * 根据条件查询业务目标
   *
   * @param query 查询条件
   * @return List<BusinessTargetOfMonthVO>
   */
  public List<TargetOfMonthVO> findBusinessTargetList(BusinessTargetQuery query) {
    List<TargetOfMonthVO> resultList = mapper.selectBusinessTargetList(query);
    return resultList;
  }

  /**
   * 新增业务目标
   *
   * @param model 新增参数
   */
  public void saveOrUpdate(BusinessTargetModel model) {
    Set<TargetOfMonthModel> monthModelList = model.getBusinessTargetOfMonthModels();
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
          if (null != monthNum) {
            BusinessTarget target = new BusinessTarget();
            target.setBelongType(belongType);
            target.setBelongId(belongType == 0 ? orgId : userId);
            target.setBusinessType(businessType);
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

  /**
   * 根据条件查询门诊业务目标列表
   *
   * @param query 查询条件
   * @return PageInfo<BusinessWorkGoalVO>
   */
  public PageInfo<BusinessWorkGoalVO> findBusinessWorkGoalList(BusinessWorkGoalQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<BusinessWorkGoalVO> resultList = mapper.selectBusinessWorkGoalList(query);
    if (StringHelper.isNotEmpty(resultList)) {
      CompletedWorkGoalQuery goalQuery = new CompletedWorkGoalQuery();
      goalQuery.setDateType(query.getDateType());
      goalQuery.setBelongIds(query.getBelongIds());
      resultList.forEach(
          vo -> {
            goalQuery.setBusinessDate(vo.getBusinessDate());
            BusinessCompletedWorkGoalVO businessCompletedWorkGoalVO =
                treatmentServiceFeign.findClinicCompletedBusinessWorkGoal(goalQuery);
            BigDecimal actualReceivedAmountCompleted =
                businessCompletedWorkGoalVO.getActualReceivedAmountCompleted();
            vo.setActualReceivedAmountCompleted(actualReceivedAmountCompleted);
            BigDecimal actualReceivedAmountGoal = vo.getActualReceivedAmountGoal();
            if (!BigDecimal.valueOf(0, 2).equals(actualReceivedAmountGoal)
                && null != actualReceivedAmountCompleted) {
              BigDecimal percentageOfActualReceivedCompletedAmount =
                  actualReceivedAmountCompleted
                      .multiply(new BigDecimal(100))
                      .divide(actualReceivedAmountGoal, 2, BigDecimal.ROUND_HALF_UP);
              vo.setPercentageOfActualReceivedCompletedAmount(
                  Float.valueOf(percentageOfActualReceivedCompletedAmount.toString()));
            }
            BigDecimal workloadAmountCompleted =
                businessCompletedWorkGoalVO.getWorkloadAmountCompleted();
            vo.setWorkloadAmountCompleted(workloadAmountCompleted);
            BigDecimal workloadAmountGoal = vo.getWorkloadAmountGoal();
            if (!BigDecimal.valueOf(0, 2).equals(workloadAmountGoal)
                && null != workloadAmountCompleted) {
              BigDecimal percentageOfWorkloadAmountCompleted =
                  workloadAmountCompleted
                      .multiply(new BigDecimal(100))
                      .divide(workloadAmountGoal, 2, BigDecimal.ROUND_HALF_UP);
              vo.setPercentageOfWorkloadAmountCompleted(
                  Float.valueOf(percentageOfWorkloadAmountCompleted.toString()));
            }
            Integer firstTreatPerNumCompleted =
                businessCompletedWorkGoalVO.getFirstTreatPerNumCompleted();
            vo.setFirstTreatPerNumCompleted(firstTreatPerNumCompleted);
            Integer firstTreatPerNumGoal = vo.getFirstTreatPerNumGoal();
            if (0 != firstTreatPerNumGoal && null != firstTreatPerNumCompleted) {
              float percentageOfFirstTreatPerNumCompleted =
                  (float) firstTreatPerNumCompleted / firstTreatPerNumGoal;
              vo.setPercentageOfFirstTreatPerNumCompleted(
                  BigDecimal.valueOf(percentageOfFirstTreatPerNumCompleted)
                      .multiply(new BigDecimal(100))
                      .setScale(2, BigDecimal.ROUND_HALF_UP)
                      .floatValue());
            }
            Integer treatPerTimesCompleted =
                businessCompletedWorkGoalVO.getTreatPerTimesCompleted();
            vo.setTreatPerTimesCompleted(treatPerTimesCompleted);
            Integer treatPerTimesGoal = vo.getTreatPerTimesGoal();
            if (0 != treatPerTimesGoal && null != treatPerTimesCompleted) {
              float percentageOfTreatPerTimesCompleted =
                  (float) treatPerTimesCompleted / treatPerTimesGoal;
              vo.setPercentageOfTreatPerTimesCompleted(
                  BigDecimal.valueOf(percentageOfTreatPerTimesCompleted)
                      .multiply(new BigDecimal(100))
                      .setScale(2, BigDecimal.ROUND_HALF_UP)
                      .floatValue());
            }
          });
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出门诊业务目标列表
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportBusinessWorkGoalList(HttpServletResponse response, BusinessWorkGoalQuery query)
      throws IOException {
    ExcelUtil<BusinessWorkGoalVO> excelUtil = new ExcelUtil<>(BusinessWorkGoalVO.class);
    PageInfo<BusinessWorkGoalVO> pageInfo = findBusinessWorkGoalList(query);
    excelUtil.exportExcel(response, pageInfo.getList(), "门诊业务目标列表");
  }

  /**
   * 根据条件查询组织业务目标完成情况
   *
   * @param query 查询条件
   * @return BusinessGoalCompletedInfoVO
   */
  public BusinessGoalCompletedInfoVO findBusinessGoalCompletedInfo(
      BusinessGoalCompletedInfoQuery query) {
    String startDate = query.getStartDate();
    String endDate = query.getEndDate();
    Byte businessType = query.getBusinessType();
    Integer orgId = query.getOrgId();
    List<String> dateRange = DateUtil.sliceUpDateRange(startDate, endDate);
    BusinessGoalCompletedInfoVO resultData = new BusinessGoalCompletedInfoVO();
    resultData.setBusinessCompletedPercentage(new BigDecimal("0.00"));

    // 查询该时间段内业务目标设置数量
    BigDecimal businessGoalCount = mapper.selectBusinessGoalCount(orgId, businessType, dateRange);
    // 查询业务目标完成数量
    BigDecimal businessCompletedCount = BigDecimal.ZERO;

    switch (businessType) {
        // 营业收入
      case 0:
        businessCompletedCount = treatmentServiceFeign.findBusinessIncomeCompletedCount(query);
        break;
        // 工作量
      case 1:
        businessCompletedCount = treatmentServiceFeign.findBusinessWorkloadCompletedCount(query);
        break;
        // 初诊人数
      case 2:
        businessCompletedCount = treatmentServiceFeign.findBusinessFirstTreatCompletedCount(query);
        break;
      default:
        break;
    }
    if (null != businessGoalCount) {
      resultData.setBusinessGoalCount(businessGoalCount);
    }
    if (null != businessCompletedCount) {
      resultData.setBusinessCompletedCount(businessCompletedCount);
    }
    if (null != businessCompletedCount) {
      if (null != businessGoalCount && businessGoalCount.compareTo(BigDecimal.ZERO) > 0) {
        resultData.setBusinessCompletedPercentage(
            businessCompletedCount
                .multiply(new BigDecimal(100))
                .divide(businessGoalCount, 2, BigDecimal.ROUND_HALF_UP));
      }
    }
    return resultData;
  }

  public List<BusinessGoalVO> findBusinessGoalList(BusinessGoalCompletedInfoQuery query) {
    return mapper.selectBusinessGoalList(query);
  }
}
