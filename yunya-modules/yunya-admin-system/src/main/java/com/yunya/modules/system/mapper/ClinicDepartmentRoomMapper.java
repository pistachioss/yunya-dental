package com.yunya.modules.system.mapper;

import com.yunya.feign.system.vo.ClinicDepartmentRoomVO;
import com.yunya.feign.system.vo.DeptRoomVO;
import com.yunya.models.system.ClinicDepartmentRoom;
import com.yunya.modules.system.domain.query.ClinicDepartmentRoomQueryForm;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ClinicDepartmentRoomMapper extends Mapper<ClinicDepartmentRoom> {

  /**
   * 根据条件查询门诊科室列表
   *
   * @param queryForm 查询条件
   * @return list
   */
  List<ClinicDepartmentRoomVO> selectClinicDepartmentRoomList(
      @Param("queryForm") ClinicDepartmentRoomQueryForm queryForm);

  /**
   * 根据门诊科室ID查询门诊科室信息
   *
   * @param id 门诊科室ID
   * @return
   */
  ClinicDepartmentRoomVO selectByClinicDeptRoomId(@Param("id") Integer id);

  /**
   * 查询某个科室在门诊的配置列表
   *
   * @param orgId 诊所ID
   * @param deptRoomId 科室ID
   * @return
   */
  ClinicDepartmentRoomVO selectClinicDeptRoom(
      @Param("orgId") Integer orgId, @Param("deptRoomId") Integer deptRoomId);

  /**
   * 查询门诊可用科室
   *
   * @param orgId 组织ID
   * @param departmentRoomId 科室ID
   * @param inservice 是否启用
   * @return
   */
  DeptRoomVO selectDeptRoomVO(
      @Param("orgId") Integer orgId,
      @Param("departmentRoomId") Integer departmentRoomId,
      @Param("inservice") boolean inservice);

  /**
   * 批量添加门诊科室
   * @param list 门诊科室列表
   */
  void insertBatch(@Param("list") List<ClinicDepartmentRoom> list);
}
