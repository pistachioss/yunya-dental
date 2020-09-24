package com.yunya.modules.treatment.mapper;

import com.yunya.feign.treatment.domain.vo.BillPayRecordVO;
import com.yunya.models.treatment.BillRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

public interface BillRecordMapper extends Mapper<BillRecord> {

  /**
   * 查询门诊某天最大的账单编号
   *
   * @param orgId 组织ID
   * @param date 日期
   * @return
   */
  String selectBillNumberByOrgId(@Param("orgId") Integer orgId, @Param("date") Date date);

  /**
   * 根据开单记录ID查询订单支付记录列表信息
   *
   * @param orderRecordId 开单记录ID
   * @return
   */
  List<BillPayRecordVO> selectBillPayRecord(@Param("orderRecordId") Integer orderRecordId);
}
