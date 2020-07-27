package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.TreeUtil;
import com.yunya.models.system.CompanyDepartment;
import com.yunya.modules.system.domain.form.CompanyDepartmentForm;
import com.yunya.modules.system.domain.model.CompanyDepartmentModel;
import com.yunya.modules.system.domain.query.OrgDeptQueryForm;
import com.yunya.modules.system.mapper.CompanyDepartmentMapper;
import com.yunya.modules.system.vo.OrgDeptTreeVO;
import com.yunya.modules.system.vo.OrgDeptVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 简单介绍:</br> 组织部门业务层
 *
 * @author: chow
 * @date: 2020/6/2 19:45
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CompanyDepartmentBiz extends BaseBiz<CompanyDepartmentMapper, CompanyDepartment> {

  /**
   * 查询组织的部门列表
   *
   * @param companyId 组织ID
   * @return
   */
  public List<OrgDeptTreeVO> findDeptTree(Integer companyId) {
    OrgDeptQueryForm queryForm = new OrgDeptQueryForm();
    queryForm.setCompanyId(companyId);
    List<OrgDeptVO> vos = mapper.selectOrganizationDepartmentByExample(queryForm);
    return initTree(vos);
  }

  /**
   * 构建组织部门树
   *
   * @param vos 组织部门列表
   * @return
   */
  private List<OrgDeptTreeVO> initTree(List<OrgDeptVO> vos) {
    List<OrgDeptTreeVO> trees = new ArrayList<>();
    if (vos.size() > 0) {
      OrgDeptTreeVO node;
      for (OrgDeptVO vo : vos) {
        node = new OrgDeptTreeVO();
        BeanUtils.copyProperties(vo, node);
        trees.add(node);
      }
    }
    return TreeUtil.buildByRecursive(trees, BusinessConstants.DEFAULT_PARENT_ID);
  }

  /**
   * 根据条件查询组织部门列表（可分页）
   *
   * @param queryForm 参数封装
   * @return list
   */
  public PageInfo<OrgDeptVO> findList(OrgDeptQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<OrgDeptVO> resultList = mapper.selectOrganizationDepartmentByExample(queryForm);
    return new PageInfo<>(resultList);
  }

  /**
   * 新增组织部门
   *
   * @param resource 参数封装
   */
  public void addCompanyDepartment(CompanyDepartmentModel resource) {
    Integer departmentId = resource.getDepartmentId();
    Integer companyId = resource.getCompanyId();
    CompanyDepartment entity = new CompanyDepartment();
    entity.setDepartmentId(departmentId);
    entity.setCompanyId(companyId);
    int count = mapper.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException(
          "新增部门失败，当前组织已存在该部门", OperationCodeConstants.NAME_IS_OCCUPIED);
    }
    Integer parentId = resource.getParentId();
    if (null != parentId) {
      CompanyDepartment resultData = mapper.selectByPrimaryKey(parentId);
      if (resultData.getDepartmentId().equals(departmentId)) {
        throw new ClientServiceException(
            "新增组织部门失败，当前新增部门与上级部门相同", OperationCodeConstants.SAME_DATA_EXIST);
      }
      entity.setParentId(parentId);
    }
    mapper.insertSelective(entity);
  }

  /**
   * 修改组织部门信息
   *
   * @param id 组织部门ID
   * @param form 封装参数
   */
  public void edit(Integer id, CompanyDepartmentForm form) {
    CompanyDepartment result = mapper.selectByPrimaryKey(id);
    if (null == result) {
      throw new ClientServiceException(
          "修改组织部门，组织部门ID为'" + id + "'的数据不存在", OperationCodeConstants.QUERY_RESULT_INVALID);
    }
    Integer parentId = form.getParentId();
    if (null != parentId && !BusinessConstants.DEFAULT_PARENT_ID.equals(parentId)) {
      Integer departmentId = result.getDepartmentId();
      CompanyDepartment parentResult = mapper.selectByPrimaryKey(parentId);
      if (parentResult.getDepartmentId().equals(departmentId)) {
        throw new ClientServiceException(
            "编辑组织部门失败，上级部门不能是自身", OperationCodeConstants.SAME_DATA_EXIST);
      }
      CompanyDepartment entity = new CompanyDepartment();
      entity.setParentId(parentId);
      List<CompanyDepartment> departments = mapper.select(entity);
      // 检查父级部门列表下是否存在相同部门
      checkChildCompanyDepartment(departmentId, departments);
    }
    result.setParentId(parentId);
    result.setOrderNum(form.getOrderNum());
    mapper.updateByPrimaryKeySelective(result);
  }

  /**
   * 检查同一父级下是否有相同部门
   *
   * @param departmentId 部门ID
   * @param departments 子级列表
   */
  private void checkChildCompanyDepartment(
      Integer departmentId, List<CompanyDepartment> departments) {
    if (departments.size() > 0) {
      departments.stream()
          .filter(department -> department.getDepartmentId().equals(departmentId))
          .forEach(
              department -> {
                throw new ClientServiceException(
                    "编辑组织部门失败，该父级下已存在相同部门", OperationCodeConstants.SAME_DATA_EXIST);
              });
    }
  }

  /**
   * 根据ID删除组织部门
   *
   * @param id 组织部门ID
   */
  public void deleteOrganizationDepartment(Integer id) {
    CompanyDepartment entity = new CompanyDepartment();
    entity.setParentId(id);
    List<CompanyDepartment> departments = mapper.select(entity);
    if (departments.size() > 0) {
      throw new ClientServiceException(
          "删除组织部门失败，该部门下存在子级部门", OperationCodeConstants.DELETE_NOT_ALLOW);
    }
    mapper.deleteByPrimaryKey(id);
  }
}
