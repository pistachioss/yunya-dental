package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.query.ClinicOralTariffQueryForm;
import com.yunya.feign.treatment.domain.vo.BaseOralTariffVO;
import com.yunya.feign.treatment.domain.vo.ClinicOralTariffVO;
import com.yunya.models.tariff.ClinicOralTariff;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ClinicOralTariffMapper extends Mapper<ClinicOralTariff> {

  /**
   * 根据门诊商品项目ID获取门诊商品项目信息
   *
   * @param orgId 组织ID
   * @param oralTariffId 门诊商品项目ID
   * @return
   */
  ClinicOralTariffVO selectClinicOralTariffById(
      @Param("orgId") Integer orgId, @Param("oralTariffId") Integer oralTariffId);

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
  List<BaseOralTariffVO> selectClinicOralTariffExportList(
      @Param("queryForm") ClinicOralTariffQueryForm queryForm);

  List<ClinicOralTariff> selectClinicOralTariffInId(
      @Param("orgId") Integer orgId, @Param("tariffIds") List<Integer> tariffIds);

  /**
   * 根据商品表ID启用门诊商品表项目
   *
   * @param oralTariffId 商品表ID
   * @param userId 操作人ID
   * @param userName 操作人姓名
   */
  void enableClinicOralTariffByTariffId(
          @Param("oralTariffId") Integer oralTariffId,
          @Param("userId") Integer userId,
          @Param("userName") String userName);

  /**
   * 根据商品表ID禁用门诊商品表项目
   *
   * @param oralTariffId 商品表ID
   * @param userId 用户ID
   * @param userName 操作人姓名
   */
  void disableClinicOralTariffByTariffId(
          @Param("oralTariffId") Integer oralTariffId,
          @Param("userId") Integer userId,
          @Param("userName") String userName);
}
