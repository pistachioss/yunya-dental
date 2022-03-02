package com.yunya.modules.system.mapper;

import com.yunya.models.system.SysEmployee;
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

  /***
   * 查询最大的工号
   * @return
   */
  String selectMaxWorkNumber();

  /***
   * 查询员工工号是否存在
   * @return
   */
  Integer selectWorkNumberByUserId(@Param("userId") Integer userId, @Param("workNumber") String workNumber);
}
