package com.yunya.feign.system;

import com.yunya.feign.system.factory.RemoteSystemServiceFallBackFactory;
import com.yunya.feign.system.form.*;
import com.yunya.feign.system.vo.*;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.models.system.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Set;

/**
 * 简单介绍: 云牙系统基础服务接口调用
 *
 * @author: chow
 * @date: 2020/7/5 15:47
 * @description:
 * @since: 1.0.0
 */
@FeignClient(
    name = YunyaServiceNameConstants.YUNYA_SYSTEM_SERVICE,
    fallbackFactory = RemoteSystemServiceFallBackFactory.class)
public interface RemoteSystemServiceFeign {

  /**
   * 校验用户合法性
   *
   * @param jwtRequestFrom 参数封装
   * @return
   */
  @RequestMapping(value = "/api/user/validate", method = RequestMethod.POST)
  UserInfo validate(@RequestBody JwtRequestFrom jwtRequestFrom);

  /**
   * 根据ID查询品牌
   *
   * @param id 品牌ID
   * @return
   */
  @RequestMapping(value = "/api/brand/one/{id}", method = RequestMethod.GET)
  Brand findBrandById(@PathVariable("id") Integer id);

  /**
   * 根据条件获取品牌列表
   *
   * @param model 查询条件
   * @return
   */
  @RequestMapping(value = "/api/brand/list", method = RequestMethod.POST)
  List<Brand> findBrandList(@RequestBody BrandModel model);

  /**
   * 根据ID查询部门
   *
   * @param id 部门ID
   * @return
   */
  @RequestMapping(value = "/api/department/{id}", method = RequestMethod.GET)
  Department findDepartmentById(@PathVariable("id") Integer id);

  /**
   * 根据条件查询部门列表
   *
   * @param model 查询条件
   * @return
   */
  @RequestMapping(value = "/api/department/list", method = RequestMethod.POST)
  List<Department> findDepartmentList(@RequestBody DepartmentModel model);

  /**
   * 根据ID查询字典明细
   *
   * @param id 字典明细ID
   * @return
   */
  @RequestMapping(value = "/api/dictionary/{id}", method = RequestMethod.GET)
  DictionaryItem findDictionaryItemById(@PathVariable(value = "id") Integer id);

  /**
   * 根据条件查询字典明细列表
   *
   * @param model 查询条件
   * @return
   */
  @RequestMapping(value = "/api/dictionary/list", method = RequestMethod.POST)
  List<DictionaryItem> findDictionaryItemList(@RequestBody DictionaryItemModel model);

  /**
   * 根据ID查询字典类型
   *
   * @param id 字典类型ID
   * @return
   */
  @RequestMapping(value = "/api/dict/{id}", method = RequestMethod.GET)
  DictionaryType findDictionaryTypeById(@PathVariable(value = "id") Integer id);

  /**
   * 根据条件查询字典类型列表
   *
   * @param model 查询条件
   * @return
   */
  @RequestMapping(value = "/api/dict/list", method = RequestMethod.GET)
  List<DictionaryType> findDictionaryTypeList(@RequestBody DictionaryTypeModel model);

  /**
   * 根据组织ID查询组织信息
   *
   * @param id 组织ID
   * @return
   */
  @RequestMapping(value = "/api/organization/{id}", method = RequestMethod.GET)
  OrganizationInfo findOrgInfoByOrgId(@PathVariable(value = "id") Integer id);

  /**
   * 根据条件查询组织信息
   *
   * @param model 查询条件
   * @return
   */
  @RequestMapping(value = "/api/organization/list", method = RequestMethod.POST)
  List<OrganizationInfoVO> findOrgInfoList(@RequestBody OrganizationModel model);

  /**
   * 根据组织部门ID查询组织部门信息
   *
   * @param id 组织部门ID
   * @return obj
   */
  @RequestMapping(value = "/api/org/dept/{id}", method = RequestMethod.GET)
  CompanyDepartment findCompanyDepartmentById(@PathVariable(value = "id") Integer id);

  /**
   * 根据条件查询组织部门列表
   *
   * @param model 查询参数
   * @return list
   */
  @RequestMapping(value = "/api/org/dept/list", method = RequestMethod.POST)
  List<CompanyDepartment> findCompanyDepartmentList(@RequestBody OrgDepartmentModel model);

  /**
   * 根据岗位ID查询岗位信息
   *
   * @param id 岗位ID
   * @return obj
   */
  @RequestMapping(value = "/api/post/one/{id}", method = RequestMethod.GET)
  Post findPostById(@PathVariable(value = "id") Integer id);

  /**
   * 根据条件查询岗位信息列表
   *
   * @param model 查询条件
   * @return list
   */
  @RequestMapping(value = "/api/post/list", method = RequestMethod.POST)
  List<Post> findPostList(@RequestBody PostModel model);

  /**
   * 根据用户ID获取用户的权限列表
   *
   * @param userId 用户ID
   * @return
   */
  @RequestMapping(value = "/api/user/one", method = RequestMethod.GET)
  Set<String> selectPermsByUserId(Integer userId);

  /**
   * 获取所有权限列表
   *
   * @return
   */
  @RequestMapping(value = "/api/user/all", method = RequestMethod.GET)
  List<PermissionInfo> getAllPermissionInfo();

  /**
   * 根据用户名获取权限列表
   *
   * @param uniqueName
   * @return
   */
  @RequestMapping(value = "/api/user/get", method = RequestMethod.GET)
  List<PermissionInfo> getPermissionByUsername(String uniqueName);

  /**
   * 保存日志信息
   *
   * @param log
   */
  @RequestMapping(value = "/api/user/save", method = RequestMethod.POST)
  void saveLog(LogInfo log);
}
