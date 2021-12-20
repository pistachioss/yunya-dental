package com.yunya.modules.system.mapper;

import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.models.system.Company;
import com.yunya.modules.system.domain.query.OrganizationQueryForm;
import com.yunya.modules.system.vo.OrganizationInfoVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CompanyMapper extends Mapper<Company> {

  /**
   * 根据ID获取组织信息
   *
   * @param id 组织ID
   * @return
   */
  OrganizationInfo selectOrgInfoById(@Param("id") Integer id);

  /**
   * 新增组织（返回新增ID）
   *
   * @param company 组织信息信息
   * @return int
   */
  int insertCompany(Company company);

  /**
   * 根据条件查询组织详情列表
   *
   * @param queryForm 参数封装
   * @return list
   */
  List<OrganizationInfoVO> selectOrganizationByExample(@Param("queryForm") OrganizationQueryForm queryForm);

  List<OrganizationInfoVO> selectOrganizationInIds(@Param("orgIds") List<Integer> orgIds);

  /**
   * 根据上级组织ID获取组织信息
   *
   * @param parentId 上级组织ID
   * @return
   */
  List<OrganizationInfo> selectOrgInfoByParentId(@Param("parentId") Integer parentId);
}
