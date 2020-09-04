package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.query.ClinicOralTariffQueryForm;
import com.yunya.feign.treatment.domain.vo.ClinicOralTariffExportVO;
import com.yunya.feign.treatment.domain.vo.ClinicOralTariffVO;
import com.yunya.models.tariff.ClinicOralTariff;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ClinicOralTariffMapper extends Mapper<ClinicOralTariff> {

  /**
   * 根据门诊商品项目ID获取门诊商品项目信息
   *
   * @param clinicOralTariffId 门诊商品项目ID
   * @return
   */
  ClinicOralTariffVO selectClinicOralTariffById(
      @Param("clinicOralTariffId") Integer clinicOralTariffId);

  /**
   * 根据条件查询门诊商品项目信息列表
   *
   * @param queryForm 查询条件
   * @return
   */
  List<ClinicOralTariffVO> selectClinicOralTariffList(
      @Param("queryForm") ClinicOralTariffQueryForm queryForm);

  /**
   * 根据条件查询门诊商品项目列表
   *
   * @param queryForm 查询条件
   * @return
   */
  List<ClinicOralTariffExportVO> selectClinicOralTariffExportList(
      @Param("queryForm") ClinicOralTariffQueryForm queryForm);
}
