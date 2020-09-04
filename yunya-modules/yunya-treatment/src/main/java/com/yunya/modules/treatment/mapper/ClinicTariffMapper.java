package com.yunya.modules.treatment.mapper;

import com.yunya.feign.tariff.domain.query.ClinicTariffQueryForm;
import com.yunya.feign.tariff.domain.vo.ClinicTariffExportVO;
import com.yunya.feign.tariff.domain.vo.ClinicTariffVO;
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
}
