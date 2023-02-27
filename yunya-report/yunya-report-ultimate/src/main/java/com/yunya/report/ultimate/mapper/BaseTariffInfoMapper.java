package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.vo.ItemCategoryInfoVO;
import com.yunya.feign.report.domain.vo.ItemCategoryVO;
import com.yunya.feign.report.domain.vo.ItemInfoVO;
import com.yunya.models.report.BaseTariffInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseTariffInfoMapper extends Mapper<BaseTariffInfo> {
  /**
   * 根据联合主键查询
   *
   * @param dataId 数据ID
   * @param dateType 数据类型
   * @return
   */
  BaseTariffInfo selectByUnionPrimaryKey(
      @Param("dataId") Integer dataId, @Param("dateType") Integer dateType);

  /**
   * 根据联合主键删除
   *
   * @param itemId 数据ID
   * @param itemType 数据类型
   * @return
   */
  void deleteByUnionPrimaryKey(
      @Param("itemId") Integer itemId, @Param("itemType") Integer itemType);

  /**
   * 获取全部开单项目分类列表
   *
   * @param itemType
   * @return List<ItemCategoryInfoVO>
   */
  List<ItemCategoryInfoVO> selectItemCategoryList(@Param("itemType") Integer itemType);

  /**
   * 根据项目分类ID查询全部项目列表
   *
   * @return List<ItemInfoVO>
   */
  List<ItemInfoVO> selectItemListByCategoryId(@Param("itemType") Integer itemType, @Param("categoryId") Integer categoryId);

  List<ItemCategoryVO> selectItemCategoryListByCategoryId(@Param("itemType")Byte itemType, @Param("categoryId") Integer categoryId);

  /**
   * 根据名字前缀模糊匹配查询商品列表
   *
   * @param name
   * @return
   */
  List<BaseTariffInfo> selectOralItemListPreLikeName(@Param("name") String name);
}
