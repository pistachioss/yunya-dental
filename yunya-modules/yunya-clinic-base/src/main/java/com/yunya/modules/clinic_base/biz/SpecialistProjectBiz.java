package com.yunya.modules.clinic_base.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Joiner;
import com.yunya.feign.clinic_base.domain.form.SpecialistProjectForm;
import com.yunya.feign.clinic_base.domain.form.SpecialistProjectReportForm;
import com.yunya.feign.clinic_base.domain.model.SpecialistProjectModel;
import com.yunya.feign.clinic_base.domain.model.SpecialistProjectReportModel;
import com.yunya.feign.clinic_base.domain.query.SpecialistProjectQuery;
import com.yunya.feign.clinic_base.domain.vo.SpecialistProjectNameVO;
import com.yunya.feign.clinic_base.domain.vo.SpecialistProjectReportVO;
import com.yunya.feign.clinic_base.domain.vo.SpecialistProjectVO;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.clinic_base.SpecialistProject;
import com.yunya.modules.clinic_base.mapper.SpecialistProjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.PARAMETERS_IS_ILLEGAL;

/**
 * 简介: 专科项目设置业务层
 *
 * @author: chow
 * @date: 2020/12/22 13:29
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SpecialistProjectBiz extends BaseBiz<SpecialistProjectMapper, SpecialistProject> {

  /** 价目表服务 */
  @Autowired private RemoteTreatmentServiceFeign treatmentServiceFeign;

  /**
   * 根据条件查询专科项目列表
   *
   * @param query 查询条件
   * @return PageInfo<SpecialistProjectVO>
   */
  public PageInfo<SpecialistProjectVO> findSpecialistProjectList(SpecialistProjectQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<SpecialistProjectVO> resultList = mapper.selectSpecialistProjectList(query);
    if (StringHelper.isNotEmpty(resultList)) {
      resultList.forEach(
          vo -> {
            String tariffItemIds = vo.getTariffItemIds();
            String[] ids = tariffItemIds.split(",");
            String tariffItemName = treatmentServiceFeign.findBaseTariffNamesByIds(ids);
            vo.setTariffItemName(tariffItemName);
          });
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 专科项目名称列表
   *
   * @return List<SpecialistProjectNameVO>
   */
  public List<SpecialistProjectNameVO> findSpecialistProjectName() {
    return mapper.selectSpecialistProjectNameList();
  }

  /**
   * 新增专科项目
   *
   * @param model 新增模型
   */
  public void save(SpecialistProjectModel model) {
    String specialistProjectName = model.getSpecialistProjectName();
    SpecialistProject entity = checkSpecialistProjectName(specialistProjectName);
    Integer[] tariffItemIds = model.getTariffItemIds();
    if (StringHelper.isNotEmpty(tariffItemIds)) {
      Joiner joiner = Joiner.on(',');
      entity.setTariffIds(joiner.join(tariffItemIds));
      Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
      String name = BaseContextHandler.getName();
      entity.setCrtId(userId);
      entity.setCrtName(name);
      entity.setUpdId(userId);
      entity.setUpdName(name);
      mapper.insertSelective(entity);
    }
  }

  /**
   * 校验专科项目名字是否重复
   *
   * @param specialistProjectName 专科项目名称
   * @return SpecialistProject
   */
  private SpecialistProject checkSpecialistProjectName(String specialistProjectName) {
    SpecialistProject entity = new SpecialistProject();
    entity.setName(specialistProjectName);
    int count = mapper.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException("新增失败，该专科项目名称已存在！", PARAMETERS_IS_ILLEGAL);
    }
    return entity;
  }

  /**
   * 根据ID修改专科项目
   *
   * @param id 专科项目ID
   * @param form 修改参数
   */
  public void updateSpecialistProject(Integer id, SpecialistProjectForm form) {
    SpecialistProject specialistProject = mapper.selectByPrimaryKey(id);
    if (null == specialistProject) {
      throw new ClientServiceException("修改失败，未查询到相关专科项目！", PARAMETERS_IS_ILLEGAL);
    }
    String specialistProjectName = form.getSpecialistProjectName();
    String projectName = specialistProject.getName();
    if (!specialistProjectName.equals(projectName)) {
      checkSpecialistProjectName(specialistProjectName);
    }
    specialistProject.setName(specialistProjectName);
    Integer[] tariffItemIds = form.getTariffItemIds();
    if (StringHelper.isNotEmpty(tariffItemIds)) {
      Joiner joiner = Joiner.on(',');
      specialistProject.setTariffIds(joiner.join(tariffItemIds));
      Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
      String name = BaseContextHandler.getName();
      specialistProject.setUpdId(userId);
      specialistProject.setUpdName(name);
      mapper.updateByPrimaryKeySelective(specialistProject);
    }
  }

  /**
   * 就诊患者分析-专科项目
   *
   * @param form 查询条件
   * @return List<SpecialistProjectReportVO>
   */
  public List<SpecialistProjectReportVO> specialistProjectReport(SpecialistProjectReportForm form) {
    SpecialistProjectReportModel specialistProjectReportModel = new SpecialistProjectReportModel();
    BeanUtils.copyProperties(form,specialistProjectReportModel);
    List<SpecialistProjectReportVO> specialistProjectReportVOList = new ArrayList<>();
    List<SpecialistProject> specialistProjects = mapper.selectListAll();
    if (specialistProjects.size() > 0) {
      specialistProjects.forEach(
          specialistProject -> {
            SpecialistProjectReportVO specialistProjectReportVO = new SpecialistProjectReportVO();
            specialistProjectReportVO.setSpecialistProjectName(specialistProject.getName());
            String tariffIds = specialistProject.getTariffIds();
            if (tariffIds != null) {
              String[] billingItemIds = tariffIds.split(",");
              specialistProjectReportModel.setBillingItemIds(billingItemIds);
              String percentage =
                  treatmentServiceFeign.findTariffSpecialistPercentage(specialistProjectReportModel);
              specialistProjectReportVO.setPercentage(percentage);
            }
            specialistProjectReportVOList.add(specialistProjectReportVO);
          });
    }
    return specialistProjectReportVOList;
  }
}
