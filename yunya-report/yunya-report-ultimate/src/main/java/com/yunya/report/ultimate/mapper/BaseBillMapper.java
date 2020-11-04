package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.OrderRecordQuery;
import com.yunya.feign.report.domain.vo.BillOfOrderRecordVO;
import com.yunya.feign.report.domain.query.ArrearsQueryForm;
import com.yunya.feign.report.domain.vo.ArrearsVo;
import com.yunya.models.report.BaseBill;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseBillMapper extends Mapper<BaseBill> {

  /**
   * 根据条件查询开单记录列表
   *
   * @param query 查询条件
   * @return
   */
  List<BillOfOrderRecordVO> selectBillRecordOfOrderList(@Param("query") OrderRecordQuery query);
    /**
     * 欠费查询
     * @param form 欠费查询
     * @param patientIds 患者id
     * @return List<ArrearsVo>
     */
    List<ArrearsVo> arrears(@Param("form") ArrearsQueryForm form,@Param("patientIds") List<Integer> patientIds);
}