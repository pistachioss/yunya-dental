package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.TreeUtil;
import com.yunya.framework.common.utils.UUIDUtils;
import com.yunya.models.system.SysElement;
import com.yunya.models.system.SysPostElement;
import com.yunya.modules.system.form.ResourceAuthorityForm;
import com.yunya.modules.system.form.UserResourceForm;
import com.yunya.modules.system.form.query.SysElementQueryForm;
import com.yunya.modules.system.mapper.SysElementMapper;
import com.yunya.modules.system.vo.PostVO;
import com.yunya.modules.system.vo.SysElementVO;
import com.yunya.modules.system.vo.SysResourceAuthorityVO;
import com.yunya.modules.system.vo.tree.SysElementTreeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 简单介绍:</br> 系统菜单按钮业务层
 *
 * @author: chow
 * @date: 2020/6/19 13:29
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysElementBiz extends BaseBiz<SysElementMapper, SysElement> {

  /** 注入对象 */
  private final SysUserPostBiz sysUserPostBiz;

  private final SysPostElementBiz sysPostElementBiz;

  private final SysResourceAuthorityBiz sysResourceAuthorityBiz;

  public SysElementBiz(
      SysPostElementBiz sysPostElementBiz,
      SysUserPostBiz sysUserPostBiz,
      SysResourceAuthorityBiz sysResourceAuthorityBiz) {
    this.sysPostElementBiz = sysPostElementBiz;
    this.sysUserPostBiz = sysUserPostBiz;
    this.sysResourceAuthorityBiz = sysResourceAuthorityBiz;
  }

  /**
   * 新增菜单功能按钮
   *
   * @param resource 参数封装
   */
  public void add(SysElement resource) {
    if (null == resource) {
      return;
    }
    String s = "页面列表";
    SysElement sysElement = EntityUtils.build(resource, SysElement.class);
    if (resource.getName().contains(s)) {
      sysElement.setType(BusinessConstants.RESOURCE_TYPE_URI);
      resource.setType(BusinessConstants.RESOURCE_TYPE_URI);
    } else {
      sysElement.setType(BusinessConstants.RESOURCE_TYPE_BTN);
      resource.setType(BusinessConstants.RESOURCE_TYPE_BTN);
    }

    SysElement resultData = mapper.selectOne(sysElement);
    if (null == resultData) {
      resource.setId(UUIDUtils.generateShortUuid());
      mapper.insertSelective(resource);
    } else {
      resource.setId(resultData.getId());
      mapper.updateByPrimaryKeySelective(resource);
    }
  }

  /**
   * 修改系统功能
   *
   * @param id 功能按钮ID
   * @param resource 参数封装
   */
  public void edit(String id, SysElement resource) {
    SysElement sysElement = mapper.selectByPrimaryKey(id);
    if (null != sysElement) {
      SysElement element = EntityUtils.build(resource, SysElement.class);
      element.setId(sysElement.getId());
      mapper.updateByPrimaryKeySelective(element);
    }
  }

  /**
   * 根据ID删除功能按钮
   *
   * @param id 功能ID
   */
  public void deleteSysElement(String id) {
    SysPostElement entity = new SysPostElement();
    entity.setSysElementId(id);
    List<SysPostElement> elements = sysPostElementBiz.selectList(entity);
    if (elements.size() > 0) {
      return;
    }
    mapper.deleteByPrimaryKey(id);
  }

  /**
   * 根据条件查询菜单按钮列表
   *
   * @param queryForm 查询参数封装
   * @return list
   */
  public PageInfo<SysElementVO> findList(SysElementQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<SysElementVO> vos = mapper.selectListByExample(queryForm);
    return new PageInfo<>(vos);
  }

  /**
   * 根据组织ID、用户ID查询功能权限列表
   *
   * @param resourceForm 参数封装
   * @return
   */
  public List<SysElementVO> findElementResourceList(UserResourceForm resourceForm) {
    // 查询用户在该组织下的所有岗位列表
    Integer orgId = resourceForm.getOrgId();
    Integer userId = resourceForm.getUserId();
    List<PostVO> posts = sysUserPostBiz.findUserPostList(orgId, userId);
    ArrayList<SysElement> elements = getPostElementResourceAuthorityList(posts);
    return EntityUtils.build(elements, SysElementVO.class);
  }

  /**
   * 查询岗位对应的权限列表
   *
   * @param posts 岗位列表
   * @return
   */
  private ArrayList<SysElement> getPostElementResourceAuthorityList(List<PostVO> posts) {
    ArrayList<SysElement> elements = new ArrayList<>();
    if (posts.size() > 0) {
      Set<SysResourceAuthorityVO> hashSet = new HashSet<>();
      // 获取岗位的菜单权限列表
      ResourceAuthorityForm form = new ResourceAuthorityForm();
      form.setResourceType((byte) 1);
      posts.forEach(
          post -> {
            form.setPostId(post.getId());
            List<SysResourceAuthorityVO> authorityList =
                sysResourceAuthorityBiz.findResourceAuthorityList(form);
            hashSet.addAll(authorityList);
          });
      if (hashSet.size() > 0) {
        elements =
            hashSet.stream()
                .map(vo -> mapper.selectByPrimaryKey(vo.getResourceId()))
                .collect(Collectors.toCollection(ArrayList::new));
      }
    }
    return elements;
  }

  /**
   * 获取功能树状列表
   *
   * @return list
   */
  public List<SysElementTreeVO> getElementTreeByExample() {
    Example example = new Example(SysElement.class);
    List<SysElement> sysElements = mapper.selectByExample(example);
    return initTree(sysElements);
  }

  /**
   * 构建功能菜单列表树
   *
   * @param sysElements 功能列表
   * @return list
   */
  private List<SysElementTreeVO> initTree(List<SysElement> sysElements) {
    ArrayList<SysElementTreeVO> trees = new ArrayList<>();
    if (sysElements.size() > 0) {
      SysElementTreeVO node;
      for (SysElement vo : sysElements) {
        node = new SysElementTreeVO();
        BeanUtils.copyProperties(vo, node);
        trees.add(node);
      }
    }
    List<SysElementTreeVO> list =
        TreeUtil.buildByRecursive(trees, BusinessConstants.DEFAULT_PARENT_ID);
    return list;
  }
}
