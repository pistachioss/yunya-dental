package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.TreeUtil;
import com.yunya.models.system.Post;
import com.yunya.models.system.PostGroup;
import com.yunya.modules.system.domain.form.PostGroupForm;
import com.yunya.modules.system.domain.model.PostGroupModel;
import com.yunya.modules.system.domain.query.PostGroupQueryForm;
import com.yunya.modules.system.mapper.PostGroupMapper;
import com.yunya.modules.system.mapper.PostMapper;
import com.yunya.modules.system.vo.PostGroupVO;
import com.yunya.modules.system.vo.tree.PostGroupTreeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 简单介绍:</br> 岗位组业务层
 *
 * @author: chow
 * @date: 2020/6/10 10:48
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PostGroupBiz extends BaseBiz<PostGroupMapper, PostGroup> {

  /** 注入对象 */
  @Autowired private PostMapper postMapper;

  /**
   * 根据条件查询岗位组列表
   *
   * @param queryForm 参数封装
   * @return list
   */
  public PageInfo<PostGroupVO> findList(PostGroupQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<PostGroupVO> resultList = mapper.selectPostGroupByExample(queryForm);
    return new PageInfo<>(resultList);
  }

  /**
   * 查询岗位组树
   *
   * @return list
   */
  public List<PostGroupTreeVO> initPostGroupTree() {
    List<PostGroupVO> voList = mapper.selectPostGroupByExample(new PostGroupQueryForm());
    return initTree(voList);
  }

  /**
   * 初始化岗位组树列表
   *
   * @param vos 参数列表
   * @return list
   */
  private List<PostGroupTreeVO> initTree(List<PostGroupVO> vos) {
    List<PostGroupTreeVO> trees = new ArrayList<>();
    if (vos.size() > 0) {
      PostGroupTreeVO node;
      for (PostGroupVO vo : vos) {
        node = new PostGroupTreeVO();
        BeanUtils.copyProperties(vo, node);
        trees.add(node);
      }
    }
    return TreeUtil.buildByRecursive(trees, BusinessConstants.DEFAULT_PARENT_ID);
  }

  /**
   * 新增岗位组
   *
   * @param resource 参数封装
   */
  public void add(PostGroupModel resource) {
    Integer parentId = resource.getParentId();
    PostGroup postGroup = mapper.selectByPrimaryKey(parentId);
    if (null == postGroup) {
      throw new ClientServiceException(
          "新增岗位组失败，岗位分类ID'" + parentId + "'为非法参数", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }
    String name = resource.getName();
    PostGroup entity = new PostGroup();
    entity.setName(name);
    int count = mapper.selectCount(entity);
    if (count > 0) {
      throw new ClientServiceException(
          "新增岗位组'" + name + "'失败，该岗位组名称已存在", OperationCodeConstants.NAME_IS_OCCUPIED);
    }
    entity.setParentId(parentId);
    Boolean allowOperation = resource.getAllowOperation();
    entity.setAllowOperation(allowOperation);
    Integer orderNum = resource.getOrderNum();
    entity.setOrderNum(orderNum);
    entity.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
    entity.setCrtName(BaseContextHandler.getName());
    mapper.insertSelective(entity);
  }

  /**
   * 岗位组编辑
   *
   * @param id 岗位组ID
   * @param form 参数封装
   */
  public void edit(Integer id, PostGroupForm form) {
    PostGroup postGroup = mapper.selectByPrimaryKey(id);
    checkPostGroupEdit(id, postGroup);
    Integer parentId = checkPostGroupForm(form, postGroup);
    postGroup.setParentId(parentId);
    postGroup.setOrderNum(form.getOrderNum());
    if (null != form.getInservice()) {
      postGroup.setInservice(form.getInservice());
    }
    postGroup.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    postGroup.setUpdName(BaseContextHandler.getName());
    postGroup.setUpdTime(new Date(System.currentTimeMillis()));
    mapper.updateByPrimaryKeySelective(postGroup);
  }

  /**
   * 检查岗位组修改参数
   *
   * @param form 参数封装
   * @param postGroup 岗位组信息
   * @return
   */
  private Integer checkPostGroupForm(PostGroupForm form, PostGroup postGroup) {
    if (!postGroup.getName().equals(form.getName())) {
      String name = form.getName();
      PostGroup entity = new PostGroup();
      entity.setName(name);
      int count = mapper.selectCount(entity);
      if (count > 0) {
        throw new ClientServiceException(
            "修改岗位组'" + name + "'失败，该岗位组名称已存在", OperationCodeConstants.NAME_IS_OCCUPIED);
      }
      postGroup.setName(name);
    }
    Integer parentId = form.getParentId();
    PostGroup postResult = mapper.selectByPrimaryKey(parentId);
    if (null == postResult) {
      throw new ClientServiceException(
          "修改岗位组失败，'" + parentId + "'为非法参数", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
    }
    return parentId;
  }

  /**
   * 校验岗位组能否被修改
   *
   * @param id 岗位组ID
   * @param postGroup 岗位组信息
   */
  private void checkPostGroupEdit(Integer id, PostGroup postGroup) {
    if (null == postGroup) {
      throw new ClientServiceException(
          "修改岗位组失败，岗位ID为'" + id + "'的数据不存在", OperationCodeConstants.QUERY_RESULT_INVALID);
    }
    // 判断是否顶级岗位组或是默认岗位组
    if (BusinessConstants.DEFAULT_PARENT_ID.equals(postGroup.getParentId())
        || !postGroup.getAllowOperation()) {
      throw new ClientServiceException(
          "修改岗位组失败，当前岗位组属于默认岗位组，不可被修改", OperationCodeConstants.OBJECT_EDIT_FAIL);
    }
  }

  /**
   * 根据ID删除岗位组信息
   *
   * @param id 岗位组ID
   */
  public void deletePostGroup(Integer id) {
    checkDelete(id);
    mapper.deleteByPrimaryKey(id);
  }

  /**
   * 检查岗位组能否被删除
   *
   * @param id 岗位组ID
   */
  private void checkDelete(Integer id) {
    PostGroup postGroup = mapper.selectByPrimaryKey(id);
    if (!postGroup.getAllowOperation()
        || BusinessConstants.DEFAULT_PARENT_ID.equals(postGroup.getParentId())) {
      throw new ClientServiceException(
          "删除岗位组失败，当前岗位组属于默认岗位组", OperationCodeConstants.DELETE_NOT_ALLOW);
    }
    PostGroup entity = new PostGroup();
    entity.setParentId(id);
    List<PostGroup> groups = mapper.select(entity);
    Post post = new Post();
    post.setPostGroupId(id);
    List<Post> posts = postMapper.select(post);
    if (groups.size() > 0 || posts.size() > 0) {
      throw new ClientServiceException(
          "删除ID为" + id + "'失败，该岗位组已被使用", OperationCodeConstants.DELETE_NOT_ALLOW);
    }
  }
}
