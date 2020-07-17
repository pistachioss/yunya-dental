package com.yunya.modules.system.mapper;

import com.yunya.models.system.SysDistricts;
import com.yunya.modules.system.form.query.SysDistrictsQueryForm;
import com.yunya.modules.system.vo.SysDistrictsVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SysDistrictsMapper extends Mapper<SysDistricts> {
  /**
   * 根据条件查询地区列表
   *
   * @param queryForm 参数封装
   * @return
   */
  List<SysDistrictsVO> selectDistrictsList(@Param("queryForm") SysDistrictsQueryForm queryForm);

  /**
   * 根据ID删除地区
   *
   * @param ids ID数组
   */
  void deleteDistrictsByIds(String[] ids);
}
