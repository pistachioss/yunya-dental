package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.vo.OrderProcessVO;
import com.yunya.feign.treatment.domain.vo.TreatmentOrderDetailVO;
import com.yunya.feign.treatment_other.domain.query.ReturnVisitQuery;
import com.yunya.models.treatment.OrderRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface OrderRecordMapper extends Mapper<OrderRecord> {
  /**
   * 生成订单编号
   *
   * @param orgId 组织ID
   * @param date 开单日期
   * @return
   */
  String selectOrderNumberByOrgId(@Param("orgId") Integer orgId, @Param("date") Date date);

  /**
   * 订单处理查询（门诊端-订单处理）
   *
   * @param orderRecordNum 订单编号
   * @param orgIds 门诊ID列表
   * @return 订单处理列表
   */
  List<OrderProcessVO> selectOrderProcess(
      @Param("orderRecordNum") String orderRecordNum, @Param("orgIds") Integer[] orgIds);

  /**
   * 根据就诊记录D集合查询订单记录信息列表
   *
   * @param treatmentRecordIds 就诊记录ID集合
   * @return 订单记录列表
   */
  List<OrderRecord> selectOrderRecordByTreatmentIds(
      @Param("treatmentRecordIds") List<Integer> treatmentRecordIds);

  /**
   * 查询当前订单可用预付款支付金额
   *
   * @param orderRecordId 订单记录ID
   * @return
   */
  BigDecimal currentOrderEnablePrepayment(@Param("orderRecordId") Integer orderRecordId);

  /**
   * 获取全部未结账开单记录列表
   *
   * @param treatmentRecordIds 就诊记录ID
   * @return list
   */
  List<OrderRecord> selectAllUnCheckedOrderRecords(
      @Param("treatmentRecordIds") List<Integer> treatmentRecordIds);

  List<TreatmentOrderDetailVO> selectLastTimeTreatOrderRecord(@Param("query") ReturnVisitQuery query);
}
