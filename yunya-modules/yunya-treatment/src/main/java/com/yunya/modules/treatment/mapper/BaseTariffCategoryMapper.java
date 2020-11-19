package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.query.BaseTariffCategoryQueryForm;
import com.yunya.feign.treatment.domain.vo.BaseTariffCategoryVO;
import com.yunya.models.tariff.BaseTariffCategory;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseTariffCategoryMapper extends Mapper<BaseTariffCategory> {

  /**
   * 根据ID查询价目表分类
   *
   * @param id 价目表分类ID
   * @return
   */
  BaseTariffCategoryVO selectBaseTariffCategoryById(@Param("id") Integer id);

  /**
   * 根据条件查询价目表分类列表
   *
   * @param queryForm 查询条件
   * @return
   */
  List<BaseTariffCategoryVO> selectBaseTariffCategoryList(
      @Param("queryForm") BaseTariffCategoryQueryForm queryForm);

  /**
   * 批量添加价目表分类列表
   *
   * @param list 基础价目表分类列表
   * @return
   */
  Integer insertBaseTariffCategoryList(@Param("list") List<BaseTariffCategory> list);

  /**
   * 批量更新价目表分类列表
   *
   * @param list 基础价目表分类列表
   * @return
   */
  Integer updateBaseTariffCategoryList(@Param("list") List<BaseTariffCategory> list);

  /**
   * 查询基础价目表分类视图列表
   *
   * @return List<BaseTariffCategory>
   */
  List<BaseTariffCategory> selectBaseTariffCategoryView();
}
