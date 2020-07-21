package com.yunya.modules.system.mapper;

import com.yunya.models.system.ClinicDepartmentRoom;
import com.yunya.modules.system.form.query.ClinicDepartmentRoomQueryForm;
import com.yunya.modules.system.vo.ClinicDepartmentRoomVO;
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
}
