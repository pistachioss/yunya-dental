package com.yunya.modules.system.mapper;

import com.yunya.modules.system.entity.Department;
import com.yunya.modules.system.form.query.DepartmentQueryForm;
import com.yunya.modules.system.vo.DepartmentVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface DepartmentMapper extends Mapper<Department> {

  /**
   * 根据条件查询部门模版列表
   *
   * @param form 查询条件封装
   * @return list
   */
  List<DepartmentVO> selectDepartmentByExample(@Param("form") DepartmentQueryForm form);
}
