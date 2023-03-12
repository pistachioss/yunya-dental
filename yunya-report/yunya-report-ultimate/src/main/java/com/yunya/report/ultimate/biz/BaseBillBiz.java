package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BaseBill;
import com.yunya.models.report.BaseOrganization;
import com.yunya.report.ultimate.mapper.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.yunya.framework.common.constant.BusinessConstants.FREE_PAYMENT_ID;

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
  /** 账单收费记录 */
  @Autowired private BaseBillPayMapper billPayMapper;
  /** 组织 */
  @Autowired private BaseOrganizationMapper organizationMapper;
  /** 账单详情 */
  @Autowired private BaseBillDetailMapper billDetailMapper;

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
    String fileName = "开单记录表";
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    if (null != organization) {
      fileName = organization.getAbbreviation() + fileName;
    }
    excelUtil.exportExcel(response, list, "开单记录表", fileName);
  }

  /**
   * 根据条件查询产品优惠项目明细列表
   *
   * @param query 查询条件
   * @return
   */
  public PageInfo<CouponDiscountItemInfoVO> couponDiscountItems(CouponDiscountItemsQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    // 查询该账单下所有的使用了产品优惠
    List<CouponDiscountItemInfoVO> resultList = mapper.selectCouponDiscountItems(query);
    return new PageInfo<>(resultList);
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
      query.setPrivilegeTypes(new Byte[] {1, 2, 3});
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
    query.setWhetherPage(false);
    List<BillOfDiscountDetailVO> resultList = findBillDiscountDetailList(query).getList();
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
    String fileName = query.getQueryDate() + "应收账款余额表";
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgIds());
    if (null != organization) {
      fileName = organization.getAbbreviation() + fileName;
    }
    excelUtil.exportExcel(response, resultList, "应收账款余额表", fileName);
  }

  /**
   * 根据条件查询门诊账单数据总览
   *
   * @param query 查询条件
   * @return BillDataStatisticsVO
   */
  public BillDataStatisticsVO findClinicBillDataStatistic(DataStatisticsQuery query) {
    query.setPayIds(FREE_PAYMENT_ID);
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
    excelUtil.exportExcel(response, resultList, "患者催缴欠费列表", "患者催缴欠费列表");
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
    excelUtil.exportExcel(response, resultList, "医生所属欠费明细列表", "医生所属欠费明细列表");
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
   * @return 返回结果信息
   */
  public BillDiscountVO billDiscountDetailInfo(Integer billId) {
    BillDiscountVO billDiscountVOs = new BillDiscountVO();
    List<BillDiscountDetailInifoVO> billDiscountDetails = Lists.newArrayList();
    // 查询账单详情列表
    List<BaseBillDetailVO> baseBillDetails = billDetailMapper.selectBillDetailByBillId(billId);
    Map<Integer, BaseBillDetailVO> details =
        baseBillDetails.stream()
            .collect(Collectors.toMap(BaseBillDetailVO::getBillDetailId, (vo) -> vo));
    // 查询该账单下所有的使用了产品优惠
    List<BaseBenefitInfoVO> benefits = mapper.selectBaseBenefitInfoByBillId(billId);
    Map<String, BaseBenefitInfoVO> benefitMap = new HashMap<>(16);
    Map<String, BigDecimal> amountMap = new HashMap<>(16);
    if (StringHelper.isNotEmpty(benefits)) {
      benefits.forEach(
          benefit -> {
            Integer orderDetailId = benefit.getOrderDetailId();
            Integer cardId = benefit.getCardId();
            String key = orderDetailId + "," + cardId;
            BigDecimal amount = amountMap.get(key);
            if (amount == null) {
              amount = BigDecimal.ZERO;
            }
            amountMap.put(key, amount.add(benefit.getBenefitAmount()));
            if (!benefitMap.containsKey(key)) {
              benefitMap.put(key, benefit);
            }
            billDiscountVOs.setOperateUserName(benefit.getAuthorizedName());
          });
    }
    if (StringHelper.isNotEmpty(amountMap)) {
      amountMap.forEach(
          (key, amount) -> {
            BillDiscountDetailInifoVO vo = new BillDiscountDetailInifoVO();
            BaseBenefitInfoVO benefitInfoVO = benefitMap.get(key);
            if (benefitInfoVO != null) {
              vo.setCouponName(benefitInfoVO.getCouponName());
              vo.setSaleChannelName(benefitInfoVO.getSaleChannelName());
              vo.setCardNumber(benefitInfoVO.getCardNumber());
            }

            vo.setBenefitAmount(amount);
            Integer orderDetailId = Integer.parseInt(key.split(",")[0]);
            BaseBillDetailVO detailVO = details.get(orderDetailId);
            BigDecimal quantity = BigDecimal.ZERO;
            if (detailVO != null) {
              BigDecimal price = detailVO.getPrice();
              vo.setItemName(detailVO.getItemName());
              vo.setUnit(detailVO.getUnit());
              vo.setEmployeeName(detailVO.getOperateUserName());
              vo.setPrice(price);
              quantity = compute(price, amount);
              vo.setOriginPrice(quantity.multiply(price));
            }
            vo.setQuantity(quantity.intValue());
            billDiscountDetails.add(vo);
          });
    }
    billDiscountVOs.setBillDiscountDetail(billDiscountDetails);
    return billDiscountVOs;
  }

  /**
   * 计算使用优惠的数量
   *
   * @param price 单价
   * @param amount 优惠总价
   * @return
   */
  private static BigDecimal compute(BigDecimal price, BigDecimal amount) {
    BigDecimal res = BigDecimal.ZERO;
    BigDecimal[] result = amount.divideAndRemainder(price);
    // 商
    BigDecimal quotient = result[0];
    // 余数
    BigDecimal remainder = result[1];
    if (quotient.compareTo(res) == 0 && remainder.compareTo(res) == 0) {
      return res;
    } else {
      res = quotient;
      if (remainder.compareTo(res) > 0) {
        res = res.add(new BigDecimal(1));
      }
    }
    return res;
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
    resultData.setCurrentMonthTotalFreePayAmount(new BigDecimal("0"));
    resultData.setCurrentMonthTotalDebtAmount(new BigDecimal("0"));
    String currentDate = DateUtil.parseDateToStr("yyyy-MM", new Date());
    CurrentMonthBillStatisticVO statisticVO;
    if (currentDate.equals(queryDate)) {
      statisticVO = mapper.selectRealBillStatistic(query);
      BigDecimal currentMonthTotalFreePayAmount =
          billPayMapper.selectCurrentMonthTotalFreePayAmount(query);
      statisticVO.setCurrentMonthTotalFreePayAmount(currentMonthTotalFreePayAmount);
    } else {
      statisticVO = currentMonthBillStatisticsMapper.selectCurrentMonthBillStatistics(query);
    }
    return null == statisticVO ? resultData : statisticVO;
  }

  /**
   * 根据条件导出门诊当月账单收欠费（使用优惠列表）
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportBillCollectionDebt(
      HttpServletResponse response, CurrentMonthBillInfoQuery query) throws IOException {
    List<CurrentMonthBillCollectionDebtVO> resultList =
        mapper.selectCurrentMonthBillCollectionDebtList(query);
    if (StringHelper.isNotEmpty(resultList)) {
      for (CurrentMonthBillCollectionDebtVO vo : resultList) {
        String billDate = vo.getBillDate();
        vo.setCurrentMonthBill(
            query.getCurrentMonth().equals(new DateTime(billDate).toString("yyyy-MM"))
                ? "当月账单"
                : "非当月账单");
      }
    }
    String fileName = "当月收欠费（使用优惠）账单记录";
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    if (organization != null) {
      fileName = organization.getAbbreviation() + query.getCurrentMonth() + fileName;
    }
    ExcelUtil<CurrentMonthBillCollectionDebtVO> excelUtil =
        new ExcelUtil<>(CurrentMonthBillCollectionDebtVO.class);
    excelUtil.exportExcel(response, resultList, "门诊当月收欠费（使用优惠）账单记录", fileName);
  }

  /**
   * 根据条件查询未结账订单列表
   *
   * @param query 查询条件
   * @return PageInfo<BillRecordOfUncheckedVO>
   */
  public PageInfo<BillRecordOfUncheckedVO> findUncheckedBillList(BillUnCheckedQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<BillRecordOfUncheckedVO> resultList = mapper.selectUncheckedBillList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 产品优惠明细导出
   *
   * @param query 查询条件
   * @param response 响应
   * @throws IOException
   */
  public void couponDiscountItemsExport(
      CouponDiscountItemsQuery query, HttpServletResponse response) throws IOException {
    List<CouponDiscountItemInfoVO> resultList = mapper.selectCouponDiscountItems(query);
    ExcelUtil<CouponDiscountItemInfoVO> excelUtil = new ExcelUtil<>(CouponDiscountItemInfoVO.class);
    excelUtil.exportExcel(response, resultList, "产品优惠项目明细", "产品优惠项目明细");
  }

  public List<PatientCostInfoVO> findPatientCostInfo(PatientDimensionQueryForm query) {
    return mapper.selectPatientCostInfo(query);
  }

  public List<EmployeeAmountVO> findPatientDebAmount(ClinicEmployeeWorkloadQuery query, boolean groupByOrgId) {
    return mapper.selectPatientDebtAmount(query, groupByOrgId);
  }
}
