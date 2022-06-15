package com.yunya.modules.treatment.mapper;

import com.yunya.feign.clinic_base.domain.model.SpecialistProjectReportModel;
import com.yunya.feign.report.domain.query.CategoryIncomeQuery;
import com.yunya.feign.report.domain.query.SpecialistProjectCompletedCountQuery;
import com.yunya.feign.report.domain.vo.BillItemAmountSharedVO;
import com.yunya.feign.treatment.domain.query.SpecialistProjectTariffCompletedInfoQuery;
import com.yunya.feign.treatment.domain.vo.*;
import com.yunya.models.treatment.OrderDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface OrderDetailMapper extends Mapper<OrderDetail> {

  /**
   * 根据开单记录ID查询开单详情列表
   *
   * @param orderRecordId 开单记录ID
   * @param sourceType 添加来源（0-开单；1-收费）
   * @return List<OrderDetailVO>
   */
  List<OrderDetailVO> selectOrderDetailVOList(
      @Param("orderRecordId") Integer orderRecordId, @Param("sourceType") Byte sourceType);

  /**
   * 根据开单记录ID查询收费开单明细
   *
   * @param orderRecordId 开单记录ID
   * @return List<OrderDetailChargeVO>
   */
  List<OrderDetailChargeVO> selectChargeOrderDetailList(
      @Param("orderRecordId") Integer orderRecordId);

  /**
   * 打印账单信息
   *
   * @param patientId 患者ID
   * @param billNumber 账单编号
   * @return 返回账单信息
   */
  BillPrintInfoVO billPrintInfo(
      @Param("patientId") Integer patientId, @Param("billNumber") String billNumber);

  /**
   * 查询总数
   *
   * @param specialistProjectReportModel 查询条件
   * @return Integer
   */
  Integer selectCountTariffSpecialist(
      @Param("form") SpecialistProjectReportModel specialistProjectReportModel);

  /**
   * 查询占比数量
   *
   * @param specialistProjectReportModel 查询条件
   * @return Integer
   */
  List<SpecialistTariffProjectVO> selectTariffSpecialistPercentage(
      @Param("form") SpecialistProjectReportModel specialistProjectReportModel);

  /**
   * 计算占比
   *
   * @param number 专科数量
   * @param count 总数量
   * @return String
   */
  String percentage(@Param("number") Integer number, @Param("count") Integer count);

  /**
   * 根据条件查询专科项目开单项目完成数量
   *
   * @param query 查询条件
   * @return 开单项目完成数量
   */
  Integer selectSpecialistProjectTariffCompletedAmount(
      @Param("query") SpecialistProjectTariffCompletedInfoQuery query);

  /**
   * 根据条件查询专科项目开单明细列表
   *
   * @param query 查询条件
   * @return List<SpecialistTariffCompletedDetailVO>
   */
  List<SpecialistTariffCompletedDetailVO> selectSpecialistProjectTariffDetail(
      @Param("query") SpecialistProjectTariffCompletedInfoQuery query);

  /**
   * 根据条件查询专科项目开单完成数量
   *
   * @param query 查询条件
   * @return Integer
   */
  Integer selectSpecialistProjectCompletedCount(
      @Param("query") SpecialistProjectCompletedCountQuery query);

  List<OrderDetail> selectSpecialistProjectCompletedList(@Param("query") SpecialistProjectCompletedCountQuery query);

  /**
   * 查询门诊下项目分类的原价合计
   *
   * @param query
   * @return
   */
  List<ClinicTariffOrderVO> selectClinicTariffCategoryOriginalAmount(@Param("query") CategoryIncomeQuery query);

  /**
   * 查询门诊下开单项目的应收列表
   * @param query
   * @param payIds 不属于的收费id
   * @return
   */
  List<BillItemAmountSharedVO> selectClinicOrderDetailList(@Param("query") CategoryIncomeQuery query, @Param("payIds") List<Integer> payIds);

  /**
   * 查询撤销前的免单项目的原价列表
   * @param payIds 撤销的收费id
   * @return
   */
  List<BillItemAmountSharedVO> selectClinicOrderDetailAfterRevoke(@Param("payIds") List<Integer> payIds);
}
