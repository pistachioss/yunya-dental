package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.bo.ClinicWorkloadGroupInfoVO;
import com.yunya.feign.report.domain.query.BillPayRecordQuery;
import com.yunya.feign.report.domain.query.DataStatisticsQuery;
import com.yunya.feign.report.domain.query.StatementBillChargeDetailInfoQuery;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BaseBillPay;
import com.yunya.models.report.BaseOrganization;
import com.yunya.report.ultimate.mapper.BaseBillPayDetailMapper;
import com.yunya.report.ultimate.mapper.BaseBillPayMapper;
import com.yunya.report.ultimate.mapper.BaseOrganizationMapper;
import com.yunya.report.ultimate.mapper.BaseRefundDetailMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;

import static com.yunya.framework.common.constant.BusinessConstants.ACCOUNT_ITEM_OF_MEMBER;
import static com.yunya.framework.common.constant.BusinessConstants.ACCOUNT_ITEM_OF_PREPARE;

/**
 * 简介: 账单收费记录业务层
 *
 * @author: chow
 * @date: 2020/10/27 16:49
 * @description:
 * @since: 1.0.0
 */
@Service
public class BaseBillPayBiz extends BaseBiz<BaseBillPayMapper, BaseBillPay> {
  /** 开单明细 */
  @Autowired private BaseBillDetailBiz billDetailBiz;
  /** 收费记录明细 */
  @Autowired private BaseBillPayDetailMapper billPayDetailMapper;
  /** 组织 */
  @Autowired private BaseOrganizationMapper organizationMapper;
  /** 退费订单明细 */
  @Autowired private BaseRefundDetailMapper refundDetailMapper;

  /**
   * 构建门诊工作量相关信息
   *
   * @param query 查询条件
   * @return 门诊工作量
   */
  public ClinicWorkloadGroupInfoVO generateClinicWorkloadInfo(DataStatisticsQuery query) {
    ClinicWorkloadGroupInfoVO resultData = new ClinicWorkloadGroupInfoVO();
    BigDecimal firstReceivedAmount = BigDecimal.ZERO;
    BigDecimal firstReceivedWorkload = BigDecimal.ZERO;
    BigDecimal firstFreePayWorkload = BigDecimal.ZERO;
    BigDecimal firstCouponWorkload = BigDecimal.ZERO;
    BigDecimal beCollectedReceivedAmount = BigDecimal.ZERO;
    BigDecimal beCollectedReceivedWorkload = BigDecimal.ZERO;
    BigDecimal beCollectedFreePayWorkload = BigDecimal.ZERO;
    BigDecimal beCollectedCouponWorkload = BigDecimal.ZERO;
    BigDecimal arrearsReceivedAmount = BigDecimal.ZERO;
    BigDecimal arrearsReceivedWorkload = BigDecimal.ZERO;
    BigDecimal arrearsFreePayWorkload = BigDecimal.ZERO;
    BigDecimal arrearsCouponWorkload = BigDecimal.ZERO;
    BigDecimal totalRefundWorkload = BigDecimal.ZERO;
    List<BillIdAndBillPayIdVO> vos = mapper.selectBillIdsAndBillPayIds(query);
    if (StringHelper.isNotEmpty(vos)) {
      Set<Integer> billIds = new HashSet<>();
      for (BillIdAndBillPayIdVO vo : vos) {
        Integer billId = vo.getBillId();
        billIds.add(billId);
        Integer billOrgId = vo.getBillOrgId();
        Date billDate = vo.getBillDate();
        BigDecimal actualAmount = vo.getActualAmount();
        Integer privilegeOrgId = vo.getPrivilegeOrgId();
        Date privilegeDate = vo.getPrivilegeDate();
        Integer payeeOrgId = vo.getPayeeOrgId();
        Date payeeDate = vo.getPayeeDate();
        Integer billPayId = vo.getBillPayId();
        BigDecimal receivedAmount = vo.getReceivedAmount();
        // 开单明细工作量列表
        List<BaseBillDetailToWorkloadVO> billDetailToWorkloadVOS =
            billDetailBiz.findBillDetailForWorkload(billId);
        // 收费使用免单金额
        BigDecimal freePayAmount = billPayDetailMapper.selectFreePayAmount(billPayId);
        if (billOrgId.equals(payeeOrgId)) {
          if (billDate.equals(payeeDate)) {
            firstReceivedAmount = firstReceivedAmount.add(receivedAmount);
            firstReceivedWorkload =
                calculateReceivedWorkload(
                    firstReceivedWorkload, receivedAmount, actualAmount, billDetailToWorkloadVOS);
            firstFreePayWorkload =
                calculateFreePayWorkload(
                    firstFreePayWorkload, actualAmount, freePayAmount, billDetailToWorkloadVOS);
          } else {
            arrearsReceivedAmount = arrearsReceivedAmount.add(receivedAmount);
            arrearsReceivedWorkload =
                calculateReceivedWorkload(
                    arrearsReceivedWorkload, receivedAmount, actualAmount, billDetailToWorkloadVOS);
            arrearsFreePayWorkload =
                calculateFreePayWorkload(
                    arrearsFreePayWorkload, actualAmount, freePayAmount, billDetailToWorkloadVOS);
          }
        } else {
          beCollectedReceivedAmount = beCollectedReceivedAmount.add(receivedAmount);
          beCollectedReceivedWorkload =
              calculateReceivedWorkload(
                  beCollectedReceivedWorkload,
                  receivedAmount,
                  actualAmount,
                  billDetailToWorkloadVOS);
          beCollectedFreePayWorkload =
              calculateFreePayWorkload(
                  beCollectedFreePayWorkload, actualAmount, freePayAmount, billDetailToWorkloadVOS);
        }
        if (billOrgId.equals(privilegeOrgId)) {
          if (billDate.equals(privilegeDate)) {
            firstCouponWorkload =
                calculateCouponWorkload(firstCouponWorkload, billDetailToWorkloadVOS);
          } else {
            arrearsCouponWorkload =
                calculateCouponWorkload(arrearsCouponWorkload, billDetailToWorkloadVOS);
          }
        } else {
          beCollectedCouponWorkload =
              calculateCouponWorkload(beCollectedCouponWorkload, billDetailToWorkloadVOS);
        }
      }
      totalRefundWorkload = refundDetailMapper.selectTotalRefundWorkload(billIds);
    }
    resultData.setFirstReceivedAmount(firstReceivedAmount);
    resultData.setFirstReceivedWorkload(firstReceivedWorkload);
    resultData.setFirstFreePayWorkload(firstFreePayWorkload);
    resultData.setFirstCouponWorkload(firstCouponWorkload);
    resultData.setBeCollectedReceivedAmount(beCollectedReceivedAmount);
    resultData.setBeCollectedReceivedWorkload(beCollectedReceivedWorkload);
    resultData.setBeCollectedFreePayWorkload(beCollectedFreePayWorkload);
    resultData.setBeCollectedCouponWorkload(beCollectedCouponWorkload);
    resultData.setArrearsReceivedAmount(arrearsReceivedAmount);
    resultData.setArrearsReceivedWorkload(arrearsReceivedWorkload);
    resultData.setArrearsFreePayWorkload(arrearsFreePayWorkload);
    resultData.setArrearsCouponWorkload(arrearsCouponWorkload);
    resultData.setTotalRefundWorkload(totalRefundWorkload);
    return resultData;
  }

  /**
   * 计算首次收费补入工作量
   *
   * @param firstCouponWorkload 首次收费补入工作量
   * @param billDetailToWorkloads 订单明细
   * @return BigDecimal - 首次收费补入工作量
   */
  private BigDecimal calculateCouponWorkload(
      BigDecimal firstCouponWorkload, List<BaseBillDetailToWorkloadVO> billDetailToWorkloads) {
    for (BaseBillDetailToWorkloadVO vo : billDetailToWorkloads) {
      firstCouponWorkload = firstCouponWorkload.add(vo.getBillDetailCouponWorkload());
    }
    return firstCouponWorkload;
  }

  /**
   * 计算首次收费免单工作量合计
   *
   * @param firstFreePayWorkload 首次收费免单工作量
   * @param freePayAmount 收费免单金额
   * @param actualAmount 实收总和
   * @param billDetailToWorkloads 首次收费订单工作量明细
   * @return BigDecimal - 首次收费免单工作量
   */
  private BigDecimal calculateFreePayWorkload(
      BigDecimal firstFreePayWorkload,
      BigDecimal freePayAmount,
      BigDecimal actualAmount,
      List<BaseBillDetailToWorkloadVO> billDetailToWorkloads) {
    if (actualAmount.compareTo(BigDecimal.ZERO) > 0) {
      for (BaseBillDetailToWorkloadVO vo : billDetailToWorkloads) {
        BigDecimal billDetailWorkload = vo.getBillDetailWorkload();
        if (billDetailWorkload.compareTo(BigDecimal.ZERO) > 0) {
          firstFreePayWorkload =
              firstFreePayWorkload.add(
                  billDetailWorkload
                      .divide(actualAmount, 4, BigDecimal.ROUND_HALF_UP)
                      .multiply(freePayAmount));
        }
      }
    }
    return firstFreePayWorkload;
  }

  /**
   * 计算首次收费工作量
   *
   * @param firstReceivedWorkload 首次收费工作量
   * @param receivedAmount 首次收费金额
   * @param actualAmount 实收工作量
   * @param billDetailToWorkloads 开单明细工作量列表
   * @return BigDecimal - 首次收费工作量合计
   */
  private BigDecimal calculateReceivedWorkload(
      BigDecimal firstReceivedWorkload,
      BigDecimal receivedAmount,
      BigDecimal actualAmount,
      List<BaseBillDetailToWorkloadVO> billDetailToWorkloads) {
    if (actualAmount.compareTo(BigDecimal.ZERO) > 0) {
      for (BaseBillDetailToWorkloadVO vo : billDetailToWorkloads) {
        firstReceivedWorkload =
            firstReceivedWorkload.add(
                vo.getBillDetailWorkload()
                    .divide(actualAmount, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(receivedAmount));
      }
    }
    return firstReceivedWorkload;
  }

  /**
   * 根据账单ID（开单记录ID）查询收费记录列表
   *
   * @param billId 账单ID
   * @return List<BaseBillPayVO>
   */
  public List<BaseBillPayVO> findBillPayList(Integer billId) {
    return mapper.selectBillPayList(billId);
  }

  /**
   * 根据条件查询账单支付记录列表
   *
   * @param query 查询条件
   * @return
   */
  public PageInfo<BillOfPayRecordVO> findBillRecordOfPayList(BillPayRecordQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<BillOfPayRecordVO> resultList = mapper.selectBillRecordOfPayList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出账单收费记录列表
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportBillOfPayRecord(HttpServletResponse response, BillPayRecordQuery query)
      throws IOException {
    List<BillOfPayRecordVO> list = mapper.selectBillRecordOfPayList(query);
    ExcelUtil<BillOfPayRecordVO> excelUtil = new ExcelUtil<>(BillOfPayRecordVO.class);
    String fileName = "账单收费记录表";
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    if (null != organization) {
      fileName =
          MessageFormat.format(
              "{0}{1}-{2}{3}",
              organization.getAbbreviation(),
              query.getBillStartDate(),
              query.getBillEndDate(),
              fileName);
    }
    excelUtil.exportExcel(response, list, "账单收费记录表", fileName);
  }

  /**
   * 根据条件查询门诊收费数据总览
   *
   * @param query 查询条件
   * @return TollDataStatisticsVO
   */
  public TollDataStatisticsVO findClinicTollDataStatistic(DataStatisticsQuery query) {
    TollDataStatisticsVO resultData = mapper.selectClinicTollDataStatistic(query);
    return resultData;
  }

  /**
   * 根据条件查询账单收费（本月账单本月首次收费）详情信息列表
   *
   * @param query 查询条件
   * @return PageInfo<StatementBillChargeDetailVO>
   */
  public PageInfo<StatementBillChargeDetailVO> findBillChargeDetailInfoList(
      StatementBillChargeDetailInfoQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<StatementBillChargeDetailVO> resultList = mapper.selectBillChargeDetailInfoList(query);
    generateBillChargeAccountItemDetail(resultList);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出门诊账单收费（本月）明细列表
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportBillChargeDetailInfoList(
      HttpServletResponse response, StatementBillChargeDetailInfoQuery query) throws IOException {
    PageInfo<StatementBillChargeDetailVO> pageInfo = findBillChargeDetailInfoList(query);
    List<StatementBillChargeDetailVO> resultList = pageInfo.getList();
    ExcelUtil<StatementBillChargeDetailExportVO> excelUtil =
        new ExcelUtil<>(StatementBillChargeDetailExportVO.class);
    // 构建导出数据列表
    List<StatementBillChargeDetailExportVO> exportList =
        buildStatementBillChargeDetailExportList(resultList);
    String fileName = "门诊账单收费（本月）明细列表";
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    if (null != organization) {
      fileName =
          MessageFormat.format(
              "{0}{1}-{2}{3}",
              organization.getAbbreviation(), query.getStartDate(), query.getEndDate(), fileName);
    }
    excelUtil.exportExcel(response, exportList, "门诊账单收费（本月）明细", fileName);
  }

  /**
   * 根据条件查询账单收欠费（本月账单本月收费）详情信息列表
   *
   * @param query 查询条件
   * @return PageInfo<StatementBillChargeDetailVO>
   */
  public PageInfo<StatementBillChargeDetailVO> findBillCurrentCollectDebtDetailList(
      StatementBillChargeDetailInfoQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<StatementBillChargeDetailVO> resultList =
        mapper.selectBillCurrentChargeDebtDetailList(query);
    generateBillChargeAccountItemDetail(resultList);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出账单收欠费（本月账单本月收费）详情信息列表
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportBillCollectDebtDetailList(
      HttpServletResponse response, StatementBillChargeDetailInfoQuery query) throws IOException {
    PageInfo<StatementBillChargeDetailVO> pageInfo = findBillCurrentCollectDebtDetailList(query);
    List<StatementBillChargeDetailVO> resultList = pageInfo.getList();
    ExcelUtil<StatementBillChargeDetailExportVO> excelUtil =
        new ExcelUtil<>(StatementBillChargeDetailExportVO.class);
    // 构建导出数据列表
    List<StatementBillChargeDetailExportVO> exportList =
        buildStatementBillChargeDetailExportList(resultList);
    String fileName = "门诊账单收欠费（本月）明细列表";
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    if (null != organization) {
      fileName =
          MessageFormat.format(
              "{0}{1}-{2}{3}",
              organization.getAbbreviation(), query.getStartDate(), query.getEndDate(), fileName);
    }
    excelUtil.exportExcel(response, exportList, "门诊账单收欠费（本月）明细", fileName);
  }

  /**
   * 根据条件查询账单收欠费（非本月账单本月收费）详情信息列表
   *
   * @param query 查询条件
   * @return PageInfo<StatementBillChargeDetailVO>
   */
  public PageInfo<StatementBillChargeDetailVO> findOtherCollectDebtDetailList(
      StatementBillChargeDetailInfoQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<StatementBillChargeDetailVO> resultList =
        mapper.selectBillOtherChargeDebtDetailList(query);
    generateBillChargeAccountItemDetail(resultList);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出账单收欠费（非本月账单本月收费）详情信息列表
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportBillOtherCollectDebtDetailList(
      HttpServletResponse response, StatementBillChargeDetailInfoQuery query) throws IOException {
    PageInfo<StatementBillChargeDetailVO> pageInfo = findOtherCollectDebtDetailList(query);
    List<StatementBillChargeDetailVO> resultList = pageInfo.getList();
    ExcelUtil<StatementBillChargeDetailExportVO> excelUtil =
        new ExcelUtil<>(StatementBillChargeDetailExportVO.class);
    // 构建导出数据列表
    List<StatementBillChargeDetailExportVO> exportList =
        buildStatementBillChargeDetailExportList(resultList);
    String fileName = "门诊账单收欠费（非本月）明细列表";
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    if (null != organization) {
      fileName =
          MessageFormat.format(
              "{0}{1}-{2}{3}",
              organization.getAbbreviation(), query.getStartDate(), query.getEndDate(), fileName);
    }
    excelUtil.exportExcel(response, exportList, "门诊账单收欠费（非本月）明细", fileName);
  }

  /**
   * 根据条件查询门诊账单代收详情信息列表
   *
   * @param query 查询条件
   * @return
   */
  public PageInfo<StatementBillChargeDetailVO> findCurrentBillCollectionDetailList(
      StatementBillChargeDetailInfoQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<StatementBillChargeDetailVO> resultList =
        mapper.selectCurrentBillCollectionDetailList(query);
    generateBillChargeAccountItemDetail(resultList);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出诊所代收(本月)记录明细
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportCurrentBillCollectionDetailList(
      HttpServletResponse response, StatementBillChargeDetailInfoQuery query) throws IOException {
    PageInfo<StatementBillChargeDetailVO> pageInfo = findCurrentBillCollectionDetailList(query);
    List<StatementBillChargeDetailVO> list = pageInfo.getList();
    // 构建导出数据列表
    List<StatementBillChargeDetailExportVO> exportList =
        buildStatementBillChargeDetailExportList(list);
    ExcelUtil<StatementBillChargeDetailExportVO> excelUtil =
        new ExcelUtil<>(StatementBillChargeDetailExportVO.class);
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    String fileName = "诊所代收(本月)记录明细列表";
    if (null != organization) {
      String abbreviation = organization.getAbbreviation();
      fileName = abbreviation + fileName;
    }
    excelUtil.exportExcel(response, exportList, "诊所代收(本月)记录明细", fileName);
  }

  /**
   * 根据条件查询门诊账单代（非本月）收详情信息列表
   *
   * @param query 查询条件
   * @return PageInfo<StatementBillChargeDetailVO>
   */
  public PageInfo<StatementBillChargeDetailVO> findOtherBillCollectionDetailList(
      StatementBillChargeDetailInfoQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<StatementBillChargeDetailVO> resultList =
        mapper.selectOtherBillCollectionDetailList(query);
    generateBillChargeAccountItemDetail(resultList);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出诊所代收(非本月)记录明细
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportOtherBillCollectionDetailList(
      HttpServletResponse response, StatementBillChargeDetailInfoQuery query) throws IOException {
    PageInfo<StatementBillChargeDetailVO> pageInfo = findOtherBillCollectionDetailList(query);
    List<StatementBillChargeDetailVO> list = pageInfo.getList();
    // 构建导出数据列表
    List<StatementBillChargeDetailExportVO> exportList =
        buildStatementBillChargeDetailExportList(list);
    ExcelUtil<StatementBillChargeDetailExportVO> excelUtil =
        new ExcelUtil<>(StatementBillChargeDetailExportVO.class);
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    String fileName = "诊所代收(非本月)记录明细列表";
    if (null != organization) {
      String abbreviation = organization.getAbbreviation();
      fileName = abbreviation + fileName;
    }
    excelUtil.exportExcel(response, exportList, "诊所代收(非本月)记录明细", fileName);
  }

  /**
   * 根据条件查询诊所被代收账（本月）明细列表
   *
   * @param query 查询条件
   * @return PageInfo<StatementBillChargeDetailVO>
   */
  public PageInfo<StatementBillChargeDetailVO> findCurrentBillIsAcceptedDetailList(
      StatementBillChargeDetailInfoQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<StatementBillChargeDetailVO> resultList =
        mapper.selectCurrentBillIsAcceptedDetailList(query);
    generateBillChargeAccountItemDetail(resultList);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出诊所被代收帐(本月)记录明细
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportCurrentBillIsAcceptedDetailList(
      HttpServletResponse response, StatementBillChargeDetailInfoQuery query) throws IOException {
    PageInfo<StatementBillChargeDetailVO> pageInfo = findCurrentBillIsAcceptedDetailList(query);
    List<StatementBillChargeDetailVO> list = pageInfo.getList();
    // 构建导出数据列表
    List<StatementBillChargeDetailExportVO> exportList =
        buildStatementBillChargeDetailExportList(list);
    ExcelUtil<StatementBillChargeDetailExportVO> excelUtil =
        new ExcelUtil<>(StatementBillChargeDetailExportVO.class);
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    String fileName = "诊所被代收账(本月)记录明细列表";
    if (null != organization) {
      String abbreviation = organization.getAbbreviation();
      fileName = abbreviation + fileName;
    }
    excelUtil.exportExcel(response, exportList, "诊所被代收账(本月)记录明细", fileName);
  }

  /**
   * 根据条件查询诊所被代收账（非本月）明细列表
   *
   * @param query 查询条件
   * @return PageInfo<StatementBillChargeDetailVO>
   */
  public PageInfo<StatementBillChargeDetailVO> findOtherBillIsAcceptedDetailList(
      StatementBillChargeDetailInfoQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<StatementBillChargeDetailVO> resultList =
        mapper.selectOtherBillIsAcceptedDetailList(query);
    generateBillChargeAccountItemDetail(resultList);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出诊所被代收帐(非本月)记录明细
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportOtherBillIsAcceptedDetailList(
      HttpServletResponse response, StatementBillChargeDetailInfoQuery query) throws IOException {
    PageInfo<StatementBillChargeDetailVO> pageInfo = findOtherBillIsAcceptedDetailList(query);
    List<StatementBillChargeDetailVO> list = pageInfo.getList();
    // 构建导出数据列表
    List<StatementBillChargeDetailExportVO> exportList =
        buildStatementBillChargeDetailExportList(list);
    ExcelUtil<StatementBillChargeDetailExportVO> excelUtil =
        new ExcelUtil<>(StatementBillChargeDetailExportVO.class);
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    String fileName = "诊所被代收账(非本月)记录明细列表";
    if (null != organization) {
      String abbreviation = organization.getAbbreviation();
      fileName = abbreviation + fileName;
    }
    excelUtil.exportExcel(response, exportList, "诊所被代收账(非本月)记录明细", fileName);
  }

  /**
   * 构建账单收费记录的支付方式明细信息
   *
   * @param resultList 账单收费记录列表
   */
  private void generateBillChargeAccountItemDetail(List<StatementBillChargeDetailVO> resultList) {
    if (StringHelper.isNotEmpty(resultList)) {
      for (StatementBillChargeDetailVO vo : resultList) {
        List<StatementPaymentVO> statementPaymentResult = new ArrayList<>();
        List<StatementPaymentVO> statementPayments =
            billPayDetailMapper.selectBillPayDetailList(vo.getBillPayId());
        setStatementPaymentValue(statementPaymentResult, statementPayments);
        vo.setStatementPayments(statementPaymentResult);
      }
    }
  }

  /**
   * 设置对账单会员卡、预付款本金、赠金
   *
   * @param statementPaymentResult 原对账单列表
   * @param statementPayments 对账单结果
   */
  static void setStatementPaymentValue(
      List<StatementPaymentVO> statementPaymentResult, List<StatementPaymentVO> statementPayments) {
    if (StringHelper.isNotEmpty(statementPayments)) {
      for (StatementPaymentVO payment : statementPayments) {
        String accountItemName = payment.getAccountItemName();
        // 将支付方式名称为：会员卡或预付款的支付港式拆分为-会员卡本金/赠金；预付款本金/赠金
        switch (accountItemName) {
          case ACCOUNT_ITEM_OF_MEMBER:
            Integer memberCardAccountItemId = payment.getAccountItemId();
            BigDecimal memberCardTotalAmount = payment.getTotalAmount();
            BigDecimal memberCardBonusAmount = payment.getBonusAmount();
            StatementPaymentVO memberCardPrinciple = new StatementPaymentVO();
            memberCardPrinciple.setAccountItemId(memberCardAccountItemId);
            memberCardPrinciple.setAccountItemName("会员卡本金");
            memberCardPrinciple.setTotalAmount(memberCardTotalAmount);
            statementPaymentResult.add(0, memberCardPrinciple);
            StatementPaymentVO memberCardBonus = new StatementPaymentVO();
            memberCardBonus.setAccountItemId(memberCardAccountItemId);
            memberCardBonus.setAccountItemName("会员卡赠金");
            memberCardBonus.setTotalAmount(memberCardBonusAmount);
            statementPaymentResult.add(1, memberCardBonus);
            break;
          case ACCOUNT_ITEM_OF_PREPARE:
            Integer prePaidCardAccountItemId = payment.getAccountItemId();
            BigDecimal prePaidCardTotalAmount = payment.getTotalAmount();
            BigDecimal prePaidCardBonusAmount = payment.getBonusAmount();
            StatementPaymentVO prePaidCardPrinciple = new StatementPaymentVO();
            prePaidCardPrinciple.setAccountItemId(prePaidCardAccountItemId);
            prePaidCardPrinciple.setAccountItemName("预付款本金");
            prePaidCardPrinciple.setTotalAmount(prePaidCardTotalAmount);
            statementPaymentResult.add(2, prePaidCardPrinciple);
            StatementPaymentVO prepaidCardBonus = new StatementPaymentVO();
            prepaidCardBonus.setAccountItemId(prePaidCardAccountItemId);
            prepaidCardBonus.setAccountItemName("预付款赠金");
            prepaidCardBonus.setTotalAmount(prePaidCardBonusAmount);
            statementPaymentResult.add(3, prepaidCardBonus);
            break;
          default:
            statementPaymentResult.add(payment);
            break;
        }
      }
    }
  }

  /**
   * 构建对账单账单收费明细导出列表
   *
   * @param resultList 账单收费明细列表
   * @return List<StatementBillChargeDetailExportVO>
   */
  private List<StatementBillChargeDetailExportVO> buildStatementBillChargeDetailExportList(
      List<StatementBillChargeDetailVO> resultList) {
    ArrayList<StatementBillChargeDetailExportVO> chargeDetailExports = new ArrayList<>();
    if (StringHelper.isNotEmpty(resultList)) {
      for (StatementBillChargeDetailVO vo : resultList) {
        StatementBillChargeDetailExportVO chargeDetailExport =
            new StatementBillChargeDetailExportVO();
        BeanUtils.copyProperties(vo, chargeDetailExport);
        List<StatementPaymentVO> payments = vo.getStatementPayments();
        if (StringHelper.isNotEmpty(payments)) {
          for (StatementPaymentVO payment : payments) {
            // todo 当前硬编码全部支付方式，后期再考虑根据现有支付方式动态设置
            String accountItemName = payment.getAccountItemName();
            BigDecimal totalAmount = payment.getTotalAmount();
            switch (accountItemName) {
              case "会员卡本金":
                chargeDetailExport.setMemberPrincipleAmount(totalAmount);
                break;
              case "会员卡赠金":
                chargeDetailExport.setMemberBonusAmount(totalAmount);
                break;
              case "预付款本金":
                chargeDetailExport.setPrepaidPrincipleAmount(totalAmount);
                break;
              case "预付款赠金":
                chargeDetailExport.setPrepaidBonusAmount(totalAmount);
                break;
              case "现金":
                chargeDetailExport.setCashAmount(totalAmount);
                break;
              case "支付宝":
                chargeDetailExport.setAliPayAmount(totalAmount);
                break;
              case "微信":
                chargeDetailExport.setWeChatAmount(totalAmount);
                break;
              case "银行账户":
                chargeDetailExport.setBankAmount(totalAmount);
                break;
              case "浙江省医保":
                chargeDetailExport.setZheJiangProvinceMedicalInsuranceAmount(totalAmount);
                break;
              case "杭州市医保":
                chargeDetailExport.setHangZhouCityMedicalInsuranceAmount(totalAmount);
                break;
              case "杭州市余杭医保":
                chargeDetailExport.setHangZhouYuHangMedicalInsuranceAmount(totalAmount);
                break;
              case "杭州市萧山医保":
                chargeDetailExport.setHangZhouXiaoShanMedicalInsuranceAmount(totalAmount);
                break;
              case "Cigna":
                chargeDetailExport.setCignaAmount(totalAmount);
                break;
              case "MSH":
                chargeDetailExport.setMshAmount(totalAmount);
                break;
              case "AXA":
                chargeDetailExport.setAxaAmount(totalAmount);
                break;
              case "风石":
                chargeDetailExport.setFengShiAmount(totalAmount);
                break;
              case "本次免单支付":
                chargeDetailExport.setThisWaiverAmount(totalAmount);
                break;
              case "艾维员工免单":
                chargeDetailExport.setEmployeeWaiverAmount(totalAmount);
                break;
              default:
                break;
            }
          }
        }
        chargeDetailExports.add(chargeDetailExport);
      }
    }
    return chargeDetailExports;
  }
}
