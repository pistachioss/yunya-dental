package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.system.CompanyDepartment;
import com.yunya.models.system.Department;
import com.yunya.modules.system.domain.base.BaseForm;
import com.yunya.modules.system.domain.model.DepartmentModel;
import com.yunya.modules.system.domain.query.DepartmentQueryForm;
import com.yunya.modules.system.mapper.CompanyDepartmentMapper;
import com.yunya.modules.system.mapper.DepartmentMapper;
import com.yunya.modules.system.vo.DepartmentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 简单介绍:</br> 部门模版业务层
 *
 * @author: chow
 * @date: 2020/5/29 13:53
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DepartmentBiz extends BaseBiz<DepartmentMapper, Department> {

  /** 注入对象 */
  @Autowired private CompanyDepartmentMapper companyDepartmentMapper;

  /**
   * 根据条件查询部门列表（可分页）
   *
   * @param queryForm 查询条件封装
   * @return list
   */
  public PageInfo<DepartmentVO> findAll(DepartmentQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<DepartmentVO> resultList = mapper.selectDepartmentByExample(queryForm);
    return new PageInfo<>(resultList);
  }

  /**
   * 新增部门模版
   *
   * @param resource 参数封装
   */
  public void add(DepartmentModel resource) {
    String name = resource.getName();
    Department entity = new Department();
    entity.setName(name);
    int count = mapper.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException(
          "新增部门'" + name + "'失败，该部门名称已存在", OperationCodeConstants.NAME_IS_OCCUPIED);
    }
    entity.setType(resource.getType());
    entity.setOrderNum(resource.getOrderNum());
    entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    entity.setCrtName(BaseContextHandler.getName());
    mapper.insertSelective(entity);
  }

  /**
   * 编辑部门模版
   *
   * @param id 部门模版ID
   * @param form 参数封装
   */
  public void modifyDepartment(Integer id, BaseForm form) {
    Department resultData = mapper.selectByPrimaryKey(id);
    if (null == resultData) {
      throw new ClientServiceException(
          "修改部门，部门ID为'" + id + "'的数据不存在", OperationCodeConstants.QUERY_RESULT_INVALID);
    }
    String name = form.getName();
    // 部门名称有修改，校验名称是否重复
    if (!resultData.getName().equals(name)) {
      resultData = new Department();
      resultData.setName(name);
      int count = mapper.selectCount(resultData);
      if (count > 0) {
        throw new ClientServiceException(
            "修改部门'" + name + "'失败，该部门名称已存在", OperationCodeConstants.NAME_IS_OCCUPIED);
      }
    }
    resultData.setType(form.getType());
    resultData.setOrderNum(form.getOrderNum());
    Boolean inservice = form.getInservice();
    if (null != inservice) {
      resultData.setInservice(inservice);
    }
    resultData.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    resultData.setUpdName(BaseContextHandler.getName());
    resultData.setUpdTime(new Date(System.currentTimeMillis()));
    resultData.setId(id);
    mapper.updateByPrimaryKeySelective(resultData);
  }

  /**
   * 删除部门模版
   *
   * @param departmentId 部门模版ID
   */
  public void deleteDepartment(Integer departmentId) {
    CompanyDepartment entity = new CompanyDepartment();
    entity.setDepartmentId(departmentId);
    List<CompanyDepartment> companyDepartments = companyDepartmentMapper.select(entity);
    if (companyDepartments.size() > 0) {
      throw new ClientServiceException(
          "删除ID为'" + departmentId + "'的部门失败，该部门已被使用", OperationCodeConstants.DELETE_NOT_ALLOW);
    }
    mapper.deleteByPrimaryKey(departmentId);
  }
}
