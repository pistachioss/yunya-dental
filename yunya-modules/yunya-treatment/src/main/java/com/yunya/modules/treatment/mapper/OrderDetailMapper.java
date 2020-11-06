package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.vo.OrderDetailChargeVO;
import com.yunya.feign.treatment.domain.vo.OrderDetailVO;
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
   * @return
   */
  List<OrderDetailVO> selectOrderDetailVOList(
      @Param("orderRecordId") Integer orderRecordId, @Param("sourceType") Byte sourceType);

  /**
   * 根据开单记录ID查询收费开单明细
   *
   * @param orderRecordId 开单记录ID
   * @return
   */
  List<OrderDetailChargeVO> selectChargeOrderDetailList(
      @Param("orderRecordId") Integer orderRecordId);
}
