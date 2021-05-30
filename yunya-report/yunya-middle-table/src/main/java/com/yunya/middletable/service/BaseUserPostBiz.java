package com.yunya.middletable.service;

import com.yunya.feign.report.domain.form.PullForm;
import com.yunya.feign.report.domain.model.MessageModel;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.dao.employee_expand.ClinicEmployeeConfigMapper;
import com.yunya.middletable.dao.report.BaseUserPostMapper;
import com.yunya.middletable.dao.system.PostGroupMapper;
import com.yunya.middletable.dao.system.PostMapper;
import com.yunya.middletable.dao.system.SysUserPostMapper;
import com.yunya.models.expand.ClinicEmployeeConfig;
import com.yunya.models.report.BaseUserPost;
import com.yunya.models.system.Post;
import com.yunya.models.system.PostGroup;
import com.yunya.models.system.SysUserPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 简介: 中间表用户可登录组织业务层
 *
 * @author: chow
 * @date: 2020/10/16 20:26
 * @description:
 * @since: 1.0.0
 */
@Service
public class BaseUserPostBiz extends BaseBiz<BaseUserPostMapper, BaseUserPost> {

  /** 岗位组 */
  @Autowired private PostGroupMapper groupMapper;
  /** 岗位 */
  @Autowired private PostMapper postMapper;
  /** 用户可登录组织 */
  @Autowired private SysUserPostMapper userPostMapper;
  /** 门诊员工配置 */
  @Autowired private ClinicEmployeeConfigMapper employeeConfigMapper;

  /**
   * 根据消息同步中间表用户可登录组织信息
   *
   * @param msg 消息
   */
  public void operateUserPost(MessageModel msg) {
    Integer dataId = (Integer) msg.getParamMap().get("id");
    BaseUserPost userPost = generateUserPost(dataId);
    Integer operateType = msg.getOperateType();
    switch (operateType) {
      case 0:
        mapper.deleteByPrimaryKey(dataId);
        if (null != userPost) {
          mapper.insertSelective(userPost);
        }
        break;
      case 1:
        if (null != userPost) {
          BaseUserPost result = mapper.selectByPrimaryKey(dataId);
          if (null == result) {
            mapper.deleteByPrimaryKey(dataId);
            mapper.insertSelective(userPost);
          } else {
            mapper.updateByPrimaryKeySelective(userPost);
          }
        } else {
          mapper.deleteByPrimaryKey(dataId);
        }
        break;
      case 2:
        if (null == userPost) {
          mapper.deleteByPrimaryKey(dataId);
        } else {
          mapper.insertSelective(userPost);
        }
        break;
      default:
        break;
    }
  }

  /**
   * 构建中间表用户可登录组织信息
   *
   * @param dataId 用户可登录组织ID
   */
  private BaseUserPost generateUserPost(Integer dataId) {
    SysUserPost userPost = userPostMapper.selectByPrimaryKey(dataId);
    return null != userPost ? setUserPostValue(userPost) : null;
  }

  /**
   * 设置用户可登录组织字段属性
   *
   * @param userPost 用户可登录组织
   * @return
   */
  private BaseUserPost setUserPostValue(SysUserPost userPost) {
    BaseUserPost baseUserPost = new BaseUserPost();
    baseUserPost.setUserPostId(userPost.getId());
    Integer userId = userPost.getUserId();
    baseUserPost.setUserId(userId);
    Integer orgId = userPost.getCompanyId();
    baseUserPost.setOrgId(orgId);
    Integer groupId = userPost.getGroupId();
    baseUserPost.setGroupId(groupId);
    PostGroup postGroup = groupMapper.selectByPrimaryKey(groupId);
    baseUserPost.setPostGroupName(null != postGroup ? postGroup.getName() : "--");
    Integer postId = userPost.getPostId();
    baseUserPost.setPostId(postId);
    Post post = postMapper.selectByPrimaryKey(postId);
    baseUserPost.setPostName(null != post ? post.getName() : "--");
    ClinicEmployeeConfig empConfig = new ClinicEmployeeConfig();
    empConfig.setEmployeeId(userId);
    empConfig.setClinicId(orgId);
    ClinicEmployeeConfig employeeConfig = employeeConfigMapper.selectOne(empConfig);
    baseUserPost.setEnableAppoint(null == employeeConfig || employeeConfig.getEnableAppoint() == 1);
    baseUserPost.setEnableRegistry(
        null == employeeConfig || employeeConfig.getEnableRegistry() == 1);
    return baseUserPost;
  }

  /**
   * 拉取某段时间内的用户可登录组织数据并更新中间表
   *
   * @param form 拉取时间
   */
  public void pullUserPostData(PullForm form) {
    String startDate = form.getStartDate();
    String endDate = form.getEndDate();
    Example emp = new Example(SysUserPost.class);
    emp.createCriteria().andBetween("updTime", startDate, endDate);
    List<SysUserPost> userPosts = userPostMapper.selectByExample(emp);
    if (StringHelper.isNotEmpty(userPosts)) {
      userPosts.forEach(
          us -> {
            mapper.deleteByPrimaryKey(us.getId());
            BaseUserPost entity = setUserPostValue(us);
            mapper.insertSelective(entity);
          });
    }
  }
}
