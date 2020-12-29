package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.query.SpecialistProjectTariffCompletedInfoQuery;
import com.yunya.feign.treatment.domain.vo.BillPrintInfoVO;
import com.yunya.feign.treatment.domain.vo.OrderDetailChargeVO;
import com.yunya.feign.treatment.domain.vo.OrderDetailVO;
import com.yunya.feign.treatment.domain.vo.SpecialistTariffCompletedDetailVO;
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
}
