package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.query.BaseTariffAssociationQueryForm;
import com.yunya.feign.treatment.domain.query.BaseTariffQueryForm;
import com.yunya.feign.treatment.domain.vo.*;
import com.yunya.models.tariff.BaseTariff;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Set;

public interface BaseTariffMapper extends Mapper<BaseTariff> {

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
   * 根据开单项目ID集合查询开单项目列表
   * @param billingItemIds  开单项目ID
   * @return 返回实体信息列表
   */
  List<BaseTariff> selectBaseTariffListByBillingItemIds(@Param("billingItemIds") Set<Integer> billingItemIds);
}
