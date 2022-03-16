package com.yunya.feign.system;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.system.factory.RemoteSystemServiceFallBackFactory;
import com.yunya.feign.system.form.*;
import com.yunya.feign.system.vo.*;
import com.yunya.framework.common.constant.YunyaServiceNameConstants;
import com.yunya.models.system.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotBlank;
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
  FrontUserInfoVO validate(@RequestBody JwtRequestFrom jwtRequestFrom);

  /**
   * 根据ID查询品牌
   *
   * @param id 品牌ID
   * @return
   */
  @RequestMapping(value = "/api/brand/one/{id}", method = RequestMethod.GET)
  Brand findBrandById(@PathVariable(value = "id") Integer id);

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
  Department findDepartmentById(@PathVariable(value = "id") Integer id);

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
  List<OrganizationInfoDetail> findOrgInfoList(@RequestBody OrganizationModel model);

  /**
   * 根据组织id列表查询组织信息列表
   *
   * @param orgIds 查询条件
   * @return
   */
  @RequestMapping(value = "/api/organization/ids", method = RequestMethod.POST)
  List<OrganizationInfoDetail> findOrgInfoInIds(@RequestBody List<Integer> orgIds);

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
   * 根据岗位组ID查询岗位组信息
   *
   * @param id 岗位组ID
   * @return obj
   */
  @RequestMapping(value = "/api/post/group/{id}", method = RequestMethod.GET)
  PostGroup findPostGroupById(@PathVariable(value = "id") Integer id);

  /**
   * 根据条件查询岗位组列表
   *
   * @param model 查询条件
   * @return
   */
  @RequestMapping(value = "/api/postGroup/list", method = RequestMethod.POST)
  List<PostGroup> findPostGroupList(@RequestBody PostGroupModel model);

  /**
   * 根据用户ID查询用户信息
   *
   * @param userId 用户ID
   * @return
   */
  @RequestMapping(value = "/api/userInfo/{userId}", method = RequestMethod.GET)
  SysUserInfoDetail findSysUserEmployeeInfoByUserId(@PathVariable(value = "userId") Integer userId);

  /**
   * 根据用户ID查询员工信息
   *
   * @param userId 用户ID
   * @return
   */
  @RequestMapping(value = "/api/employee/{userId}", method = RequestMethod.GET)
  SysEmployee findSysEmployeeById(@PathVariable(value = "userId") Integer userId);

  /**
   * 根据 员工ID 查询员工信息
   *
   * @param empId 员工ID
   * @return
   */
  @RequestMapping(value = "/api/user/{empId}", method = RequestMethod.GET)
  SysEmployee findSysUserByEmpId(@PathVariable(value = "empId") Integer empId);

  /**
   * 根据条件查询用户信息（含员工信息）
   *
   * @param model 查询条件
   * @return list
   */
  @RequestMapping(value = "/api/userInfo/list", method = RequestMethod.POST)
  List<SysUserInfoDetail> findSysUserEmployeeInfoList(@RequestBody SysUserEmployeeModel model);

  /**
   * 根据条件查询用户组织信息
   *
   * @param model 查询条件
   * @return list
   */
  @RequestMapping(value = "/api/userWithOrg/list", method = RequestMethod.POST)
  PageInfo<SysUserInfoDetail> findSysUserEmployeeWithOrgList(
      @RequestBody SysUserEmployeeModel model);

  /**
   * 根据条件查询可登陆组织员工列表
   *
   * @param queryForm 查询条件
   * @return List<EmployeeInfoVO>
   */
  @RequestMapping(value = "/api/enable/login/list", method = RequestMethod.POST)
  List<EmployeeInfoVO> findEnableLoginEmployeeList(
      @RequestBody @Validated EmployeeInfoQueryForm queryForm);

  /**
   * 根据科室ID查询科室
   *
   * @param id 科室模板ID
   * @return DepartmentRoom
   */
  @RequestMapping(value = "/api/deptRoom/{id}", method = RequestMethod.GET)
  DepartmentRoom findDepartmentRoomById(@PathVariable(value = "id") Integer id);

  /**
   * 根据科室ID集合查询科室
   *
   * @param ids 科室模板ID集合
   * @return List<DepartmentRoom>
   */
  @RequestMapping(value = "/api/deptRoom/ids/list", method = RequestMethod.POST)
  List<DepartmentRoom> findDepartmentRoomByIds(@RequestBody List<Integer> ids);

  /**
   * 根据条件查询科室列表
   *
   * @param departmentRoom 查询条件
   * @return List<DepartmentRoom>
   */
  @RequestMapping(value = "/api/deptRoom/list", method = RequestMethod.POST)
  List<DepartmentRoom> findDepartmentRoomList(@RequestBody DepartmentRoom departmentRoom);

  /**
   * 根据ID查询门诊科室信息
   *
   * @param id 门诊科室ID
   * @return ClinicDepartmentRoomVO
   */
  @RequestMapping(value = "/api/deptRoom/clinic/{id}", method = RequestMethod.GET)
  ClinicDepartmentRoomVO findClinicDepartmentRoomById(@PathVariable(value = "id") Integer id);

  /**
   * 根据条件查询门诊科室列表
   *
   * @param queryForm 查询条件
   * @return List<ClinicDepartmentRoomVO>
   */
  @RequestMapping(value = "/api/deptRoom/clinic/list", method = RequestMethod.POST)
  List<ClinicDepartmentRoomVO> findClinicDepartmentRoomList(
      @RequestBody ClinicDepartmentRoomQueryForm queryForm);

  /**
   * 根据ID查询会员卡分类
   *
   * @param id 会员卡分类ID
   * @return
   */
  @RequestMapping(value = "/api/memberType/{id}", method = RequestMethod.GET)
  MemberType findMemberTypeById(@PathVariable(value = "id") Integer id);

  /**
   * 根据条件查询会员类型列表
   *
   * @param model 查询条件
   * @return
   */
  @RequestMapping(value = "/api/memberType/list", method = RequestMethod.POST)
  List<MemberType> findMemberTypeList(@RequestBody MemberType model);

  /**
   * 根据ID查询入账方式分类
   *
   * @param id 入账方式分类ID
   * @return
   */
  @RequestMapping(value = "/api/accountType/{id}", method = RequestMethod.GET)
  AccountType findAccountTypeById(@PathVariable(value = "id") Integer id);

  /**
   * 根据条件查询入账方式分类列表
   *
   * @param model 查询条件
   * @return
   */
  @RequestMapping(value = "/api/accountType/list", method = RequestMethod.POST)
  List<AccountType> findAccountTypeList(@RequestBody AccountType model);

  /**
   * 根据ID查询入账方式
   *
   * @param id 入账方式ID
   * @return
   */
  @RequestMapping(value = "/api/accountItem/{id}", method = RequestMethod.GET)
  AccountItem findAccountItemById(@PathVariable(value = "id") Integer id);

  /**
   * 根据实体查询入账方式
   *
   * @param entity 入账方式
   * @return AccountItem
   */
  @RequestMapping(value = "/api/accountItem/one", method = RequestMethod.POST)
  AccountItem findAccountItem(@RequestBody AccountItem entity);

  /**
   * 根据条件查询入账方式列表
   *
   * @param model 查询条件
   * @return List<AccountItem>
   */
  @RequestMapping(value = "/api/accountItem/list", method = RequestMethod.POST)
  List<AccountItem> findAccountItemList(@RequestBody AccountItem model);

  /**
   * 根据ID查询门诊入账方式
   *
   * @param id 门诊入账方式ID
   * @return
   */
  @RequestMapping(value = "/api/accountItem/clinic/{id}", method = RequestMethod.GET)
  ClinicAccountItemVO findClinicAccountItemById(@PathVariable(value = "id") Integer id);

  /**
   * 根据条件查询门诊入账方式列表
   *
   * @param queryForm 查询条件
   * @return
   */
  @RequestMapping(value = "/api/accountItem/clinic/list", method = RequestMethod.POST)
  List<ClinicAccountItemVO> findClinicAccountItemList(
      @RequestBody ClinicAccountItemQueryForm queryForm);

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

  /**
   * 获取设备信息
   *
   * @return EquipmentInfo
   */
  @RequestMapping(value = "/api/equipmentInfoOne", method = RequestMethod.GET)
  EquipmentInfo equipmentInfoOne();

  /**
   * 根据ID集合查询会员卡分类
   *
   * @param ids 会员卡分类ID集合
   * @return 返回会员卡集合
   */
  @RequestMapping(value = "/api/memberTypes", method = RequestMethod.POST)
  List<MemberType> findMemberTypeByIds(@RequestBody List<Integer> ids);

  /**
   * 根据用户ID集合查询用户信息
   *
   * @param userIds 用户ID集合
   * @return 用户信息集合
   */
  @RequestMapping(value = "/api/userInfo/ids/list", method = RequestMethod.POST)
  List<SysUserInfoDetail> findSysUserEmployeeInfoByUserIds(@RequestBody List<Integer> userIds);

  /**
   * 根据条件分页查询用户信息（含员工信息）
   *
   * @param model 查询条件
   * @return list
   */
  @RequestMapping(value = "/api/userInfo/page", method = RequestMethod.POST)
  PageInfo<SysUserInfoDetail> findSysUserEmployeeInfoPage(@RequestBody SysUserEmployeeModel model);

  /**
   * 根据组织ID获取医疗机构详细信息
   *
   * @param companyId 组织ID
   * @return MedicalOrganizationInfoVO
   */
  @RequestMapping(value = "/api/clinicExtInfo/{companyId}", method = RequestMethod.GET)
  MedicalOrganizationInfoVO clinicExtInfoByCompanyId(
      @PathVariable(value = "companyId") Integer companyId);

  /**
   * 根据ID查询字典明细
   *
   * @param ids 字典明细ID列表
   * @return
   */
  @RequestMapping(value = "/api/dictionary/ids", method = RequestMethod.POST)
  List<DictionaryItem> findDictionaryItemByIds(@RequestBody List<Integer> ids);

  @RequestMapping(value = "/api/dictItem/name", method = RequestMethod.GET)
  DictionaryItem getDictItemByNames(@NotBlank @RequestParam(value = "typeName", required = true) String typeName
          , @NotBlank @RequestParam(value = "itemName", required = true) String itemName);
}
