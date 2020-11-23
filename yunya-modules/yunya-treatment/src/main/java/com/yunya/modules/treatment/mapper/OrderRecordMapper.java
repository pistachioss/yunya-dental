package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.vo.OrderProcessVO;
import com.yunya.models.treatment.OrderRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

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
      @Param("orderRecordNum") String orderRecordNum,
      @Param("orgIds") Integer[] orgIds);
}
