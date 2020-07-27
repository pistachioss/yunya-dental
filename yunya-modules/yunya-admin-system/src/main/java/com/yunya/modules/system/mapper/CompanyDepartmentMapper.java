package com.yunya.modules.system.mapper;

import com.yunya.models.system.CompanyDepartment;
import com.yunya.modules.system.domain.query.OrgDeptQueryForm;
import com.yunya.modules.system.vo.OrgDeptVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CompanyDepartmentMapper extends Mapper<CompanyDepartment> {
  /**
   * 根据条件查询组织部门列表
   *
   * @param queryForm 参数封装
   * @return list
   */
  List<OrgDeptVO> selectOrganizationDepartmentByExample(
      @Param("queryForm") OrgDeptQueryForm queryForm);
}
