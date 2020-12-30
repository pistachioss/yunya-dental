package com.yunya.modules.clinic_base.biz;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.clinic_base.domain.model.SpecialistProjectTargetModel;
import com.yunya.feign.clinic_base.domain.model.TargetOfMonthModel;
import com.yunya.feign.clinic_base.domain.query.BusinessWorkGoalQuery;
import com.yunya.feign.clinic_base.domain.query.SpecialistProjectTargetQuery;
import com.yunya.feign.clinic_base.domain.query.SpecialistProjectWorkGoalQuery;
import com.yunya.feign.clinic_base.domain.vo.SpecialistProjectWorkGoalVO;
import com.yunya.feign.clinic_base.domain.vo.TargetOfMonthVO;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.feign.treatment.domain.query.SpecialistProjectTariffCompletedInfoQuery;
import com.yunya.feign.treatment.domain.vo.SpecialistProjectTariffCompletedInfoVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.clinic_base.SpecialistBusinessTarget;
import com.yunya.models.clinic_base.SpecialistProject;
import com.yunya.modules.clinic_base.mapper.SpecialistBusinessTargetMapper;
import com.yunya.modules.clinic_base.mapper.SpecialistProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

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

  /** 专科项目 */
  @Autowired private SpecialistProjectMapper specialistProjectMapper;
  /** 诊疗服务调用 */
  @Autowired private RemoteTreatmentServiceFeign treatmentServiceFeign;

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

  /**
   * 根据条件查询专科数量列表
   *
   * @param query 查询条件
   * @return PageInfo<SpecialistProjectWorkGoalVO>
   */
  public PageInfo<SpecialistProjectWorkGoalVO> findSpecialistProjectWorkGoalList(
      BusinessWorkGoalQuery query) {
    Byte dateType = query.getDateType();
    Integer[] belongIds = query.getBelongIds();
    // 将时间区间进行切片
    List<String> dateRange = DateUtil.sliceUpDateRange(query.getStartDate(), query.getEndDate());
    List<SpecialistProject> specialistProjects = specialistProjectMapper.selectAll();
    if (StringHelper.isEmpty(specialistProjects)) {
      return null;
    }
    List<Map<String, Object>> specialistProjectWorkGoals = new ArrayList<>();
    for (String date : dateRange) {
      Map<String, Object> hashMap = new HashMap<>(16);
      hashMap.put(date, date);
      for (SpecialistProject project : specialistProjects) {
        // 设置专科项目工作目标属性
        SpecialistProjectWorkGoalVO specialistProjectWorkGoal =
            setSpecialistProjectWorkGoalValue(dateType, date, project.getId(), belongIds);
        specialistProjectWorkGoal.setSpecialistProjectName(project.getName());
        // 获取专科项目工作目标完成信息
        SpecialistProjectTariffCompletedInfoVO specialistProjectCompletedInfo =
            getClinicSpecialistProjectCompletedInfo(
                dateType, date, project.getTariffIds(), belongIds);
        specialistProjectWorkGoal.setSpecialistProjectCompletedInfo(specialistProjectCompletedInfo);
        Integer specialistProjectCompleted =
            specialistProjectCompletedInfo.getSpecialistProjectCompleted();
        Integer specialistProjectGoal = specialistProjectWorkGoal.getSpecialistProjectGoal();
        if (0 != specialistProjectGoal) {
          float percentageOfSpecialistProjectCompleted =
              (float) specialistProjectCompleted / specialistProjectCompleted;
          specialistProjectWorkGoal.setPercentageOfSpecialistProjectCompleted(
              BigDecimal.valueOf(percentageOfSpecialistProjectCompleted)
                  .setScale(2, BigDecimal.ROUND_HALF_UP)
                  .floatValue());
          hashMap.put(project.getId().toString(), specialistProjectWorkGoal);
        }
      }
    }
    return new PageInfo<>();
  }

  /**
   * 设置专科项目工作目标属性
   *
   * @param dateType 日期类型
   * @param date 日期
   * @param specialistProjectId 专科项目ID
   * @param belongIds 所属诊所ID列表
   * @return SpecialistProjectWorkGoalVO
   */
  private SpecialistProjectWorkGoalVO setSpecialistProjectWorkGoalValue(
      Byte dateType, String date, Integer specialistProjectId, Integer[] belongIds) {
    SpecialistProjectWorkGoalVO vo = new SpecialistProjectWorkGoalVO();
    vo.setBusinessDate(date);
    vo.setSpecialistProjectId(specialistProjectId);
    Integer selectSpecialistProjectGoalAmount =
        mapper.selectClinicSpecialistProjectWorkGoal(
            dateType, date, specialistProjectId, belongIds);
    vo.setSpecialistProjectGoal(selectSpecialistProjectGoalAmount);
    return vo;
  }

  /**
   * 获取门诊专科项目完成信息
   *
   * @param dateType 日期类型
   * @param date 日期
   * @param tariffId 专科项目对应项目ID列表
   * @param belongIds 门诊ID列表
   * @return SpecialistProjectCompletedInfoVO
   */
  private SpecialistProjectTariffCompletedInfoVO getClinicSpecialistProjectCompletedInfo(
      Byte dateType, String date, String tariffId, Integer[] belongIds) {
    String[] tariffIds = tariffId.split(",");
    SpecialistProjectTariffCompletedInfoQuery query =
        new SpecialistProjectTariffCompletedInfoQuery();
    query.setDateType(dateType);
    query.setQueryDate(date);
    query.setTariffIds(tariffIds);
    query.setBelongIds(belongIds);
    return treatmentServiceFeign.findClinicTariffOrderCompletedInfo(query);
  }

  /**
   * 根据条件查询专科数量列表
   *
   * @param query 查询条件
   * @return PageInfo<SpecialistProjectWorkGoalVO>
   */
  public PageInfo<SpecialistProjectWorkGoalVO> findSpecialistProjectWorkGoalList(
      SpecialistProjectWorkGoalQuery query) {
    Byte dateType = query.getDateType();
    Integer[] belongIds = query.getBelongIds();
    Integer[] specialistProjectIds = query.getSpecialistProjectIds();
    // 将时间区间进行切片
    List<String> dateRange = DateUtil.sliceUpDateRange(query.getStartDate(), query.getEndDate());
    List<SpecialistProjectWorkGoalVO> specialistProjectWorkGoals = new ArrayList<>();
    for (Integer projectId : specialistProjectIds) {
      for (String date : dateRange) {
        // 设置专科项目工作目标属性
        SpecialistProjectWorkGoalVO specialistProjectWorkGoal =
            setSpecialistProjectWorkGoalValue(dateType, date, projectId, belongIds);
        SpecialistProject specialistProject = specialistProjectMapper.selectByPrimaryKey(projectId);
        if (null != specialistProject) {
          specialistProjectWorkGoal.setSpecialistProjectName(specialistProject.getName());
          // 获取专科项目工作目标完成信息
          SpecialistProjectTariffCompletedInfoVO specialistProjectCompletedInfo =
              getClinicSpecialistProjectCompletedInfo(
                  dateType, date, specialistProject.getTariffIds(), belongIds);
          specialistProjectWorkGoal.setSpecialistProjectCompletedInfo(
              specialistProjectCompletedInfo);
          Integer specialistProjectCompleted =
              specialistProjectCompletedInfo.getSpecialistProjectCompleted();
          Integer specialistProjectGoal = specialistProjectWorkGoal.getSpecialistProjectGoal();
          if (0 != specialistProjectGoal && null != specialistProjectCompleted) {
            float percentageOfSpecialistProjectCompleted =
                (float) specialistProjectCompleted / specialistProjectGoal;
            specialistProjectWorkGoal.setPercentageOfSpecialistProjectCompleted(
                BigDecimal.valueOf(percentageOfSpecialistProjectCompleted)
                    .setScale(2, BigDecimal.ROUND_HALF_UP)
                    .floatValue());
          }
          specialistProjectWorkGoals.add(specialistProjectWorkGoal);
        }
      }
    }
    if (query.getWhetherPage()) {
      Integer pageNum = query.getPageNum();
      Integer pageSize = query.getPageSize();
      int total = specialistProjectWorkGoals.size();
      PageInfo<SpecialistProjectWorkGoalVO> pageInfo = new PageInfo<>();
      pageInfo.setPageNum(pageNum);
      pageInfo.setPageSize(pageSize);
      pageInfo.setTotal(total);
      List<SpecialistProjectWorkGoalVO> list =
          specialistProjectWorkGoals.subList(
              pageSize * (pageNum - 1), (Math.min((pageSize * pageNum), total)));
      pageInfo.setList(list);
      return pageInfo;
    }
    return new PageInfo<>(specialistProjectWorkGoals);
  }

  public void exportSpecialistProjectWorkGoalList(
      HttpServletResponse response, SpecialistProjectWorkGoalQuery query) {
    PageInfo<SpecialistProjectWorkGoalVO> pageInfo = findSpecialistProjectWorkGoalList(query);
    List<SpecialistProjectWorkGoalVO> infoList = pageInfo.getList();
  }
}
