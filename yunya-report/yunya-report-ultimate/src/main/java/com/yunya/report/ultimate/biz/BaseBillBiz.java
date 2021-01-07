package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BaseBill;
import com.yunya.report.ultimate.mapper.BaseBillMapper;
import com.yunya.report.ultimate.mapper.CurrentMonthBillStatisticsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
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

  /** 当前月账单统计 */
  @Autowired private CurrentMonthBillStatisticsMapper currentMonthBillStatisticsMapper;

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
    if (StringHelper.isEmpty(query.getPrivilegeTypes())) {
      query.setPrivilegeTypes(new Byte[] {1, 2});
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
    if (StringHelper.isEmpty(query.getPrivilegeTypes())) {
      query.setPrivilegeTypes(new Byte[] {1, 2});
    }
    List<BillOfDiscountDetailVO> resultList = mapper.selectBillDiscountDetailList(query);
    ExcelUtil<BillOfDiscountDetailVO> excelUtil = new ExcelUtil<>(BillOfDiscountDetailVO.class);
    excelUtil.exportExcel(response, resultList, "账单优惠明细列表", "账单优惠明细");
  }

  /**
   * 根据条件查询应收账款余额表
   *
   * @param query 查询条件
   * @return PageInfo<BillRestReceivableAmountVO>
   */
  public PageInfo<BillRestReceivableAmountVO> findBillReceivableAmount(
      BillOfReceivableQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<BillRestReceivableAmountVO> resultList = mapper.selectBillReceivableAmountList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出应收账款余额表
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportBillReceivableAmountList(
      HttpServletResponse response, BillOfReceivableQuery query) throws IOException {
    List<BillRestReceivableAmountVO> resultList = mapper.selectBillReceivableAmountList(query);
    ExcelUtil<BillRestReceivableAmountVO> excelUtil =
        new ExcelUtil<>(BillRestReceivableAmountVO.class);
    excelUtil.exportExcel(response, resultList, "应收账款余额表");
  }

  /**
   * 根据条件查询门诊账单数据总览
   *
   * @param query 查询条件
   * @return BillDataStatisticsVO
   */
  public BillDataStatisticsVO findClinicBillDataStatistic(DataStatisticsQuery query) {
    BillDataStatisticsVO resultData = mapper.selectClinicBillDataStatistic(query);
    return resultData;
  }

  /**
   * 根据条件查询患者催缴欠费列表
   *
   * @param query 查询条件
   * @return PageInfo<PatientArrearsCallForVO>
   */
  public PageInfo<PatientArrearsCallForVO> findPatientArrearsList(
      PatientArrearsCallForQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<PatientArrearsCallForVO> resultList = mapper.selectPatientArrearsList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出患者催缴欠费列表
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportPatientArrearsList(
      HttpServletResponse response, PatientArrearsCallForQuery query) throws IOException {
    List<PatientArrearsCallForVO> resultList = mapper.selectPatientArrearsList(query);
    ExcelUtil<PatientArrearsCallForVO> excelUtil = new ExcelUtil<>(PatientArrearsCallForVO.class);
    excelUtil.exportExcel(response, resultList, "患者催缴欠费列表");
  }

  /**
   * 根据患者ID查询患者欠款明细列表
   *
   * @param patientId 患者ID
   * @return List<PatientArrearsDetailVO>
   */
  public List<PatientArrearsDetailVO> findPatientArrearsDetailList(Integer patientId) {
    List<PatientArrearsDetailVO> resultList = mapper.selectPatientArrearsDetailList(patientId);
    return resultList;
  }

  /**
   * 根据条件查询所属医生催缴欠费列表
   *
   * @param query 查询条件
   * @return PageInfo<DentistArrearsCallForVO>
   */
  public PageInfo<DentistArrearsCallForVO> findDentistArrearsList(
      DentistArrearsCallForQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<DentistArrearsCallForVO> resultList = mapper.selectDentistArrearsList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件所属查询医生催缴欠费明细列表
   *
   * @param query 查询条件
   * @return PageInfo<DentistArrearsDetailVO>
   */
  public PageInfo<DentistArrearsDetailVO> findDentistArrearsDetailList(
      DentistArrearsDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<DentistArrearsDetailVO> resultList = mapper.selectDentistArrearsDetailList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出医生所属欠费明细列表
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportDentistArrearsDetailList(
      HttpServletResponse response, DentistArrearsDetailQuery query) throws IOException {
    List<DentistArrearsDetailVO> resultList = mapper.selectDentistArrearsDetailList(query);
    ExcelUtil<DentistArrearsDetailVO> excelUtil = new ExcelUtil<>(DentistArrearsDetailVO.class);
    excelUtil.exportExcel(response, resultList, "医生所属欠费明细列表");
  }

  /**
   * 查询全部账单欠费统计
   *
   * @return BillArrearsStatisticVO
   */
  public BillArrearsStatisticVO findArrearsStatistic() {
    BillArrearsStatisticVO resultData = mapper.selectBillArrearsStatistic();
    return resultData;
  }

  /**
   * 根据条件查询本月对账单账单收支统计信息
   *
   * @param query 查询条件
   * @return StatementBillIncomeStatisticVO
   */
  public StatementBillIncomeStatisticVO findStatementStatistic(StatementStatisticQuery query) {
    StatementBillIncomeStatisticVO resultData = mapper.selectStatementStatistic(query);
    return resultData;
  }

  /**
   * 根据账单ID查询账单优惠明细
   *
   * @param billId 账单ID
   * @param pageNum 页码 默认1
   * @param pageSize 分页大小 默认10
   * @param whetherPage 是否开启分页 默认开启
   * @return 返回结果信息
   */
  public PageInfo<BillDiscountDetailInifoVO> billDiscountDetailInfo(
      Integer billId, Integer pageNum, Integer pageSize, Boolean whetherPage) {
    if (null == whetherPage || whetherPage) {
      pageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
      pageSize = pageSize == null || pageSize <= 0 ? 10 : pageSize;
      PageHelper.startPage(pageNum, pageSize);
    }
    List<BillDiscountDetailInifoVO> resultList = mapper.selectBillDiscountDetailInfo(billId);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件查询本月对账单账单统计信息
   *
   * @param query 查询条件
   * @return CurrentMonthStatementStatisticVO
   */
  public CurrentMonthBillStatisticVO findCurrentMonthStatementStatistic(
      StatementStatisticQuery query) {
    String queryDate = query.getQueryDate();
    Integer orgId = query.getOrgId();
    CurrentMonthBillStatisticVO resultData = new CurrentMonthBillStatisticVO();
    resultData.setCurrentMonth(queryDate);
    resultData.setOrgId(orgId);
    resultData.setCurrentMonthTotalActualAmount(new BigDecimal("0"));
    resultData.setCurrentMonthTotalDiscountAmount(new BigDecimal("0"));
    resultData.setCurrentMonthTotalReceivedAmount(new BigDecimal("0"));
    resultData.setCurrentMonthTotalDebtAmount(new BigDecimal("0"));
    String currentDate = DateUtil.parseDateToStr("yyyy-MM", new Date());
    CurrentMonthBillStatisticVO statisticVO;
    if (currentDate.equals(queryDate)) {
      statisticVO = mapper.selectRealBillStatistic(query);
    } else {
      statisticVO = currentMonthBillStatisticsMapper.selectCurrentMonthBillStatistics(query);
    }
    return null == statisticVO ? resultData : statisticVO;
  }

}
