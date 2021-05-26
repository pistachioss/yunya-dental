package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.query.BaseOralTariffQueryForm;
import com.yunya.feign.treatment.domain.vo.BaseOralTariffExportVO;
import com.yunya.feign.treatment.domain.vo.BaseOralTariffInfoVO;
import com.yunya.feign.treatment.domain.vo.BaseOralTariffVO;
import com.yunya.models.tariff.BaseOralTariff;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseOralTariffMapper extends Mapper<BaseOralTariff> {

  /**
   * 根据商品项目ID查询商品项目信息
   *
   * @param id 商品项目ID
   * @return
   */
  BaseOralTariffInfoVO selectBaseOralTariffInfoById(@Param("id") Integer id);

  /**
   * 根据条件查询商品项目列表
   *
   * @param queryForm 查询条件
   * @return
   */
  List<BaseOralTariffVO> selectBaseOralTariffList(
      @Param("queryForm") BaseOralTariffQueryForm queryForm);

  /**
   * 根据条件查询导出商品项目列表
   *
   * @param queryForm 查询条件
   * @return
   */
  List<BaseOralTariffExportVO> selectExportBaseOralTariffList(
      @Param("queryForm") BaseOralTariffQueryForm queryForm);

  /**
   * 查询基础商品表视图列表
   *
   * @return List<BaseOralTariff>
   */
  List<BaseOralTariff> selectBaseOralTariffView();

  /**
   * 批量插入商品项目列表
   *
   * @param list 商品项目列表
   */
  void insertBaseItems(@Param("list") List<BaseOralTariff> list);

  /**
   * 批量更新商品项目列表
   *
   * @param list 商品项目列表
   */
  void updateBaseItems(@Param("list") List<BaseOralTariff> list);

  /**
   * 根据多个商品表ID查询商品表名称
   *
   * @param ids 字符串ID
   * @return String
   */
  String selectBaseOralNamesByIds(@Param("ids") String ids);
}
