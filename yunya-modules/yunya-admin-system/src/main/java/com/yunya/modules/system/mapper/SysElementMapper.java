package com.yunya.modules.system.mapper;

import com.yunya.modules.system.entity.SysElement;
import com.yunya.modules.system.form.query.SysElementQueryForm;
import com.yunya.modules.system.vo.SysElementVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SysElementMapper extends Mapper<SysElement> {
  /**
   * 根据条件查询系统功能按钮列表
   *
   * @param queryForm 参数封装
   * @return list
   */
  List<SysElementVO> selectListByExample(@Param("queryForm") SysElementQueryForm queryForm);
}
