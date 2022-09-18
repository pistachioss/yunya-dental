package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.query.BaseTariffAssociationQueryForm;
import com.yunya.feign.treatment.domain.query.BaseTariffQueryForm;
import com.yunya.feign.treatment.domain.vo.*;
import com.yunya.models.tariff.BaseTariff;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseTariffMapper extends Mapper<BaseTariff> {

  /**
   * 根据多个价目表ID查询价目表名称
   *
   * @param ids 字符串ID
   * @return String
   */
  String selectBaseTariffNamesByIds(@Param("ids") String ids);

  /**
   * 根据基础价目表ID查询价目表信息
   *
   * @param id 基础价目表ID
   * @return
   */
  BaseTariffInfoVO selectBaseTariffInfoById(@Param("id") Integer id);

  /**
   * 根据条件查询商品项目列表
   *
   * @param queryForm 查询条件
   * @return
   */
  List<BaseTariffVO> selectBaseTariffList(@Param("queryForm") BaseTariffQueryForm queryForm);

  /**
   * 根据条件查询导出价目表列表
   *
   * @param queryForm 查询条件
   * @return
   */
  List<BaseTariffExportVO> selectExportBaseTariffList(
      @Param("queryForm") BaseTariffQueryForm queryForm);

  /**
   * 根据条件查询开单关联信息列表
   *
   * @param queryForm 查询条件
   * @return
   */
  List<BaseTariffAssociationVO> selectBaseTariffAssociationList(
      @Param("queryForm") BaseTariffAssociationQueryForm queryForm);

  /**
   * 根据条件查询价目表开单关联信息列表
   *
   * @param queryForm 查询条件
   * @return
   */
  List<BaseTariffAssociationExportVO> selectExportBaseTariffAssociationList(
      @Param("queryForm") BaseTariffAssociationQueryForm queryForm);

  /**
   * 批量插入价目表
   *
   * @param list 价目表列表
   * @return 返回影响行数
   */
  int insertBaseItems(@Param("list") List<BaseTariff> list);

  /**
   * 批量更新价目表
   *
   * @param list 价目表列表
   * @return 返回影响行数
   */
  int updateBaseItems(@Param("list") List<BaseTariff> list);

  /**
   * 查询基础价目表视图列表
   *
   * @return List<BaseTariff>
   */
  List<BaseTariff> selectBaseTariffView();

  /**
   * 根据价目表分类ID查询该分类下最大价目表编号
   *
   * @param tariffCategoryId 价目表分类ID
   * @param categoryNumber 分类编号
   * @return String
   */
  String selectMaxBaseTariffNumber(
      @Param("tariffCategoryId") Integer tariffCategoryId,
      @Param("categoryNumber") String categoryNumber);

  /**
   * 查询全部项目表（包含价目表和商品表）
   *
   * @param queryForm
   * @return
   */
  List<BaseTariffVO> selectAllTariffList(@Param("queryForm") BaseTariffQueryForm queryForm);

  /**
   * 批量查询项目
   * @param ids
   * @return
   */
    List<BaseTariff> selectByIds(@Param("ids") List<Integer> ids);
}
