package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.MultiClinicEmployeeQuery;
import com.yunya.feign.report.domain.query.base.FuchaForm;
import com.yunya.feign.report.domain.vo.ClinicEmployeBonusCoefficientVO;
import com.yunya.feign.report.domain.vo.FuchaVO;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.models.report.BaseEmployee;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Collection;
import java.util.List;

public interface BaseEmployeeMapper extends Mapper<BaseEmployee> {

  List<FuchaVO> fuchaList(FuchaForm fuchaForm);
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
  List<SysUserInfoDetail> selectUserInfoByIds(@Param("userIds") Collection<Integer> userIds);

  /**
   * 查询员工和门诊的笛卡尔积
   *
   * @param query
   * @return
   */
  List<ClinicEmployeBonusCoefficientVO> selectClinicEmployeeCartesianProduct(@Param("query") MultiClinicEmployeeQuery query, @Param("groupByOrgId") boolean groupByOrgId);
}
