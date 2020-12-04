package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.AssistantRefundDetailQuery;
import com.yunya.feign.report.domain.query.BillRefundRecordQuery;
import com.yunya.feign.report.domain.query.EmployeePersonalWorkloadDetailQuery;
import com.yunya.feign.report.domain.query.EmployeeRefundWorkloadDetailQuery;
import com.yunya.feign.report.domain.vo.AssistantRefundDetailVO;
import com.yunya.feign.report.domain.vo.BillOfRefundRecordVO;
import com.yunya.feign.report.domain.vo.EmployeePersonalRefundWorkloadDetailVO;
import com.yunya.feign.report.domain.vo.EmployeeRefundDetailWorkloadVO;
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

  /**
   * 根据条件查询员工退费工作量明细列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeePersonalRefundWorkloadDetailVO>
   */
  public PageInfo<EmployeePersonalRefundWorkloadDetailVO> findRefundWorkloadDetailList(
      EmployeePersonalWorkloadDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<EmployeePersonalRefundWorkloadDetailVO> resultList =
        mapper.selectEmployeePersonalRefundWorkloadDetail(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出员工退费工作量列表
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportEmployeePersonalRefundWorkloadDetailList(
      HttpServletResponse response, EmployeePersonalWorkloadDetailQuery query) throws IOException {
    PageInfo<EmployeePersonalRefundWorkloadDetailVO> pageInfo = findRefundWorkloadDetailList(query);
    List<EmployeePersonalRefundWorkloadDetailVO> list = pageInfo.getList();
    ExcelUtil<EmployeePersonalRefundWorkloadDetailVO> excelUtil =
        new ExcelUtil<>(EmployeePersonalRefundWorkloadDetailVO.class);
    excelUtil.exportExcel(response, list, "员工账单退费明细表");
  }

  /**
   * 根据条件查询员工退费工作量退费明细列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeRefundDetailWorkloadVO>
   */
  public PageInfo<EmployeeRefundDetailWorkloadVO> findRefundOrderDetailList(
      EmployeeRefundWorkloadDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<EmployeeRefundDetailWorkloadVO> resultList =
        mapper.selectEmployeeRefundOrderDetailList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件查询助手退费金额明细列表
   *
   * @param query 查询条件
   * @return PageInfo<AssistantRefundDetailVO>
   */
  public PageInfo<AssistantRefundDetailVO> findAssistantRefundDetailList(
      AssistantRefundDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<AssistantRefundDetailVO> resultList = mapper.selectAssistantRefundDetailList(query);
    return new PageInfo<>(resultList);
  }
}
