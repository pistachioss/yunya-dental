package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.models.system.Post;
import com.yunya.models.system.PostGroup;
import com.yunya.models.system.SysUserPost;
import com.yunya.modules.system.form.PostForm;
import com.yunya.modules.system.form.query.PostQueryForm;
import com.yunya.modules.system.mapper.PostGroupMapper;
import com.yunya.modules.system.mapper.PostMapper;
import com.yunya.modules.system.mapper.SysUserPostMapper;
import com.yunya.modules.system.vo.PostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 简单介绍:</br> 岗位业务层（角色）
 *
 * @author: chow
 * @date: 2020/6/3 11:56
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PostBiz extends BaseBiz<PostMapper, Post> {

  /** 注入对象 */
  @Autowired private PostGroupMapper postGroupMapper;

  @Autowired private SysUserPostMapper sysUserPostMapper;

  /**
   * 岗位列表查询（可分页）
   *
   * @param form 查询参数
   * @return list
   */
  public PageInfo<PostVO> findAll(PostQueryForm form) {
    if (form.getWhetherPage()) {
      PageHelper.startPage(form.getPageNum(), form.getPageSize());
    }
    Integer postGroupId = form.getPostGroupId();
    List<Integer> ids = postGroupMapper.selectChildIdList(postGroupId);
    form.setGroupIds(ids);
    List<PostVO> resultList = mapper.selectBrandByExample(form);
    return new PageInfo<>(resultList);
  }

  /**
   * 新增岗位
   *
   * @param resource 参数封装
   */
  public void add(Post resource) {
    checkPostGroupParam(resource.getPostGroupId());
    checkNameRepeat(resource.getName());
    // resource.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    // resource.setCrtName(BaseContextHandler.getUsername());
    mapper.insertSelective(resource);
  }

  /**
   * 新增检查名称是否重复
   *
   * @param name 岗位名称
   */
  private void checkNameRepeat(String name) {
    Post post = new Post();
    post.setName(name);
    Post result = mapper.selectOne(post);
    if (null != result) {
      throw new ClientServiceException(
          "新增岗位'" + name + "'失败，该岗位名称已存在", OperationCodeConstants.NAME_IS_OCCUPIED);
    }
  }

  /**
   * 检查岗位组参数是否合法
   *
   * @param postGroupId 岗位分类ID
   */
  private void checkPostGroupParam(Integer postGroupId) {
    PostGroup postGroup = postGroupMapper.selectByPrimaryKey(postGroupId);
    if (null == postGroup || BusinessConstants.DEFAULT_PARENT_ID.equals(postGroup.getParentId())) {
      throw new ClientServiceException(
          "新增岗位失败,岗位组ID'" + postGroupId + "', 岗位分类参数不合法",
          OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }
  }

  /**
   * 编辑岗位
   *
   * @param id 岗位ID
   * @param form 参数封装
   */
  public void edit(Integer id, PostForm form) {
    Post post = mapper.selectByPrimaryKey(id);
    if (null == post) {
      throw new ClientServiceException(
          "修改岗位，岗位ID为'" + id + "'的数据不存在", OperationCodeConstants.QUERY_RESULT_INVALID);
    }
    if (!post.getAllowOperation()) {
      throw new ClientServiceException(
          "修改岗位失败，当前岗位属于默认岗位,不可被修改", OperationCodeConstants.OBJECT_EDIT_FAIL);
    }
    String formName = form.getName();
    if (!post.getName().equals(formName)) {
      Post entity = new Post();
      entity.setName(formName);
      Post result = mapper.selectOne(entity);
      if (null != result) {
        throw new ClientServiceException(
            "修改岗位'" + formName + "'失败，该岗位名称已存在", OperationCodeConstants.NAME_IS_OCCUPIED);
      }
      post.setName(formName);
    }
    post.setPostGroupId(form.getPostGroupId());
    if (null != form.getInservice()) {
      post.setInservice(form.getInservice());
    }
    mapper.updateByPrimaryKeySelective(post);
  }

  /**
   * 删除岗位
   *
   * @param id 岗位ID
   */
  public void deletePost(Integer id) {
    Post post = mapper.selectByPrimaryKey(id);
    if (!post.getAllowOperation()) {
      throw new ClientServiceException(
          "删除岗位失败，当前岗位属于默认岗位，不可被删除", OperationCodeConstants.DELETE_NOT_ALLOW);
    }
    SysUserPost entity = new SysUserPost();
    entity.setPostId(id);
    List<SysUserPost> userPosts = sysUserPostMapper.select(entity);
    if (userPosts.size() > 0) {
      throw new ClientServiceException(
          "删除ID为'" + id + "'的岗位失败，该岗位已被使用", OperationCodeConstants.DELETE_NOT_ALLOW);
    }
    mapper.deleteByPrimaryKey(id);
  }
}
