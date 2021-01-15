package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

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

  /** 收费记录明细 */
  @Autowired private BaseBillPayDetailMapper billPayDetailMapper;
  /** 组织 */
  @Autowired private BaseOrganizationMapper organizationMapper;

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
        vo.setStatementPayments(statementPaymentResult);
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
