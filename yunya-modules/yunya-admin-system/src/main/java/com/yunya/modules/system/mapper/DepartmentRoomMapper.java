package com.yunya.modules.system.mapper;

import com.yunya.models.system.DepartmentRoom;
import com.yunya.modules.system.domain.query.DepartmentRoomQueryForm;
import com.yunya.modules.system.vo.DepartmentRoomVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface DepartmentRoomMapper extends Mapper<DepartmentRoom> {

  /**
   * 根据条件查询科室列表
   *
   * @param queryForm 查询条件
   * @return
   */
  List<DepartmentRoomVO> selectList(@Param("queryForm") DepartmentRoomQueryForm queryForm);

  /**
   * 根据ID查询科室信息
   *
   * @param id 科室ID
   * @return
   */
  DepartmentRoomVO selectById(@Param("id") Integer id);
}
