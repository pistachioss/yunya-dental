package com.yunya.modules.tariff.mapper;

import com.yunya.feign.tariff.domain.query.ClinicOralTariffQueryForm;
import com.yunya.feign.tariff.domain.vo.BaseOralTariffVO;
import com.yunya.feign.tariff.domain.vo.ClinicOralTariffVO;
import com.yunya.models.tariff.ClinicOralTariff;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.HashMap;
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
}
