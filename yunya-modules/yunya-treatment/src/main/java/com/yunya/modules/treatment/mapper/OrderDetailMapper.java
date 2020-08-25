package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.vo.OrderDetailVO;
import com.yunya.models.treatment.OrderDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface OrderDetailMapper extends Mapper<OrderDetail> {
  /**
   * 根据就诊记录ID查询开单详情列表
   *
   * @param orderRecordId 就诊记录ID
   * @return
   */
  List<OrderDetailVO> selectOrderDetailVOList(
      @Param("treatmentRecordId") Integer orderRecordId);
}
