package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.system.ClinicDepartmentRoom;
import com.yunya.models.system.DepartmentRoom;
import com.yunya.modules.system.form.DepartmentRoomForm;
import com.yunya.modules.system.form.query.DepartmentRoomQueryForm;
import com.yunya.modules.system.mapper.DepartmentRoomMapper;
import com.yunya.modules.system.vo.DepartmentRoomVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  public DepartmentRoomBiz(ClinicDepartmentRoomBiz clinicDepartmentRoomBiz) {
    this.clinicDepartmentRoomBiz = clinicDepartmentRoomBiz;
  }

  /**
   * 科室列表查询
   *
   * @param queryForm 查询条件
   * @return
   */
  public List<DepartmentRoomVO> findList(DepartmentRoomQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<DepartmentRoomVO> resultList = mapper.selectList(queryForm);
    return resultList;
  }

  /**
   * 新增科室
   *
   * @param resource 参数封装
   */
  public void saveDepartmentRoom(DepartmentRoomForm resource) {
    DepartmentRoom entity = new DepartmentRoom();
    String name = resource.getName();
    entity.setName(name);
    DepartmentRoom resultData = mapper.selectOne(entity);
    if (resultData != null) {
      throw new ClientServiceException(
          "新增科室失败，'" + name + "'已经存在", OperationCodeConstants.NAME_IS_OCCUPIED);
    }
    entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    entity.setCrtName(BaseContextHandler.getName());
    mapper.insertSelective(entity);
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
    DepartmentRoom entity = new DepartmentRoom();
    entity.setName(name);
    int count = mapper.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException(
          "修改失败，名称为'" + name + "'的科室已经存在", OperationCodeConstants.NAME_IS_OCCUPIED);
    }
    if (null != form.getInservice()) {
      entity.setInservice(form.getInservice());
    }
    entity.setId(id);
    mapper.updateByPrimaryKeySelective(entity);
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
