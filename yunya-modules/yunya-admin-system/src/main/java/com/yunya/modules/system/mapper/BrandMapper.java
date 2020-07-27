package com.yunya.modules.system.mapper;

import com.yunya.models.system.Brand;
import com.yunya.modules.system.domain.query.BrandQueryForm;
import com.yunya.modules.system.vo.BrandVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {

  /**
   * 根据条件查询品牌列表
   *
   * @param form 查询条件封装
   * @return list
   */
  List<BrandVO> selectBrandByExample(@Param("form") BrandQueryForm form);
}
