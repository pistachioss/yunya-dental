package com.yunya.modules.system.mapper;

import com.yunya.modules.system.entity.SysEmployee;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface SysEmployeeMapper extends Mapper<SysEmployee> {
  /**
   * 根据用户ID查询员工信息
   *
   * @param userId 用户ID
   * @return SysEmployee
   */
  SysEmployee selectByUserId(@Param("userId") Integer userId);
}
