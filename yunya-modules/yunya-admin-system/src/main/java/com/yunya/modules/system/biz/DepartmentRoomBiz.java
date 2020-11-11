package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.system.ClinicDepartmentRoom;
import com.yunya.models.system.ClinicExtInfo;
import com.yunya.models.system.DepartmentRoom;
import com.yunya.modules.system.domain.form.DepartmentRoomForm;
import com.yunya.modules.system.domain.model.DepartmentRoomModel;
import com.yunya.modules.system.domain.query.DepartmentRoomQueryForm;
import com.yunya.modules.system.mapper.ClinicExtInfoMapper;
import com.yunya.modules.system.mapper.DepartmentRoomMapper;
import com.yunya.modules.system.vo.DepartmentRoomVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 简介: 科室管理业务层
 *
 * @author: chow
 * @date: 2020/7/20 17:25
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DepartmentRoomBiz extends BaseBiz<DepartmentRoomMapper, DepartmentRoom> {

  /** 注入对象 */
  private final ClinicDepartmentRoomBiz clinicDepartmentRoomBiz;
  @Autowired
  private ClinicExtInfoBiz clinicExtInfoBiz;

  public DepartmentRoomBiz(ClinicDepartmentRoomBiz clinicDepartmentRoomBiz) {
    this.clinicDepartmentRoomBiz = clinicDepartmentRoomBiz;
  }

  /**
   * 根据ID查询科室模板信息
   *
   * @param id 科室ID
   * @return
   */
  public DepartmentRoomVO findById(Integer id) {
    DepartmentRoomVO resultData = mapper.selectById(id);
    return resultData;
  }

  /**
   * 科室列表查询
   *
   * @param queryForm 查询条件
   * @return
   */
  public PageInfo<DepartmentRoomVO> findList(DepartmentRoomQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<DepartmentRoomVO> resultList = mapper.selectList(queryForm);
    return new PageInfo<>(resultList);
  }

  /**
   * 新增科室
   *
   * @param resource 参数封装
   */
  public void saveDepartmentRoom(DepartmentRoomModel resource) {
    DepartmentRoom entity = new DepartmentRoom();
    String name = resource.getName();
    entity.setName(name);
    int count = mapper.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException(
          "新增科室失败，'" + name + "'已经存在", OperationCodeConstants.NAME_IS_OCCUPIED);
    }
    entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    entity.setCrtName(BaseContextHandler.getName());
    int i = mapper.insertSelective(entity);
    if (i>0) {
      List<ClinicDepartmentRoom> clinicDepartmentRooms = new ArrayList<>();
      List<ClinicExtInfo> clinicExtInfos = clinicExtInfoBiz.selectListAll();
      Integer id = entity.getId();
      if (StringHelper.isNotEmpty(clinicExtInfos)) {
        clinicExtInfos.forEach(clinicExtInfo -> {
          ClinicDepartmentRoom clinicDepartmentRoom = new ClinicDepartmentRoom();
          clinicDepartmentRoom.setCompanyId(clinicExtInfo.getId());
          clinicDepartmentRoom.setDeptRoomId(id);
          clinicDepartmentRoom.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
          clinicDepartmentRoom.setCrtName(BaseContextHandler.getName());
          clinicDepartmentRoom.setCrtTime(new Date(System.currentTimeMillis()));
          clinicDepartmentRooms.add(clinicDepartmentRoom);
        });
      }
      clinicDepartmentRoomBiz.addBatch(clinicDepartmentRooms);
    }
  }

  /**
   * 修改科室
   *
   * @param id 科室ID
   * @param form 参数封装
   */
  public void modify(Integer id, DepartmentRoomForm form) {
    DepartmentRoom resultData = mapper.selectByPrimaryKey(id);
    if (null == resultData) {
      throw new ClientServiceException(
          "修改失败，ID为'" + id + "'的数据不存在", OperationCodeConstants.OBJECT_EDIT_FAIL);
    }
    String name = form.getName();
    if (!resultData.getName().equals(name)) {
      resultData = new DepartmentRoom();
      resultData.setName(name);
      int count = mapper.selectCount(resultData);
      if (count > 0) {
        throw new ClientServiceException(
            "修改失败，名称为'" + name + "'的科室已经存在", OperationCodeConstants.NAME_IS_OCCUPIED);
      }
    }
    if (null != form.getInservice()) {
      resultData.setInservice(form.getInservice());
    }
    resultData.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    resultData.setUpdName(BaseContextHandler.getName());
    resultData.setUpdTime(new Date(System.currentTimeMillis()));
    resultData.setId(id);
    mapper.updateByPrimaryKeySelective(resultData);
  }

  /**
   * 根据ID删除科室
   *
   * @param id 科室ID
   */
  public void deleteDeptRoomById(Integer id) {
    ClinicDepartmentRoom clinicDeptRoom = new ClinicDepartmentRoom();
    clinicDeptRoom.setDeptRoomId(id);
    Long count = clinicDepartmentRoomBiz.selectCount(clinicDeptRoom);
    if (count > 0) {
      throw new ClientServiceException("该科室已被关联，不允许被删除！", OperationCodeConstants.DELETE_NOT_ALLOW);
    }
    mapper.deleteByPrimaryKey(id);
  }
}
