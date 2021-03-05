package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.vo.StatementPaymentVO;
import com.yunya.models.report.BaseBillPayDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Collection;
import java.util.List;

public interface BaseBillPayDetailMapper extends Mapper<BaseBillPayDetail> {

  /**
   * 根据收费记录ID查询收费明细并按照全部支付方式分组
   *
   * @param billPayId 收费记录ID
   * @return List<StatementPaymentVO>
   */
  List<StatementPaymentVO> selectBillPayDetailList(@Param("billPayId") Integer billPayId);

  /**
   * 根据billId分组，统计免单支付总额
   *
   * @param billIds
   * @param payIds
   * @return
   */
  List<BaseBillPayDetail> sumPayDetailList(@Param("billIds") Collection<Integer> billIds, @Param("payIds") Collection<Integer> payIds);
}
