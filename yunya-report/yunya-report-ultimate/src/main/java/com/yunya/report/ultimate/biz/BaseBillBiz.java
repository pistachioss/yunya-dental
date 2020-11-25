package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.BillOfDiscountDetailQuery;
import com.yunya.feign.report.domain.query.OrderRecordQuery;
import com.yunya.feign.report.domain.vo.BillOfDiscountDetailVO;
import com.yunya.feign.report.domain.vo.BillOfOrderRecordVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BaseBill;
import com.yunya.report.ultimate.mapper.BaseBillMapper;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 简介: 账单报表业务层
 *
 * @author: chow
 * @date: 2020/10/27 10:28
 * @description:
 * @since: 1.0.0
 */
@Service
public class BaseBillBiz extends BaseBiz<BaseBillMapper, BaseBill> {

  /**
   * 根据条件查询开单列表
   *
   * @param query 查询条件
   * @return list
   */
  public PageInfo<BillOfOrderRecordVO> findBillRecordOfOrderList(OrderRecordQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<BillOfOrderRecordVO> resultList = mapper.selectBillRecordOfOrderList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出开单记录
   *
   * @param response 响应
   * @param query 查询条件
   * @throws IOException
   */
  public void exportBillOfOrderRecord(HttpServletResponse response, OrderRecordQuery query)
      throws IOException {
    List<BillOfOrderRecordVO> list = mapper.selectBillRecordOfOrderList(query);
    ExcelUtil<BillOfOrderRecordVO> excelUtil = new ExcelUtil<>(BillOfOrderRecordVO.class);
    excelUtil.exportExcel(response, list, "开单记录表");
  }

  /**
   * 根据条件查询账单优惠明细列表
   *
   * @param query 查询条件
   * @return
   */
  public PageInfo<BillOfDiscountDetailVO> findBillDiscountDetailList(
      BillOfDiscountDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<BillOfDiscountDetailVO> resultList = mapper.selectBillDiscountDetailList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出账单优惠明细列表
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportDiscountDetailList(
      HttpServletResponse response, BillOfDiscountDetailQuery query) throws IOException {
    List<BillOfDiscountDetailVO> resultList = mapper.selectBillDiscountDetailList(query);
    ExcelUtil<BillOfDiscountDetailVO> excelUtil = new ExcelUtil<>(BillOfDiscountDetailVO.class);
    excelUtil.exportExcel(response, resultList, "账单优惠明细列表");
  }
}
