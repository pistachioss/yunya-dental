package com.yunya.modules.system.mapper;

import com.yunya.models.system.DictionaryType;
import com.yunya.modules.system.form.query.DictQueryForm;
import com.yunya.modules.system.vo.DictionaryTypeVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface DictionaryTypeMapper extends Mapper<DictionaryType> {
  /**
   * 根据条件查询字典类型列表
   * @param queryForm 参数封装
   * @return
   */
  List<DictionaryTypeVO> selectBrandByExample(@Param("queryForm") DictQueryForm queryForm);
}
