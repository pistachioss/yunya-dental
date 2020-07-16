package com.yunya.modules.system.rpc;

import com.yunya.feign.system.form.*;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.feign.system.vo.UserInfo;
import com.yunya.modules.system.biz.*;
import com.yunya.modules.system.entity.*;
import com.yunya.modules.system.rpc.service.PermissionService;
import com.yunya.modules.system.vo.OrganizationInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 简介: 系统管理服务对外接口暴露
 *
 * @author: chow
 * @date: 2020/7/15 10:24
 * @description:
 * @since: 1.0.0
 */
@RestController
@RequestMapping("api")
public class SystemServiceRest {

  /** 权限 */
  @Autowired private PermissionService permissionService;
  /** 品牌 */
  @Autowired private BrandBiz brandBiz;
  /** 部门 */
  @Autowired private DepartmentBiz departmentBiz;
  /** 字典明细 */
  @Autowired private DictionaryItemBiz dictionaryItemBiz;
  /** 字典类型 */
  @Autowired private DictionaryTypeBiz dictionaryTypeBiz;
  /** 组织 */
  @Autowired private OrganizationBiz organizationBiz;
  /** 组织部门 */
  @Autowired private CompanyDepartmentBiz companyDepartmentBiz;
  /** 岗位 */
  @Autowired private PostBiz postBiz;
  /***/
  /***/

  /**
   * 根据用户名、密码查询用户信息
   *
   * @param params 参数封装
   * @return UserInfoVO
   */
  @RequestMapping(value = "/user/validate", method = RequestMethod.POST)
  public UserInfo validate(@RequestBody Map<String, String> params) {
    return permissionService.validate(params.get("username"), params.get("password"));
  }

  /**
   * 根据ID查询品牌
   *
   * @param id 品牌ID
   * @return
   */
  @RequestMapping(value = "/brand/one/{id}", method = RequestMethod.GET)
  public Brand findBrandById(@PathVariable Integer id) {
    return brandBiz.selectById(id);
  }

  /**
   * 根据条件获取品牌列表
   *
   * @param model 查询条件
   * @return
   */
  @RequestMapping(value = "/brand/list", method = RequestMethod.POST)
  public List<Brand> findBrandList(@RequestBody BrandModel model) {
    Brand brand = new Brand();
    BeanUtils.copyProperties(model, brand);
    return brandBiz.selectByObj(brand);
  }

  /**
   * 根据ID查询部门
   *
   * @param id 部门ID
   * @return
   */
  @RequestMapping(value = "/department/{id}", method = RequestMethod.GET)
  public Department findDepartmentById(@PathVariable Integer id) {
    return departmentBiz.selectById(id);
  }

  /**
   * 根据条件查询部门列表
   *
   * @param model 查询条件
   * @return
   */
  @RequestMapping(value = "/department/list", method = RequestMethod.POST)
  public List<Department> findDepartmentList(@RequestBody DepartmentModel model) {
    Department department = new Department();
    BeanUtils.copyProperties(model, department);
    return departmentBiz.selectByObj(department);
  }

  /**
   * 根据ID查询字典明细
   *
   * @param id 字典明细ID
   * @return
   */
  @RequestMapping(value = "/dictionary/{id}", method = RequestMethod.GET)
  public DictionaryItem findDictionaryItemById(@PathVariable Integer id) {
    return dictionaryItemBiz.selectById(id);
  }

  /**
   * 根据条件查询字典明细列表
   *
   * @param model 查询条件
   * @return
   */
  @RequestMapping(value = "/dictionary/list", method = RequestMethod.POST)
  public List<DictionaryItem> findDictionaryItemList(@RequestBody DictionaryItemModel model) {
    DictionaryItem dictItem = new DictionaryItem();
    BeanUtils.copyProperties(model, dictItem);
    return dictionaryItemBiz.selectByObj(dictItem);
  }

  /**
   * 根据ID查询字典类型
   *
   * @param id 字典类型ID
   * @return
   */
  @RequestMapping(value = "/dict/{id}", method = RequestMethod.GET)
  public DictionaryType findDictionaryTypeById(@PathVariable Integer id) {
    return dictionaryTypeBiz.selectById(id);
  }

  /**
   * 根据条件查询字典类型列表
   *
   * @param model 查询条件
   * @return
   */
  @RequestMapping(value = "/dict/list", method = RequestMethod.GET)
  public List<DictionaryType> findDictionaryTypeList(@RequestBody DictionaryTypeModel model) {
    DictionaryType dictType = new DictionaryType();
    BeanUtils.copyProperties(model, dictType);
    return dictionaryTypeBiz.selectByObj(dictType);
  }

  /**
   * 根据组织ID查询组织信息
   *
   * @param id 组织ID
   * @return
   */
  @RequestMapping(value = "/organization/{id}", method = RequestMethod.GET)
  public OrganizationInfo findOrgInfoByOrgId(@PathVariable Integer id) {
    return organizationBiz.findOrgInfoById(id);
  }

  /**
   * 根据条件查询组织信息
   *
   * @param model 查询条件
   * @return
   */
  @RequestMapping(value = "/organization/list", method = RequestMethod.POST)
  public List<OrganizationInfoVO> findOrgInfoList(@RequestBody OrganizationModel model) {
    return organizationBiz.findOrgInfoList(model);
  }

  /**
   * 根据组织部门ID查询组织部门信息
   *
   * @param id 组织部门ID
   * @return obj
   */
  @RequestMapping(value = "/org/dept/{id}", method = RequestMethod.GET)
  public CompanyDepartment findCompanyDepartmentById(@PathVariable Integer id) {
    return companyDepartmentBiz.selectById(id);
  }

  /**
   * 根据条件查询组织部门列表
   *
   * @param model 查询参数
   * @return list
   */
  @RequestMapping(value = "/org/dept/list", method = RequestMethod.POST)
  public List<CompanyDepartment> findCompanyDepartmentList(@RequestBody OrgDepartmentModel model) {
    CompanyDepartment department = new CompanyDepartment();
    BeanUtils.copyProperties(model, department);
    return companyDepartmentBiz.selectByObj(department);
  }

  /**
   * 根据岗位ID查询岗位信息
   *
   * @param id 岗位ID
   * @return obj
   */
  @RequestMapping(value = "/post/one/{id}", method = RequestMethod.GET)
  public Post findPostById(@PathVariable Integer id) {
    return postBiz.selectById(id);
  }

  /**
   * 根据条件查询岗位信息列表
   *
   * @param model 查询条件
   * @return list
   */
  @RequestMapping(value = "/post/list", method = RequestMethod.POST)
  public List<Post> findPostList(@RequestBody PostModel model) {
    Post post = new Post();
    BeanUtils.copyProperties(model, post);
    return postBiz.selectByObj(post);
  }
}
