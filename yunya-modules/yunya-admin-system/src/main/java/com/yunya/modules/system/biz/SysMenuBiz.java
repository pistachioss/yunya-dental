package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.TreeUtil;
import com.yunya.models.system.SysElement;
import com.yunya.models.system.SysMenu;
import com.yunya.modules.system.form.MenuElementForm;
import com.yunya.modules.system.form.ResourceAuthorityForm;
import com.yunya.modules.system.form.UserResourceForm;
import com.yunya.modules.system.form.query.SysMenuQueryForm;
import com.yunya.modules.system.mapper.SysMenuMapper;
import com.yunya.modules.system.vo.PostVO;
import com.yunya.modules.system.vo.SysMenuVO;
import com.yunya.modules.system.vo.SysResourceAuthorityVO;
import com.yunya.modules.system.vo.tree.SysMenuElementTreeVO;
import com.yunya.modules.system.vo.tree.SysMenuTreeVO;
import org.apache.commons.lang3.StringUtils;
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
 * 简单介绍:</br> 系统菜单业务层
 *
 * @author: chow
 * @date: 2020/6/19 13:28
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysMenuBiz extends BaseBiz<SysMenuMapper, SysMenu> {

  /** 注入对象 */
  private final SysElementBiz sysElementBiz;

  private final SysUserPostBiz sysUserPostBiz;

  private final SysResourceAuthorityBiz sysResourceAuthorityBiz;

  public SysMenuBiz(
      SysElementBiz sysElementBiz,
      SysUserPostBiz sysUserPostBiz,
      SysResourceAuthorityBiz sysResourceAuthorityBiz) {
    this.sysElementBiz = sysElementBiz;
    this.sysUserPostBiz = sysUserPostBiz;
    this.sysResourceAuthorityBiz = sysResourceAuthorityBiz;
  }

  /**
   * 根据条件查询(可分页)
   *
   * @param queryForm 参数封装
   * @return list
   */
  public PageInfo<SysMenuVO> findList(SysMenuQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<SysMenuVO> resultList = mapper.selectSysMenuListByExample(queryForm);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据用户ID查询用户菜单权限列表
   *
   * @param userId 用户ID
   * @return
   */
  public List<SysMenu> getUserAuthorityMenuByUserId(Integer userId) {
    return null;
  }

  /**
   * 新增系统菜单
   *
   * @param resource 参数封装
   */
  public void addSysMenu(SysMenu resource) {
    if (null == resource) {
      return;
    }
    SysMenu sysMenu = EntityUtils.build(resource, SysMenu.class);
    if (StringUtils.isBlank(resource.getHref())) {
      resource.setType(BusinessConstants.RESOURCE_TYPE_DIRT);
    } else {
      resource.setType(BusinessConstants.RESOURCE_TYPE_MENU);
    }
    SysMenu resultData = mapper.selectOne(sysMenu);
    if (null == resultData) {
      mapper.insertSelective(resource);
    } else {
      resource.setId(resultData.getId());
      mapper.updateByPrimaryKeySelective(resource);
    }
  }

  /**
   * 更新系统菜单
   *
   * @param id 菜单ID
   * @param resource 参数封装
   */
  public void edit(Integer id, SysMenu resource) {
    SysMenu menu = mapper.selectByPrimaryKey(id);
    if (null != menu) {
      SysMenu sysMenu = EntityUtils.build(resource, SysMenu.class);
      sysMenu.setId(menu.getId());
      mapper.updateByPrimaryKeySelective(sysMenu);
    }
  }

  /**
   * 删除菜单
   *
   * @param id 菜单ID
   */
  public void deleteSysMenu(Integer id) {
    SysMenu entity = new SysMenu();
    entity.setParentId(id);
    List<SysMenu> menus = mapper.select(entity);
    SysElement element = new SysElement();
    element.setMenuId(id);
    List<SysElement> elements = sysElementBiz.selectList(element);
    if (menus.size() > 0 || elements.size() > 0) {
      return;
    }
    mapper.deleteByPrimaryKey(id);
  }

  /**
   * 根据条件查询菜单功能列表树
   *
   * @param queryForm 参数封装
   * @return
   */
  public List<SysMenuElementTreeVO> findMenuElementTree(MenuElementForm queryForm) {
    List<SysMenuElementTreeVO> resultList = mapper.selectMenuElementTreeByExample(queryForm);
    return initMenuElementTree(resultList);
  }

  /**
   * 构建菜单及页面功能列表树
   *
   * @param resultList 菜单列表
   * @return
   */
  private List<SysMenuElementTreeVO> initMenuElementTree(List<SysMenuElementTreeVO> resultList) {
    List<SysMenuElementTreeVO> trees = new ArrayList<>();
    if (resultList.size() > 0) {
      SysMenuElementTreeVO node;
      for (SysMenuElementTreeVO vo : resultList) {
        node = new SysMenuElementTreeVO();
        BeanUtils.copyProperties(vo, node);
        trees.add(node);
      }
    }
    return TreeUtil.buildByRecursive(trees, BusinessConstants.DEFAULT_PARENT_ID);
  }

  /**
   * 查询系统菜单列表树
   *
   * @param title 菜单名称
   * @return
   */
  public List<SysMenuTreeVO> getMenuTreeByExample(String title) {
    Example example = new Example(SysMenu.class);
    if (StringUtils.isNoneBlank(title)) {
      example.createCriteria().andLike("title", "%" + title + "%");
    }
    List<SysMenu> sysMenus = mapper.selectByExample(example);
    return initTree(sysMenus);
  }

  /**
   * 构建系统菜单列表树
   *
   * @param sysMenus 菜单列表
   * @return
   */
  private List<SysMenuTreeVO> initTree(List<SysMenu> sysMenus) {
    ArrayList<SysMenuTreeVO> trees = new ArrayList<>();
    if (sysMenus.size() > 0) {
      SysMenuTreeVO node;
      for (SysMenu vo : sysMenus) {
        node = new SysMenuTreeVO();
        BeanUtils.copyProperties(vo, node);
        trees.add(node);
      }
    }
    return TreeUtil.buildByRecursive(trees, BusinessConstants.DEFAULT_PARENT_ID);
  }

  /**
   * 根据用户ID、组织ID获取用户在该组织的菜单权限列表
   *
   * @param resourceForm 参数封装
   * @return
   */
  public List<SysMenuTreeVO> getUserMenuResourceList(UserResourceForm resourceForm) {
    // 查询用户在该组织下的所有岗位列表
    Integer orgId = resourceForm.getOrgId();
    Integer userId = resourceForm.getUserId();
    // 将用户登陆组织设置到线程局部变量
    BaseContextHandler.setOrgId(orgId.toString());
    List<PostVO> posts = sysUserPostBiz.findUserPostList(orgId, userId);
    List<SysMenu> authorityList = getPostMenuResourceAuthorityList(posts);
    return initTree(authorityList);
  }

  /**
   * 查询岗位对应的菜单列表
   *
   * @param posts 岗位列表
   */
  private List<SysMenu> getPostMenuResourceAuthorityList(List<PostVO> posts) {
    ArrayList<SysMenu> menus = new ArrayList<>();
    if (posts.size() > 0) {
      Set<SysResourceAuthorityVO> hashSet = new HashSet<>();
      // 获取岗位的菜单权限列表
      ResourceAuthorityForm form = new ResourceAuthorityForm();
      form.setResourceType((byte) 0);
      posts.forEach(
          post -> {
            form.setPostId(post.getId());
            List<SysResourceAuthorityVO> authorityList =
                sysResourceAuthorityBiz.findResourceAuthorityList(form);
            hashSet.addAll(authorityList);
          });
      if (hashSet.size() > 0) {
        menus =
            hashSet.stream()
                .filter(vo -> vo.getResourceType() == 0)
                .map(vo -> mapper.selectByPrimaryKey(Integer.valueOf(vo.getResourceId())))
                .collect(Collectors.toCollection(ArrayList::new));
      }
    }
    return menus;
  }
}
