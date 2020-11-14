package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.query.ClinicTariffQueryForm;
import com.yunya.feign.treatment.domain.vo.BaseCategoryInfoVO;
import com.yunya.feign.treatment.domain.vo.ClinicTariffExportVO;
import com.yunya.feign.treatment.domain.vo.ClinicTariffVO;
import com.yunya.models.tariff.ClinicTariff;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ClinicTariffMapper extends Mapper<ClinicTariff> {

  /**
   * 根据门诊价目表ID获取门诊价目表信息
   *
   * @param clinicTariffId 门诊价目表ID
   * @return
   */
  ClinicTariffVO selectClinicTariffById(@Param("clinicTariffId") Integer clinicTariffId);

  /**
   * 根据条件查询门诊价目表信息列表
   *
   * @param queryForm 查询条件
   * @return
   */
  List<ClinicTariffVO> selectClinicTariffList(@Param("queryForm") ClinicTariffQueryForm queryForm);

  /**
   * 根据条件查询门诊价目表导出列表
   *
   * @param queryForm 查询条件
   * @return
   */
  List<ClinicTariffExportVO> selectClinicTariffExportList(
      @Param("queryForm") ClinicTariffQueryForm queryForm);

  /**
   * 根据条件查询价目表列表
   *
   * @param search
   * @return
   */
  List<BaseCategoryInfoVO> selectTariffList(@Param("search") String search);

  /**
   * 根据条件查询基础商品表列表
   *
   * @param search
   * @return
   */
  List<BaseCategoryInfoVO> selectBaseOralTariffList(@Param("search") String search);
}
