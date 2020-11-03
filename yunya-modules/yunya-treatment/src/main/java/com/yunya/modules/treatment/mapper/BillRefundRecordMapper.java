package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.query.BillRefundQuery;
import com.yunya.feign.treatment.domain.vo.BillRefundRecordVO;
import com.yunya.models.treatment.BillRefundRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BillRefundRecordMapper extends Mapper<BillRefundRecord> {

  /**
   * 根据条件查询患者账单退费记录列表
   *
   * @param query 查询条件
   * @return list
   */
  List<BillRefundRecordVO> selectBillRefundList(@Param("query") BillRefundQuery query);
}
