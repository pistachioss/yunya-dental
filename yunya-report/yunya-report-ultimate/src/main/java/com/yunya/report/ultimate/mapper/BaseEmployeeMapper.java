package com.yunya.report.ultimate.mapper;

import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.models.report.BaseEmployee;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseEmployeeMapper extends Mapper<BaseEmployee> {

  /**
   * 根据门诊id查询医生信息
   *
   * @param orgId 门诊id
   * @return List<BaseEmployee>
   */
  List<BaseEmployee> selectByOrgId(@Param("orgId") Integer orgId);

  /**
   * 根据ID列表查询用户信息
   *
   * @param userIds 用户ID列表
   * @return List<SysUserInfoDetail>
   */
  List<SysUserInfoDetail> selectUserInfoByIds(@Param("userIds") List<Integer> userIds);
}
