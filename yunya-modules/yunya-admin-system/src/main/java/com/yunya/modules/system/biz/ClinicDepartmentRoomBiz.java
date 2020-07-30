package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.system.ClinicDepartmentRoom;
import com.yunya.models.system.ClinicExtInfo;
import com.yunya.modules.system.domain.model.ClinicDepartmentRoomModel;
import com.yunya.modules.system.domain.query.ClinicDepartmentRoomQueryForm;
import com.yunya.modules.system.mapper.ClinicDepartmentRoomMapper;
import com.yunya.modules.system.vo.ClinicDepartmentRoomVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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
  private final ClinicExtInfoBiz clinicExtInfoBiz;

  public ClinicDepartmentRoomBiz(ClinicExtInfoBiz clinicExtInfoBiz) {
    this.clinicExtInfoBiz = clinicExtInfoBiz;
  }

  /**
   * 批量新增门诊科室
   *
   * @param deptRoomId 科室模板ID
   */
  public void batchSave(Integer deptRoomId) {
    List<ClinicExtInfo> clinicExtInfos = clinicExtInfoBiz.selectListAll();
    if (clinicExtInfos.size() == 0) {
      throw new ClientServiceException(
          "一键新增门诊科室失败，未查询到门诊信息，请联系系统管理员添加门诊！", OperationCodeConstants.QUERY_RESULT_INVALID);
    }
    ClinicDepartmentRoom clinicDeptRoom;
    for (ClinicExtInfo info : clinicExtInfos) {
      clinicDeptRoom = new ClinicDepartmentRoom();
      clinicDeptRoom.setCompanyId(info.getCompanyId());
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
          "ID为'" + clinicDeptRoomId + "'的门诊科室不存在！", OperationCodeConstants.QUERY_RESULT_INVALID);
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
}
