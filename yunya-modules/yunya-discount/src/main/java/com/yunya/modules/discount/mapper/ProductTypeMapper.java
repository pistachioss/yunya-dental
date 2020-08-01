package com.yunya.modules.discount.mapper;

import com.yunya.feign.discount.domain.query.ProductTypeQueryForm;
import com.yunya.feign.discount.domain.vo.ProductTypeVO;
import com.yunya.models.discount.ProductType;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ProductTypeMapper extends Mapper<ProductType> {
  /**
   * 根据ID产销产品分类
   *
   * @param id 产品分类ID
   * @return
   */
  ProductTypeVO selectProductTypeById(@Param("id") Integer id);

  /**
   * 根据条件查询产品分类列表
   *
   * @param queryForm 查询条件
   * @return
   */
  List<ProductTypeVO> selectProductTypeList(@Param("queryForm") ProductTypeQueryForm queryForm);
}
