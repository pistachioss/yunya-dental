package com.yunya.modules.system.rpc;

import com.github.pagehelper.PageInfo;
import com.yunya.feign.system.form.*;
import com.yunya.feign.system.vo.*;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.system.*;
import com.yunya.modules.system.biz.*;
import com.yunya.modules.system.domain.query.ClinicAccountItemQueryForm;
import com.yunya.modules.system.domain.query.ClinicDepartmentRoomQueryForm;
import com.yunya.modules.system.domain.query.SysUserInfoDetailQueryFrom;
import com.yunya.modules.system.rpc.service.PermissionService;
import com.yunya.modules.system.vo.OrganizationInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
@Api("系统管理服务接口暴露")
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
  /** 岗位组 */
  @Autowired private PostGroupBiz postGroupBiz;
  /** 用户（员工） */
  @Autowired private SysUserBiz sysUserBiz;
  /** 员工 */
  @Autowired private SysEmployeeBiz sysEmployeeBiz;
  /** 科室模版 */
  @Autowired private DepartmentRoomBiz departmentRoomBiz;
  /** 门诊科室 */
  @Autowired private ClinicDepartmentRoomBiz clinicDepartmentRoomBiz;
  /** 会员卡分类 */
  @Autowired private MemberTypeBiz memberTypeBiz;
  /** 入账方式分类 */
  @Autowired private AccountTypeBiz accountTypeBiz;
  /** 入账方式信息 */
  @Autowired private AccountItemBiz accountItemBiz;
  /** 门诊入账方式 */
  @Autowired private ClinicAccountItemBiz clinicAccountItemBiz;
  /** 人脸识别设备信息 */
  @Autowired private EquipmentBiz equipmentBiz;

  /**
   * 根据用户名、密码查询用户信息
   *
   * @param params 参数封装
   * @return UserInfoVO
   */
  @RequestMapping(value = "/user/validate", method = RequestMethod.POST)
  public FrontUserInfoVO validate(@RequestBody Map<String, String> params) {
    return permissionService.validate(params.get("username"), params.get("password"));
  }

  /**
   * 根据ID查询品牌
   *
   * @param id 品牌ID
   * @return
   */
  @RequestMapping(value = "/brand/one/{id}", method = RequestMethod.GET)
  public Brand findBrandById(@PathVariable(value = "id") Integer id) {
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
  public Department findDepartmentById(@PathVariable(value = "id") Integer id) {
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
  public DictionaryItem findDictionaryItemById(@PathVariable(value = "id") Integer id) {
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
  public DictionaryType findDictionaryTypeById(@PathVariable(value = "id") Integer id) {
    return dictionaryTypeBiz.selectById(id);
  }

  /**
   * 根据条件查询字典类型列表
   *
   * @param model 查询条件
   * @return
   */
  @RequestMapping(value = "/dict/list", method = RequestMethod.POST)
  public List<DictionaryType> findDictionaryTypeList(@RequestBody DictionaryTypeModel model) {
    DictionaryType dictType = new DictionaryType();
    BeanUtils.copyProperties(model, dictType);
    return dictionaryTypeBiz.selectByObj(dictType);
  }

  /**
   * 根据组织ID查询组织信息
   *
   * @param id 组织ID
   * @return obj
   */
  @RequestMapping(value = "/organization/{id}", method = RequestMethod.GET)
  public OrganizationInfo findOrgInfoByOrgId(@PathVariable(value = "id") Integer id) {
    return organizationBiz.findOrgInfoById(id);
  }

  /**
   * 根据组织ID列表获取组织信息列表
   *
   * @param orgIds 组织ID列表
   * @return
   */
  @RequestMapping (value = "/organization/ids", method = RequestMethod.POST)
  public List<OrganizationInfoVO> findOrgInfoInIds(@RequestBody List<Integer> orgIds) {
    return organizationBiz.findOrgInfoInIds(orgIds);
  }

  /**
   * 根据条件查询组织信息
   *
   * @param model 查询条件
   * @return list
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
  public CompanyDepartment findCompanyDepartmentById(@PathVariable(value = "id") Integer id) {
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
  public Post findPostById(@PathVariable(value = "id") Integer id) {
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

  /**
   * 根据岗位组ID查询岗位组信息
   *
   * @param id 岗位组ID
   * @return obj
   */
  @RequestMapping(value = "/post/group/{id}", method = RequestMethod.GET)
  public PostGroup findPostGroupById(@PathVariable(value = "id") Integer id) {
    return postGroupBiz.selectById(id);
  }

  /**
   * 根据条件查询岗位组列表
   *
   * @param model 查询条件
   * @return
   */
  @RequestMapping(value = "/postGroup/list", method = RequestMethod.POST)
  public List<PostGroup> findPostGroupList(@RequestBody PostGroupModel model) {
    PostGroup postGroup = new PostGroup();
    BeanUtils.copyProperties(model, postGroup);
    return postGroupBiz.selectByObj(postGroup);
  }

  /**
   * 根据用户ID查询用户信息
   *
   * @param userId 用户ID
   * @return
   */
  @RequestMapping(value = "/userInfo/by/{userId}", method = RequestMethod.GET)
  public SysUserInfoDetail findSysUserEmployeeInfoByUserId(
      @PathVariable(value = "userId") Integer userId) {
    return sysUserBiz.findUserInfoByUserId(userId);
  }

  /**
   * 根据用户ID集合查询用户信息
   *
   * @param userIds 用户ID集合
   * @return 用户信息集合
   */
  @RequestMapping(value = "/userInfo/ids/list", method = RequestMethod.POST)
  public List<SysUserInfoDetail> findSysUserEmployeeInfoByUserIds(@RequestBody List<Integer> userIds) {
    return sysUserBiz.findUserInfoByUserIds(userIds);
  }

  /**
   * 根据条件查询用户信息（含员工信息）
   *
   * @param model 查询条件
   * @return list
   */
  @RequestMapping(value = "/userInfo/list", method = RequestMethod.POST)
  public List<SysUserInfoDetail> findSysUserEmployeeInfoList(
      @RequestBody SysUserEmployeeModel model) {
    SysUserInfoDetailQueryFrom from = new SysUserInfoDetailQueryFrom();
    BeanUtils.copyProperties(model, from);
    return sysUserBiz.findUserDetailInfoList(from).getList();
  }

  /**
   * 根据条件分页查询用户信息（含员工信息）
   *
   * @param model 查询条件
   * @return list
   */
  @RequestMapping(value = "/userInfo/page", method = RequestMethod.POST)
  public PageInfo<SysUserInfoDetail> findSysUserEmployeeInfoPage(
          @RequestBody SysUserEmployeeModel model) {
    SysUserInfoDetailQueryFrom from = new SysUserInfoDetailQueryFrom();
    BeanUtils.copyProperties(model, from);
    return sysUserBiz.findUserDetailInfoList(from);
  }

  /**
   * 根据条件查询用户组织信息
   *
   * @param model 查询条件
   * @return
   */
  @RequestMapping(value = "/userWithOrg/list", method = RequestMethod.POST)
  public PageInfo<SysUserInfoDetail> findUserDetailWithOrgList(@RequestBody SysUserEmployeeModel model) {
    SysUserInfoDetailQueryFrom from = new SysUserInfoDetailQueryFrom();
    BeanUtils.copyProperties(model, from);
    return sysUserBiz.findUserDetailWithOrgList(from);
  }

  /**
   * 根据用户ID查询员工信息
   *
   * @param userId 用户ID
   * @return
   */
  @RequestMapping(value = "/employee/{userId}", method = RequestMethod.GET)
  public SysEmployee findSysEmployeeById(@PathVariable(value = "userId") Integer userId) {
    SysEmployee employee = new SysEmployee();
    employee.setUserId(userId);
    return sysEmployeeBiz.selectOne(employee);
  }

  /**
   * 根据科室ID查询科室
   *
   * @param id 科室模板ID
   * @return
   */
  @RequestMapping(value = "/deptRoom/{id}", method = RequestMethod.GET)
  public DepartmentRoom findDepartmentRoomById(@PathVariable(value = "id") Integer id) {
    return departmentRoomBiz.selectById(id);
  }

  /**
   * 根据条件查询科室列表
   *
   * @param departmentRoom 查询条件
   * @return
   */
  @RequestMapping(value = "/deptRoom/list", method = RequestMethod.POST)
  public List<DepartmentRoom> findDepartmentRoomList(@RequestBody DepartmentRoom departmentRoom) {
    return departmentRoomBiz.selectList(departmentRoom);
  }

  /**
   * 根据科室ID集合查询科室
   *
   * @param ids 科室模板ID集合
   * @return
   */
  @RequestMapping(value = "/deptRoom/ids/list", method = RequestMethod.POST)
  List<DepartmentRoom> findDepartmentRoomByIds(@RequestBody List<Integer> ids) {
    if (StringHelper.isNotEmpty(ids)) {
      return departmentRoomBiz.findDepartmentRoomByIds(ids);
    }
    return new ArrayList<>();
  }

  /**
   * 根据ID查询门诊科室信息
   *
   * @param id 门诊科室ID
   * @return
   */
  @RequestMapping(value = "/deptRoom/clinic/{id}", method = RequestMethod.GET)
  public ClinicDepartmentRoomVO findClinicDepartmentRoomById(
      @PathVariable(value = "id") Integer id) {
    return clinicDepartmentRoomBiz.findByClinicDeptRoomId(id);
  }

  /**
   * 根据条件查询门诊科室列表
   *
   * @param queryForm 查询条件
   * @return
   */
  @RequestMapping(value = "/deptRoom/clinic/list", method = RequestMethod.POST)
  public List<ClinicDepartmentRoomVO> findClinicDepartmentRoomList(
      @RequestBody ClinicDepartmentRoomQueryForm queryForm) {
    return clinicDepartmentRoomBiz.findList(queryForm).getList();
  }

  /**
   * 根据ID查询会员卡分类
   *
   * @param id 会员卡分类ID
   * @return
   */
  @RequestMapping(value = "/memberType/{id}", method = RequestMethod.GET)
  public MemberType findMemberTypeById(@PathVariable(value = "id") Integer id) {
    return memberTypeBiz.selectById(id);
  }

  /**
   * 根据ID集合查询会员卡分类
   *
   * @param ids 会员卡分类ID集合
   * @return 返回会员卡集合findMemberTypeByIds
   */
  @RequestMapping(value = "/memberTypes", method = RequestMethod.POST)
  public List<MemberType> findMemberTypeByIds(@RequestBody List<Integer> ids) {
    return memberTypeBiz.findMemberTypeByIds(ids);
  }

  /**
   * 根据条件查询会员类型列表
   *
   * @param model 查询条件
   * @return
   */
  @RequestMapping(value = "/memberType/list", method = RequestMethod.POST)
  public List<MemberType> findMemberTypeList(@RequestBody MemberType model) {
    return memberTypeBiz.selectList(model);
  }

  /**
   * 根据ID查询入账方式分类
   *
   * @param id 入账方式分类ID
   * @return
   */
  @RequestMapping(value = "/accountType/{id}", method = RequestMethod.GET)
  public AccountType findAccountTypeById(@PathVariable(value = "id") Integer id) {
    return accountTypeBiz.selectById(id);
  }

  /**
   * 根据条件查询入账方式分类列表
   *
   * @param model 查询条件
   * @return
   */
  @RequestMapping(value = "/accountType/list", method = RequestMethod.POST)
  public List<AccountType> findAccountTypeList(@RequestBody AccountType model) {
    return accountTypeBiz.selectList(model);
  }

  /**
   * 根据ID查询入账方式
   *
   * @param id 入账方式ID
   * @return
   */
  @RequestMapping(value = "/accountItem/{id}", method = RequestMethod.GET)
  public AccountItem findAccountItemById(@PathVariable(value = "id") Integer id) {
    return accountItemBiz.selectById(id);
  }

  /**
   * 根据条件查询入账方式列表
   *
   * @param model 查询条件
   * @return
   */
  @RequestMapping(value = "/accountItem/list", method = RequestMethod.POST)
  public List<AccountItem> findAccountItemList(@RequestBody AccountItem model) {
    return accountItemBiz.selectList(model);
  }

  /**
   * 根据ID查询门诊入账方式
   *
   * @param id 门诊入账方式ID
   * @return
   */
  @RequestMapping(value = "/accountItem/clinic/{id}", method = RequestMethod.GET)
  public ClinicAccountItemVO findClinicAccountItemById(@PathVariable(value = "id") Integer id) {
    return clinicAccountItemBiz.findById(id);
  }

  /**
   * 根据条件查询门诊入账方式列表
   *
   * @param queryForm 查询条件
   * @return
   */
  @RequestMapping(value = "/accountItem/clinic/list", method = RequestMethod.POST)
  public List<ClinicAccountItemVO> findClinicAccountItemList(
      @RequestBody ClinicAccountItemQueryForm queryForm) {
    return clinicAccountItemBiz.findList(queryForm).getList();
  }

  /**
   * 获取设备信息
   *
   * @return EquipmentInfo
   */
  @ApiModelProperty(value = "获取设备信息")
  @RequestMapping(value = "/equipmentInfoOne", method = RequestMethod.GET)
  public EquipmentInfo equipmentInfoOne() {
    return equipmentBiz.findEquipmentInfoVO();
  }
}
