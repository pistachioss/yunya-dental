package com.yunya.modules.tariff.mapper;

import com.yunya.feign.treatment.domain.query.BaseOralTariffQueryForm;
import com.yunya.feign.treatment.domain.vo.BaseOralTariffInfoVO;
import com.yunya.feign.treatment.domain.vo.BaseOralTariffVO;
import com.yunya.feign.treatment.domain.vo.BaseOralTariffExportVO;
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
}
