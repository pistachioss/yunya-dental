package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.expand.RemoteClinicEmployeeConfigFeign;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.system.form.EmployeeInfoQueryForm;
import com.yunya.feign.system.vo.EmployeeInfoVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.system.SysUserPost;
import com.yunya.modules.system.domain.form.LoginOrganizationForm;
import com.yunya.modules.system.domain.model.SysUserPostModel;
import com.yunya.modules.system.mapper.SysUserPostMapper;
import com.yunya.modules.system.vo.PostVO;
import com.yunya.modules.system.vo.SysUserLoginOrgVO;
import com.yunya.modules.system.vo.SysUserPostOrgVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.yunya.feign.report.enums.MsgCategoryEnum.BaseUserPost;
import static com.yunya.framework.common.constant.OperationCodeConstants.QUERY_RESULT_INVALID;
import static com.yunya.framework.common.constant.OperationCodeConstants.SAME_DATA_EXIST;

/**
 * 简单介绍:</br> 用户可登陆组织业务层
 *
 * @author: chow
 * @date: 2020/6/18 11:22
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysUserPostBiz extends BaseBiz<SysUserPostMapper, SysUserPost> {

  /** 消息中间件调用 */
  @Autowired private RemoteRabbitMqServiceFeign rabbitMqServiceFeign;
  /** 注入对象 */
  @Autowired private SysUserPostMapper sysUserPostMapper;
  /** 门诊员工配置 */
  @Autowired private RemoteClinicEmployeeConfigFeign clinicEmployeeConfigFeign;

  /**
   * 新增用户可登录组织信息
   *
   * @param resource 参数封装
   */
  public void add(SysUserPostModel resource) {
    Integer userId = resource.getUserId();
    // checkUserOrgDeptUnique(userId, resource.getDepartmentId());
    checkUserOrgDeptPostUnique(
        userId, resource.getCompanyId(), resource.getDepartmentId(), resource.getPostId());
    checkUserOrgPostUnique(userId, resource.getCompanyId(), resource.getPostId());
    SysUserPost entity = new SysUserPost();
    BeanUtils.copyProperties(resource, entity);
    entity.setGroupId(resource.getPostGroupId());
    entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    entity.setCrtName(BaseContextHandler.getName());
    int i = mapper.insertSelective(entity);
    if (i > 0) {
      // 发送消息同步员工信息
      rabbitMqServiceFeign.sendMessage(entity.getId(), 0, BaseUserPost);
    }
  }

  /**
   * 校验用户在统一组织，同一部门，同一岗位下是否唯一
   *
   * @param userId 用户ID
   * @param companyId 组织ID
   * @param departmentId 组织部门ID
   * @param postId 岗位ID
   */
  private void checkUserOrgDeptPostUnique(
      Integer userId, Integer companyId, Integer departmentId, Integer postId) {
    Integer postCount =
        sysUserPostMapper.checkUserOrgDeptPostUnique(userId, companyId, departmentId, postId);
    if (postCount > 0) {
      throw new ClientServiceException("同一组织同一部门同一岗位下不能添加同一员工", SAME_DATA_EXIST);
    }
  }

  /**
   * 校验用户在同一组织下、同一岗位下是否唯一
   *
   * @param userId 用户ID
   * @param companyId 组织ID
   * @param postId 岗位ID
   */
  private void checkUserOrgPostUnique(Integer userId, Integer companyId, Integer postId) {
    Integer postCount = sysUserPostMapper.checkOrgPostUnique(userId, companyId, postId);
    if (postCount > 0) {
      throw new ClientServiceException("同一组织同一岗位下不能添加同一员工", SAME_DATA_EXIST);
    }
  }

  /**
   * 校验用户在同一组织、同一部门下是否唯一
   *
   * @param userId 用户ID
   * @param departmentId 组织部门ID
   */
  private void checkUserOrgDeptUnique(Integer userId, Integer departmentId) {
    Integer deptCount = sysUserPostMapper.checkOrgDeptUnique(userId, departmentId);
    if (deptCount > 0) {
      throw new ClientServiceException("同一组织同一部门下不能添加同一员工", SAME_DATA_EXIST);
    }
  }

  /**
   * 修改用户可登陆组织信息
   *
   * @param userPostId 用户可登陆组织ID
   * @param form 参数封装
   */
  public void edit(Integer userPostId, LoginOrganizationForm form) {
    SysUserPost sysUserPost = mapper.selectByPrimaryKey(userPostId);
    if (null == sysUserPost) {
      throw new ClientServiceException(
          "修改用户可登陆组织失败，ID为'" + userPostId + "'数据不存在", QUERY_RESULT_INVALID);
    }
    if (!sysUserPost.getDepartmentId().equals(form.getOrgDeptId())
        || !sysUserPost.getPostId().equals(form.getPostId())
        || !sysUserPost.getCompanyId().equals(form.getOrgId())) {
      checkUserOrgDeptPostUnique(
          sysUserPost.getUserId(), form.getOrgId(), form.getOrgDeptId(), form.getPostId());
    }
    /*if (!sysUserPost.getDepartmentId().equals(form.getOrgDeptId())) {
      checkUserOrgDeptUnique(sysUserPost.getUserId(), form.getOrgDeptId());
    }*/
    if (!sysUserPost.getPostId().equals(form.getPostId())
        || !sysUserPost.getCompanyId().equals(form.getOrgId())) {
      checkUserOrgPostUnique(sysUserPost.getUserId(), form.getOrgId(), form.getPostId());
    }
    sysUserPost.setDepartmentId(form.getOrgDeptId());
    sysUserPost.setCompanyId(form.getOrgId());
    sysUserPost.setPostId(form.getPostId());
    sysUserPost.setGroupId(form.getPostGroupId());
    sysUserPost.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    sysUserPost.setUpdName(BaseContextHandler.getName());
    int i = mapper.updateByPrimaryKeySelective(sysUserPost);
    if (i > 0) {
      // 发送消息同步员工信息
      rabbitMqServiceFeign.sendMessage(userPostId, 1, BaseUserPost);
    }
  }

  /**
   * 根据ID删除用户可登陆组织
   *
   * @param userPostId 可登陆组织ID
   */
  public void remove(Integer userPostId) {
    SysUserPost sysUserPost = mapper.selectByPrimaryKey(userPostId);
    if (sysUserPost != null) {
      int i = mapper.deleteByPrimaryKey(userPostId);
      if (i > 0) {
        // 删除可挂号可预约医生信息
        this.clinicEmployeeConfigFeign.deleteClinicEmployeeConfig(
            sysUserPost.getUserId(), sysUserPost.getCompanyId());
        // 发送消息同步员工可登录组织信息
        rabbitMqServiceFeign.sendMessage(userPostId, 2, BaseUserPost);
      }
    }
  }

  /**
   * 根据条件查询用户信息
   *
   * @param queryForm 查询条件
   * @return
   */
  public PageInfo<EmployeeInfoVO> findEmployeeList(EmployeeInfoQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<EmployeeInfoVO> resultList = mapper.selectEmployeeList(queryForm);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据用户ID获取用户可登陆组织列表
   *
   * @param userId 用户ID
   * @return
   */
  public List<SysUserLoginOrgVO> getUserLoginListByUserId(Integer userId) {
    List<SysUserLoginOrgVO> resultList = mapper.selectListByUserId(userId);
    return resultList;
  }

  /**
   * 根据用户ID获取用户可登陆组织列表(员工管理-可登录组织)
   *
   * @param userId 用户ID
   * @return
   */
  public List<SysUserPostOrgVO> getUserPostListByUserId(Integer userId) {
    List<SysUserPostOrgVO> resultList = mapper.selectUserPostListByUserId(userId);
    return resultList;
  }

  /**
   * 根据用户ID，组织ID查询用户岗位列表
   *
   * @param orgId 组织ID
   * @param userId 用户ID
   * @return
   */
  public List<PostVO> findUserPostList(Integer orgId, Integer userId) {
    List<PostVO> resultList = mapper.selectPostList(orgId, userId);
    return resultList;
  }

  /**
   * 查询可登陆组织的员工列表
   *
   * @param queryForm 查询条件
   * @return List<EmployeeInfoVO>
   */
  public List<EmployeeInfoVO> findEnableLoginEmployeeList(EmployeeInfoQueryForm queryForm) {
    return mapper.selectEmployeeList(queryForm);
  }
}
