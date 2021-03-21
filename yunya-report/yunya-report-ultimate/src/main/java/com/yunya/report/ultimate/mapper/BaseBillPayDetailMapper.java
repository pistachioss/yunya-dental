package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.vo.BaseBillPayDetailVO;
import com.yunya.feign.report.domain.vo.StatementPaymentVO;
import com.yunya.models.report.BaseBillPayDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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
   * @param billPayIds 账单收费id列表
   * @param payIds 入账方式
   * @return
   */
  List<BaseBillPayDetailVO> sumPayDetailListByBillPayIds(
      @Param("billPayIds") Collection<Integer> billPayIds,
      @Param("payIds") Collection<Integer> payIds);

  /**
   * 根据billId分组，统计免单支付总额
   *
   * @param billIds 账单id列表
   * @param payIds 入账方式
   * @return
   */
  List<BaseBillPayDetailVO> sumPayDetailListByBillIds(
      @Param("billIds") Collection<Integer> billIds, @Param("payIds") Collection<Integer> payIds);

  /**
   * 查询支付记录免单总额
   *
   * @param billPayIds 支付记录ID
   * @return BigDecimal
   */
  BigDecimal selectTotalFreePayAmount(@Param("billPayIds") Set<Integer> billPayIds);

  /**
   * 免单支付金额
   *
   * @param billPayId 支付记录ID
   * @return BigDecimal
   */
  BigDecimal selectFreePayAmount(@Param("billPayId") Integer billPayId);
}
