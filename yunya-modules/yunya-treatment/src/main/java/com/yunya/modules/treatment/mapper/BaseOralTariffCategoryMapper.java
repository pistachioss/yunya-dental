package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.query.BaseOralTariffCategoryQueryForm;
import com.yunya.feign.treatment.domain.vo.BaseOralTariffCategoryVO;
import com.yunya.models.tariff.BaseOralTariffCategory;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseOralTariffCategoryMapper extends Mapper<BaseOralTariffCategory> {

  /**
   * 根据ID查询商品分类信息
   *
   * @param id 商品分类ID
   * @return
   */
  BaseOralTariffCategoryVO selectBaseOralTariffCategoryById(@Param("id") Integer id);

  /**
   * 根据条件查询商品分类列表信息
   *
   * @param queryForm 查询条件
   * @return
   */
  List<BaseOralTariffCategoryVO> selectBaseOralTariffCategoryList(
      @Param("queryForm") BaseOralTariffCategoryQueryForm queryForm);

  /**
   * 查询基础价目表分类视图列表
   *
   * @return List<BaseOralTariffCategory>
   */
  List<BaseOralTariffCategory> selectBaseOralTariffCategoryView();

  /**
   * 批量插入商品分类列表
   *
   * @param list 商品分类列表
   */
  void insertBaseOralTariffCategoryList(@Param("list") List<BaseOralTariffCategory> list);

  /**
   * 批量更新商品分类列表
   *
   * @param list 商品分类列表
   */
  void updateBaseOralTariffCategoryList(@Param("list") List<BaseOralTariffCategory> list);
}
