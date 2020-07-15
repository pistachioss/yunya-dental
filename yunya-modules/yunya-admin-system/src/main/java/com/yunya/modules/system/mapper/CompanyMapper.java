package com.yunya.modules.system.mapper;

import com.yunya.feign.system.vo.OrganizationInfo;
import com.yunya.modules.system.entity.Company;
import com.yunya.modules.system.form.query.OrganizationQueryForm;
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
  List<OrganizationInfoVO> selectOrganizationByExample(
      @Param("queryForm") OrganizationQueryForm queryForm);
}
