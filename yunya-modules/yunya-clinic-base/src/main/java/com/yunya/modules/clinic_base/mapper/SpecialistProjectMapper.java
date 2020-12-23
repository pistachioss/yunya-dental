package com.yunya.modules.clinic_base.mapper;

import com.yunya.feign.clinic_base.domain.query.SpecialistProjectQuery;
import com.yunya.feign.clinic_base.domain.vo.SpecialistProjectNameVO;
import com.yunya.feign.clinic_base.domain.vo.SpecialistProjectVO;
import com.yunya.models.clinic_base.SpecialistProject;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpecialistProjectMapper extends Mapper<SpecialistProject> {

  /**
   * 根据条件查询专科项目列表
   *
   * @param query 查询条件
   * @return List<SpecialistProjectVO>
   */
  List<SpecialistProjectVO> selectSpecialistProjectList(
      @Param("query") SpecialistProjectQuery query);

  /**
   * 专科项目名称列表
   *
   * @return List<SpecialistProjectNameVO>
   */
  List<SpecialistProjectNameVO> selectSpecialistProjectNameList();
}
