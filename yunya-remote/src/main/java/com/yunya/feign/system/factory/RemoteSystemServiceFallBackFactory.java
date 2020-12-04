package com.yunya.feign.system.factory;

import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.*;
import com.yunya.feign.system.vo.*;
import com.yunya.models.system.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 简介: 系统服务调用降级处理
 *
 * @author: chow
 * @date: 2020/7/9 12:25
 * @description:
 * @since: 1.0.0
 */
@Slf4j
@Component
public class RemoteSystemServiceFallBackFactory implements RemoteSystemServiceFeign {

  /**
   * 登陆
   *
   * @param jwtRequestFrom 参数封装
   * @return
   */
  @Override
  public FrontUserInfoVO validate(JwtRequestFrom jwtRequestFrom) {
    return null;
  }

  @Override
  public Brand findBrandById(Integer id) {
    return null;
  }

  @Override
  public List<Brand> findBrandList(BrandModel model) {
    return null;
  }

  @Override
  public Department findDepartmentById(Integer id) {
    return null;
  }

  @Override
  public List<Department> findDepartmentList(DepartmentModel model) {
    return null;
  }

  @Override
  public DictionaryItem findDictionaryItemById(Integer id) {
    return null;
  }

  @Override
  public List<DictionaryItem> findDictionaryItemList(DictionaryItemModel model) {
    return null;
  }

  @Override
  public DictionaryType findDictionaryTypeById(Integer id) {
    return null;
  }

  @Override
  public List<DictionaryType> findDictionaryTypeList(DictionaryTypeModel model) {
    return null;
  }

  @Override
  public OrganizationInfo findOrgInfoByOrgId(Integer id) {
    return null;
  }

  @Override
  public List<OrganizationInfoDetail> findOrgInfoList(OrganizationModel model) {
    return null;
  }

  @Override
  public List<OrganizationInfoDetail> findOrgInfoInIds(List<Integer> orgIds) {
    return null;
  }

  @Override
  public CompanyDepartment findCompanyDepartmentById(Integer id) {
    return null;
  }

  @Override
  public List<CompanyDepartment> findCompanyDepartmentList(OrgDepartmentModel model) {
    return null;
  }

  @Override
  public Post findPostById(Integer id) {
    return null;
  }

  @Override
  public List<Post> findPostList(PostModel model) {
    return null;
  }

  @Override
  public PostGroup findPostGroupById(Integer id) {
    return null;
  }

  @Override
  public List<PostGroup> findPostGroupList(PostGroupModel model) {
    return null;
  }

  @Override
  public SysUserInfoDetail findSysUserEmployeeInfoByUserId(Integer userId) {
    return null;
  }

  @Override
  public SysEmployee findSysEmployeeById(Integer userId) {
    return null;
  }

  @Override
  public List<SysUserInfoDetail> findSysUserEmployeeInfoList(SysUserEmployeeModel model) {
    return null;
  }

  @Override
  public List<SysUserInfoDetail> findSysUserEmployeeWithOrgList(SysUserEmployeeModel model) {
    return null;
  }

  @Override
  public DepartmentRoom findDepartmentRoomById(Integer id) {
    return null;
  }

  @Override
  public List<DepartmentRoom> findDepartmentRoomList(DepartmentRoom departmentRoom) {
    return null;
  }

  @Override
  public ClinicDepartmentRoomVO findClinicDepartmentRoomById(Integer id) {
    return null;
  }

  @Override
  public List<ClinicDepartmentRoomVO> findClinicDepartmentRoomList(ClinicDepartmentRoomQueryForm queryForm) {
    return null;
  }

  @Override
  public MemberType findMemberTypeById(Integer id) {
    return null;
  }

  @Override
  public List<MemberType> findMemberTypeList(MemberType model) {
    return null;
  }

  @Override
  public AccountType findAccountTypeById(Integer id) {
    return null;
  }

  @Override
  public List<AccountType> findAccountTypeList(AccountType model) {
    return null;
  }

  @Override
  public AccountItem findAccountItemById(Integer id) {
    return null;
  }

  @Override
  public List<AccountItem> findAccountItemList(AccountItem model) {
    return null;
  }

  @Override
  public ClinicAccountItemVO findClinicAccountItemById(Integer id) {
    return null;
  }

  @Override
  public List<ClinicAccountItemVO> findClinicAccountItemList(ClinicAccountItemQueryForm queryForm) {
    return null;
  }

  /**
   * 根据用户ID查询权限
   *
   * @param userId 用户ID
   * @return
   */
  @Override
  public Set<String> selectPermsByUserId(Integer userId) {
    return null;
  }

  /**
   * 根据用户名查询权限
   *
   * @param uniqueName 用户名
   * @return
   */
  @Override
  public List<PermissionInfo> getPermissionByUsername(String uniqueName) {
    log.error("调用{}异常{}", "getPermissionByUsername", uniqueName);
    return null;
  }

  /**
   * 查询全部权限
   *
   * @return
   */
  @Override
  public List<PermissionInfo> getAllPermissionInfo() {
    log.error("调用{}异常", "getPermissionByUsername");
    return null;
  }

  /**
   * 保存日志
   *
   * @param log
   */
  @Override
  public void saveLog(LogInfo log) {}

  @Override
  public EquipmentInfo equipmentInfoOne() {
    return null;
  }

  @Override
  public List<MemberType> findMemberTypeByIds(List<Integer> ids) {
    return null;
  }
}
