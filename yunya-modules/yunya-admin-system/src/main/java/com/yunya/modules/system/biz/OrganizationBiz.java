package com.yunya.modules.system.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.TreeUtil;
import com.yunya.models.system.*;
import com.yunya.modules.system.domain.form.OrganizationForm;
import com.yunya.modules.system.domain.query.OrganizationQueryForm;
import com.yunya.modules.system.mapper.ClinicAccountItemMapper;
import com.yunya.modules.system.mapper.ClinicDepartmentRoomMapper;
import com.yunya.modules.system.mapper.CompanyMapper;
import com.yunya.modules.system.mapper.SysUserPostMapper;
import com.yunya.modules.system.vo.OrganizationInfoVO;
import com.yunya.modules.system.vo.tree.OrganizationTreeVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.*;

/**
 * 简单介绍:</br> 组织业务层
 *
 * @author: chow
 * @date: 2020/6/1 09:46
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrganizationBiz {

  /** 组织信息 */
  @Autowired private CompanyMapper companyMapper;
  /** 组织扩展信息 */
  @Autowired private ClinicExtInfoBiz clinicExtInfoBiz;
  /** 员工可登陆组织、岗位配置 */
  @Autowired private SysUserPostMapper sysUserPostMapper;
  /** 门诊支付方式配置 */
  @Autowired private ClinicAccountItemMapper clinicAccountItemMapper;
  /** 门诊科室配置 */
  @Autowired private ClinicDepartmentRoomMapper clinicDepartmentRoomMapper;
  /** 医疗机构类型 */
  private final Byte MEDICAL_TYPE = BusinessConstants.MEDICAL_TYPE;

  /**
   * 根据ID获取组织信息
   *
   * @param id 组织ID
   * @return
   */
  public OrganizationInfo findOrgInfoById(Integer id) {
    return companyMapper.selectOrgInfoById(id);
  }

  /**
   * 根据条件初始化组织树
   *
   * @return list
   */
  public List<OrganizationTreeVO> initOrganizationTree() {
    List<OrganizationInfoVO> vos =
        companyMapper.selectOrganizationByExample(new OrganizationQueryForm());
    return initTree(vos);
  }

  /**
   * 构建组织树列表
   *
   * @param vos 组织列表
   * @return list
   */
  private List<OrganizationTreeVO> initTree(List<OrganizationInfoVO> vos) {
    List<OrganizationTreeVO> trees = new ArrayList<>();
    if (vos.size() > 0) {
      OrganizationTreeVO node;
      for (OrganizationInfoVO vo : vos) {
        node = new OrganizationTreeVO();
        BeanUtils.copyProperties(vo, node);
        trees.add(node);
      }
    }
    return TreeUtil.buildByRecursive(trees, BusinessConstants.DEFAULT_PARENT_ID);
  }

  /**
   * 根据条件查询组织信息列表（可分页）
   *
   * @param queryForm 参数封装
   * @return list
   */
  public PageInfo<OrganizationInfoVO> findList(OrganizationQueryForm queryForm) {
    if (queryForm.getWhetherPage()) {
      PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
    }
    List<OrganizationInfoVO> resultList = companyMapper.selectOrganizationByExample(queryForm);
    return new PageInfo<>(resultList);
  }

  /**
   * 新增组织信息
   *
   * @param resource 封装参数
   */
  public void addOrganization(OrganizationForm resource) {
    String name = resource.getName();
    Company entity = new Company();
    entity.setName(name);
    Company result = companyMapper.selectOne(entity);
    if (null != result) {
      throw new ClientServiceException("新增组织'" + name + "'失败，该组织名称已存在", NAME_IS_OCCUPIED);
    }
    Byte type = resource.getType();
    Company company = EntityUtils.build(resource, Company.class);
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    String userName = BaseContextHandler.getName();
    company.setCrtId(userId);
    company.setCrtName(userName);
    company.setUpdId(userId);
    company.setUpdName(userName);
    companyMapper.insertSelective(company);
    // 添加组织类型为医疗机构，添加医疗机构扩展信息
    addClinicExtInfo(resource, company.getId(), type);
  }

  /**
   * 编辑组织信息
   *
   * @param id 组织ID
   * @param resource 参数封装
   */
  public void editOrganization(Integer id, OrganizationForm resource) {
    Company company = companyMapper.selectByPrimaryKey(id);
    if (null == company) {
      throw new ClientServiceException(
          "修改组织信息失败，名称为'" + resource.getName() + "'的数据不存在", QUERY_RESULT_INVALID);
    }
    String companyName = resource.getName();
    // 名称有修改，校验名称是否重复
    if (!company.getName().equals(companyName)) {
      Company entity = new Company();
      entity.setName(companyName);
      int result = companyMapper.selectCount(entity);
      if (result > 0) {
        throw new ClientServiceException("修改组织'" + companyName + "'失败，该组织名称已存在", NAME_IS_OCCUPIED);
      }
      company.setName(companyName);
    }
    // 判断组织类型是否修改
    Byte organizationType = resource.getType();
    Byte companyType = company.getType();
    if (!companyType.equals(organizationType)) {
      // 组织类型由非医疗机构改成医疗机构，补充添加医疗机构附属信息
      addClinicExtInfo(resource, id, organizationType);
      // 组织类型由医疗机构改为非医疗机构，删除扩展信息
      deleteOrganizationExtInfo(id, companyName, companyType);
      company.setType(organizationType);
    }
    company.setOrderNum(resource.getOrderNum());
    Boolean inservice = resource.getInservice();
    if (null != inservice) {
      company.setInservice(inservice);
    }
    company.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    company.setUpdName(BaseContextHandler.getName());
    company.setUpdTime(new Date(System.currentTimeMillis()));
    companyMapper.updateByPrimaryKeySelective(company);
    // 更新医疗机构扩展信息,并校验医疗机构简称是否重复
    updateOrganizationExtInfo(id, resource, companyType);
  }

  /**
   * 更新组织为医疗机构的扩展信息
   *
   * @param id 组织ID
   * @param resource 参数封装
   * @param companyType 组织原有类型
   */
  private void updateOrganizationExtInfo(Integer id, OrganizationForm resource, Byte companyType) {
    if (companyType.equals(resource.getType()) && MEDICAL_TYPE.equals(companyType)) {
      ClinicExtInfo entity = new ClinicExtInfo();
      entity.setCompanyId(id);
      ClinicExtInfo clinicExtInfo = clinicExtInfoBiz.selectOne(entity);
      if (!clinicExtInfo.getAbbreviation().equals(resource.getAbbreviation())) {
        String abbreviation = resource.getAbbreviation();
        ClinicExtInfo info = new ClinicExtInfo();
        info.setAbbreviation(abbreviation);
        Long result = clinicExtInfoBiz.selectCount(info);
        if (result > 0) {
          throw new ClientServiceException(
              "修改组织简称'" + abbreviation + "'失败，该简称名称已存在", NAME_IS_OCCUPIED);
        }
        clinicExtInfo.setAbbreviation(abbreviation);
      }

      if (!clinicExtInfo.getClinicNumber().equals(resource.getClinicNumber())) {
        String clinicNumber = resource.getClinicNumber();
        ClinicExtInfo info = new ClinicExtInfo();
        info.setClinicNumber(clinicNumber);
        Long result = clinicExtInfoBiz.selectCount(info);
        if (result > 0) {
          throw new ClientServiceException(
              "修改组织编号'" + clinicNumber + "'失败，该组织编号已存在", NAME_IS_OCCUPIED);
        }
        clinicExtInfo.setAbbreviation(clinicNumber);
      }

      String brands = StringUtils.join(resource.getBrandIds(), ",");
      clinicExtInfo.setBrandIds(brands);
      clinicExtInfo.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
      clinicExtInfo.setUpdName(BaseContextHandler.getName());
      clinicExtInfo.setUpdTime(new Date(System.currentTimeMillis()));
      clinicExtInfoBiz.updateSelectiveById(clinicExtInfo);
    }
  }

  /**
   * 将组织类型修改为非医疗机构，删除原有医疗机构属性
   *
   * @param id 组织ID
   * @param companyName 组织名称
   * @param companyType 组织类型
   */
  private void deleteOrganizationExtInfo(Integer id, String companyName, Byte companyType) {
    if (MEDICAL_TYPE.equals(companyType)) {
      // 判断该医疗机构是否被使用
      ClinicExtInfo entity = new ClinicExtInfo();
      entity.setCompanyId(id);
      ClinicExtInfo clinicExtInfo = clinicExtInfoBiz.selectOne(entity);
      if (StringUtils.isNotBlank(clinicExtInfo.getTel())
          || StringUtils.isNotEmpty(clinicExtInfo.getChairQuantity().toString())) {
        throw new ClientServiceException(
            "修改组织'" + companyName + "'类型失败，该组织机构已关联其他数据", OBJECT_EDIT_FAIL);
      }
      clinicExtInfoBiz.delete(clinicExtInfo);
    }
  }

  /**
   * 校验添加组织为医疗机构的参数（门诊编号、门诊简称、门诊品牌）
   *
   * @param resource 参数封装
   */
  private void checkParams(OrganizationForm resource) {
    String name = resource.getName();
    String abbreviation = resource.getAbbreviation();
    if (StringUtils.isBlank(abbreviation)) {
      throw new ClientServiceException("添加医疗机构'" + name + "'门诊简称为空", PARAM_NOT_ALLOW_EMPTY);
    }
    ClinicExtInfo clinicExtInfo = new ClinicExtInfo();
    clinicExtInfo.setAbbreviation(abbreviation);
    Long result = clinicExtInfoBiz.selectCount(clinicExtInfo);
    if (result > 0) {
      throw new ClientServiceException("添加医疗机构'" + name + "'该门诊简称已存在", NAME_IS_OCCUPIED);
    }

    String clinicNumber = resource.getClinicNumber();
    if (StringUtils.isBlank(clinicNumber)) {
      throw new ClientServiceException("添加医疗机构'" + name + "'门诊编号为空", PARAM_NOT_ALLOW_EMPTY);
    }
    clinicExtInfo = new ClinicExtInfo();
    clinicExtInfo.setClinicNumber(clinicNumber);
    result = clinicExtInfoBiz.selectCount(clinicExtInfo);
    if (result > 0) {
      throw new ClientServiceException("添加医疗机构'" + name + "'该门诊编号已存在", NAME_IS_OCCUPIED);
    }

    Byte[] brandIds = resource.getBrandIds();
    if (StringHelper.isEmpty(brandIds)) {
      throw new ClientServiceException("添加医疗机构'" + name + "'门诊品牌为空", PARAM_NOT_ALLOW_EMPTY);
    }
  }

  /**
   * 添加医疗机构扩展信息并校验数据合法性
   *
   * @param resource 参数封装
   * @param organizationId 组织ID
   * @param organizationType 组织类型
   */
  private void addClinicExtInfo(
      OrganizationForm resource, Integer organizationId, Byte organizationType) {
    if (MEDICAL_TYPE.equals(organizationType)) {
      checkParams(resource);
      ClinicExtInfo clinicExtInfo = new ClinicExtInfo();
      String clinicNumber = resource.getClinicNumber();
      clinicExtInfo.setClinicNumber(clinicNumber);
      ClinicExtInfo extInfo = clinicExtInfoBiz.selectOne(clinicExtInfo);
      if (null != extInfo) {
        throw new ClientServiceException("添加医疗机构失败，门诊编号已经存在！", NAME_IS_OCCUPIED);
      }
      clinicExtInfo.setCompanyId(organizationId);
      clinicExtInfo.setClinicNumber(clinicNumber);
      String brands = StringUtils.join(resource.getBrandIds(), ",");
      clinicExtInfo.setBrandIds(brands);
      String abbreviation = resource.getAbbreviation();

      clinicExtInfo.setAbbreviation(abbreviation);
      clinicExtInfo.setCrtId(Integer.valueOf(BaseContextHandler.getUserID()));
      clinicExtInfo.setCrtName(BaseContextHandler.getName());
      clinicExtInfoBiz.insertSelective(clinicExtInfo);
    }
  }

  /**
   * 根据ID删除组织信息
   *
   * @param organizationId 组织ID
   */
  public void deleteOrganization(Integer organizationId) {
    Company company = new Company();
    company.setParentId(organizationId);
    int companyCount = companyMapper.selectCount(company);
    SysUserPost sysUserPost = new SysUserPost();
    sysUserPost.setCompanyId(organizationId);
    int userPostCount = sysUserPostMapper.selectCount(sysUserPost);
    ClinicAccountItem clinicAccountItem = new ClinicAccountItem();
    clinicAccountItem.setCompanyId(organizationId);
    int clinicAccountItemCount = clinicAccountItemMapper.selectCount(clinicAccountItem);
    ClinicDepartmentRoom clinicDeptRoom = new ClinicDepartmentRoom();
    clinicDeptRoom.setCompanyId(organizationId);
    int clinicDeptRoomCount = clinicDepartmentRoomMapper.selectCount(clinicDeptRoom);
    if (companyCount > 0
        || userPostCount > 0
        || clinicAccountItemCount > 0
        || clinicDeptRoomCount > 0) {
      throw new ClientServiceException(
          "删除ID为'" + organizationId + "'的组织失败，该组织已被使用", DELETE_NOT_ALLOW);
    }
    companyMapper.deleteByPrimaryKey(organizationId);
    ClinicExtInfo extInfo = new ClinicExtInfo();
    extInfo.setCompanyId(organizationId);
    clinicExtInfoBiz.delete(extInfo);
  }

  /**
   * 根据条件查询组织信息列表（feign）
   *
   * @param model 查询条件
   * @return list
   */
  public List<OrganizationInfoVO> findOrgInfoList(OrganizationModel model) {
    // 将obj转Map
    OrganizationQueryForm form = new OrganizationQueryForm();
    BeanUtils.copyProperties(model, form);
    return findList(form).getList();
  }

  /**
   * 更新组织信用代码
   *
   * @param companyId 组织ID
   * @param creditCode 信用代码
   */
  public void updateCompanyCredit(Integer companyId, String creditCode) {
    Company company = companyMapper.selectByPrimaryKey(companyId);
    if (null == company) {
      throw new ClientServiceException(
          String.format("修改医疗机构信息，门诊ID:%d的数据不存在", companyId), DATA_NOT_EXIST);
    }
    company.setCreditCode(creditCode);
    company.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
    company.setUpdName(BaseContextHandler.getName());
    company.setUpdTime(new Date());
    companyMapper.updateByPrimaryKeySelective(company);
  }
}
