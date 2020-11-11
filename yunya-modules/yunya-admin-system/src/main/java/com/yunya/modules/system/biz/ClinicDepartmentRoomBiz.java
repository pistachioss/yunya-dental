package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yunya.feign.system.form.ClinicDeptRoomConfigureQueryForm;
import com.yunya.feign.system.vo.ClinicDepartmentRoomVO;
import com.yunya.feign.system.vo.ClinicDeptRoomListVO;
import com.yunya.feign.system.vo.DeptRoomVO;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.system.ClinicDepartmentRoom;
import com.yunya.models.system.DepartmentRoom;
import com.yunya.modules.system.domain.model.ClinicDepartmentRoomModel;
import com.yunya.modules.system.domain.query.ClinicDepartmentRoomQueryForm;
import com.yunya.modules.system.domain.query.OrganizationQueryForm;
import com.yunya.modules.system.mapper.ClinicDepartmentRoomMapper;
import com.yunya.modules.system.mapper.CompanyMapper;
import com.yunya.modules.system.mapper.DepartmentRoomMapper;
import com.yunya.modules.system.vo.OrganizationInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.yunya.framework.common.constant.OperationCodeConstants.QUERY_RESULT_INVALID;

/**
 * 简介: 门诊科室控制器
 *
 * @author: chow
 * @date: 2020/7/20 20:30
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ClinicDepartmentRoomBiz
    extends BaseBiz<ClinicDepartmentRoomMapper, ClinicDepartmentRoom> {

  /** 注入对象 */
  @Autowired private CompanyMapper companyMapper;

  @Autowired private DepartmentRoomMapper departmentRoomMapper;

  /**
   * 批量新增门诊科室
   *
   * @param deptRoomId 科室模板ID
   */
  public void batchSave(Integer deptRoomId) {
    OrganizationQueryForm query = new OrganizationQueryForm();
    query.setTypes(new Byte[] {2});
    List<OrganizationInfoVO> organizations = companyMapper.selectOrganizationByExample(query);
    if (StringHelper.isEmpty(organizations)) {
      throw new ClientServiceException("一键新增门诊科室失败，未查询到门诊信息，请联系系统管理员添加门诊！", QUERY_RESULT_INVALID);
    }
    ClinicDepartmentRoom clinicDeptRoom;
    for (OrganizationInfoVO info : organizations) {
      clinicDeptRoom = new ClinicDepartmentRoom();
      clinicDeptRoom.setCompanyId(info.getId());
      clinicDeptRoom.setDeptRoomId(deptRoomId);
      clinicDeptRoom.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
      clinicDeptRoom.setCrtName(BaseContextHandler.getName());
      ClinicDepartmentRoom resultData = mapper.selectOne(clinicDeptRoom);
      if (null == resultData) {
        mapper.insertSelective(clinicDeptRoom);
      }
    }
  }

  /**
   * 设置门诊科室是否启用
   *
   * @param clinicDeptRoomId 门诊科室ID
   */
  public void switchDeptRoomDisable(Integer clinicDeptRoomId) {
    ClinicDepartmentRoom resultData = mapper.selectByPrimaryKey(clinicDeptRoomId);
    if (null == resultData) {
      throw new ClientServiceException(
          "ID为'" + clinicDeptRoomId + "'的门诊科室不存在！", QUERY_RESULT_INVALID);
    }
    resultData.setInservice(!resultData.getInservice());
    resultData.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    resultData.setUpdName(BaseContextHandler.getName());
    resultData.setUpdTime(new Date(System.currentTimeMillis()));
    mapper.updateByPrimaryKeySelective(resultData);
  }

  /**
   * 新增门诊科室
   *
   * @param model 参数模型
   */
  public void add(ClinicDepartmentRoomModel model) {
    ClinicDepartmentRoom entity = new ClinicDepartmentRoom();
    entity.setCompanyId(model.getOrgId());
    entity.setDeptRoomId(model.getDepartmentRoomId());
    entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    entity.setCrtName(BaseContextHandler.getName());
    ClinicDepartmentRoom resultData = mapper.selectOne(entity);
    if (null == resultData) {
      mapper.insertSelective(entity);
    }
  }

  /**
   * 根据条件查询门诊科室配置列表
   *
   * @param queryForm 查询条件
   * @return
   */
  public PageInfo<ClinicDepartmentRoomVO> configure(ClinicDeptRoomConfigureQueryForm queryForm) {
    List<ClinicDepartmentRoomVO> resultList = Lists.newArrayList();
    Integer deptRoomId = queryForm.getDeptRoomId();
    DepartmentRoom departmentRoom = departmentRoomMapper.selectByPrimaryKey(deptRoomId);
    if (null != departmentRoom) {
      if (queryForm.getWhetherPage()) {
        PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
      }
      OrganizationQueryForm form = new OrganizationQueryForm();
      form.setTypes(new Byte[] {2});
      List<OrganizationInfoVO> organizations = companyMapper.selectOrganizationByExample(form);
      if (StringHelper.isNotEmpty(organizations)) {
        organizations.forEach(
            vo -> {
              Integer orgId = vo.getId();
              ClinicDepartmentRoomVO item = mapper.selectClinicDeptRoom(orgId, deptRoomId);
              if (null != item) {
                item.setOrgId(vo.getId());
                item.setOrgName(vo.getAbbreviation());
                resultList.add(item);
              }
            });
      }
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件查询门诊科室列表
   *
   * @param queryForm 查询条件
   * @return list
   */
  public PageInfo<ClinicDepartmentRoomVO> findList(ClinicDepartmentRoomQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<ClinicDepartmentRoomVO> resultList = mapper.selectClinicDepartmentRoomList(queryForm);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据门诊ID查询可用科室列表
   *
   * @param orgId 组织ID
   * @return
   */
  public ClinicDeptRoomListVO findClinicDeptRoomList(Integer orgId) {
    ClinicDeptRoomListVO resultData = new ClinicDeptRoomListVO();
    OrganizationInfo organizationInfo = companyMapper.selectOrgInfoById(orgId);
    if (null != organizationInfo) {
      resultData.setOrgId(organizationInfo.getId());
      resultData.setOrgName(organizationInfo.getAbbreviation());
      List<DeptRoomVO> clinicDeptRooms = Lists.newArrayList();
      List<DepartmentRoom> departmentRooms = departmentRoomMapper.selectAll();
      if (StringHelper.isNotEmpty(departmentRooms)) {
        clinicDeptRooms =
            departmentRooms.stream()
                .map(DepartmentRoom::getId)
                .<DeptRoomVO>map(
                    departmentRoomId -> mapper.selectDeptRoomVO(orgId, departmentRoomId, true))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
      }
      resultData.setClinicDeptRooms(clinicDeptRooms);
    }
    return resultData;
  }

  /**
   * 根据门诊科室ID查询门诊科室
   *
   * @param id 门诊科室ID
   * @return obj
   */
  public ClinicDepartmentRoomVO findByClinicDeptRoomId(Integer id) {
    return mapper.selectByClinicDeptRoomId(id);
  }

  /**
   * 根据门诊科室ID删除门诊科室
   *
   * @param id 门诊科室ID
   */
  public void deleteByClinicDeptRoomId(Integer id) {
    // todo 增加删除判断逻辑，通过feign查询该门诊科室是否被使用
    mapper.deleteByPrimaryKey(id);
  }

  /**
   * 设置科室在门诊是否启用
   *
   * @param orgId 组织ID
   * @param deptRoomId 科室ID
   */
  public void switchClinicDept(Integer orgId, Integer deptRoomId) {
    ClinicDepartmentRoom entity = new ClinicDepartmentRoom();
    entity.setCompanyId(orgId);
    entity.setDeptRoomId(deptRoomId);
    ClinicDepartmentRoom result = mapper.selectOne(entity);
    if (null == result) {
      entity.setInservice(false);
      entity.setCrtId(Integer.valueOf(BaseContextHandler.getOrgId()));
      entity.setCrtName(BaseContextHandler.getName());
      mapper.insertSelective(entity);
    } else {
      result.setInservice(!result.getInservice());
      result.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
      result.setUpdName(BaseContextHandler.getName());
      mapper.updateByPrimaryKeySelective(result);
    }
  }

  /**
   * 批量添加门诊科室
   * @param list 门诊科室列表
   */
  public void addBatch(List<ClinicDepartmentRoom> list) {
    mapper.insertBatch(list);
  }

}
