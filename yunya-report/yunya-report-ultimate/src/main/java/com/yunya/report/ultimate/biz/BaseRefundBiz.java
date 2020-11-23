package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.BillRefundRecordQuery;
import com.yunya.feign.report.domain.vo.BillOfRefundRecordVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BaseRefund;
import com.yunya.report.ultimate.mapper.BaseRefundMapper;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 简介: 数据记录-账单记录-账单退费记录业务层
 *
 * @author: chow
 * @date: 2020/11/23 13:28
 * @description:
 * @since: 1.0.0
 */
@Service
public class BaseRefundBiz extends BaseBiz<BaseRefundMapper, BaseRefund> {

  /**
   * 根据条件查询账单退费记录列表
   *
   * @param query 查询条件
   * @return PageInfo<BillOfRefundRecordVO>
   */
  public PageInfo<BillOfRefundRecordVO> findBillRefundRecord(BillRefundRecordQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<BillOfRefundRecordVO> resultList = mapper.selectBillRefundRecord(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出账单退费记录列表
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportBillRefundRecord(HttpServletResponse response, BillRefundRecordQuery query)
      throws IOException {
    List<BillOfRefundRecordVO> list = mapper.selectBillRefundRecord(query);
    ExcelUtil<BillOfRefundRecordVO> excelUtil = new ExcelUtil<>(BillOfRefundRecordVO.class);
    excelUtil.exportExcel(response, list, "账单退费记录表");
  }
}
