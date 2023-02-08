package com.yunya.report.ultimate.biz;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.query.InboundAndOutboundStatementQuery;
import com.yunya.feign.report.domain.vo.BaseAccountItemVO;
import com.yunya.feign.report.domain.vo.ClinicInboundAndOutboundVO;
import com.yunya.feign.report.domain.vo.StatementPaymentVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.enums.PatientDepositAccountTypeEnum;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BaseAccountItem;
import com.yunya.models.report.BaseOrganization;
import com.yunya.report.ultimate.mapper.BaseAccountItemMapper;
import com.yunya.report.ultimate.mapper.BaseOrganizationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.yunya.framework.common.constant.BusinessConstants.ACCOUNT_ITEM_OF_MEMBER;
import static com.yunya.framework.common.constant.BusinessConstants.ACCOUNT_ITEM_OF_PREPARE;

/**
 * 简介: 支付方式业务层
 *
 * @author: chow
 * @date: 2020/12/11 10:24
 * @description:
 * @since: 1.0.0
 */
@Service
public class BaseAccountItemBiz extends BaseBiz<BaseAccountItemMapper, BaseAccountItem> {

  /** 组织信息*/
  @Autowired
  private BaseOrganizationMapper organizationMapper;

  /**
   * 获取全部支付方式表头
   *
   * @return List<BaseAccountItemVO>
   */
  public List<BaseAccountItemVO> findAllPaymentList() {
    List<BaseAccountItemVO> resultList = mapper.selectAllPaymentList();
    if (StringHelper.isNotEmpty(resultList)) {
      List<BaseAccountItemVO> depositAccounts = new ArrayList<>();
      Iterator<BaseAccountItemVO> it = resultList.iterator();
      while (it.hasNext()) {
        BaseAccountItemVO vo = it.next();
        Integer accountItemId = vo.getAccountItemId();
        PatientDepositAccountTypeEnum typeEnum = PatientDepositAccountTypeEnum.getTypeEnumRelId(accountItemId);
        if (StringHelper.isNotNull(typeEnum)) {
          it.remove();
          BaseAccountItemVO principal = new BaseAccountItemVO();
          principal.setAccountItemId(accountItemId);
          principal.setAccountItemName(typeEnum.getName() + "本金");
          BaseAccountItemVO bouns = new BaseAccountItemVO();
          bouns.setAccountItemId(accountItemId);
          bouns.setAccountItemName(typeEnum.getName() + "赠金");
          depositAccounts.add(principal);
          depositAccounts.add(bouns);
        }
      }
      resultList.addAll(0, depositAccounts);
    }
    return resultList;
  }

  /**
   * 根据条件查询门诊对账单
   *
   * @param query 查询条件
   * @return ClinicInboundAndOutboundVO
   */
  public List<ClinicInboundAndOutboundVO> findInboundAndOutboundStatement(
      InboundAndOutboundStatementQuery query) {
    List<ClinicInboundAndOutboundVO> resultList = Lists.newArrayList();
    // 门诊账单收费(本月)-- 查询时间段内本门诊账单首次收费
    billChargeThisMonth(query, resultList);

    // 门诊收欠费（本期）-- 查询时间段内本门诊账单非首次收费
    collectArrearsThisMonth(query, resultList);

    // 门诊收欠费（非本期）-- 非查询时间段内本门诊账单的非首次收费
    collectArrearsNotThisMonth(query, resultList);

    // 门诊会员充值 -- 查询时间段内本门诊会员卡充值
    memberCharge(query, resultList);

    // 门诊预付款充值 -- 查询时间段内本门诊预付款充值
    prePaidCharge(query, resultList);

    // 产品售出 -- 查询时间段内本门诊产品售出
    productSold(query, resultList);

    // 诊所代收（本期）-- 查询时间段内非本门诊账单在本门诊收费
    clinicCollectionThisMonth(query, resultList);

    // 诊所代收（非本期）-- 非查询时间段内非本门诊账单在本门诊收费
    clinicCollectionNotThisMonth(query, resultList);

    // 账单退费（本期）-- 查询时间段内本门诊账单退费
    billRefundThisMonth(query, resultList);

    // 账单退费（非本期）-- 非查询时间段内本门诊账单退费
    billRefundNotThisMonth(query, resultList);

    // 会员卡退费 -- 查询时间段内本门诊会员卡充值退费
    memberRefund(query, resultList);

    // 预付款退费 -- 查询时间段内本门诊预付款充值退费
    prepaidRefund(query, resultList);

    // 计算出入账合计
    calculateInboundAndOutbound(resultList);

    // 诊所被代收（本期）-- 查询时间段内本门诊账单不在本门诊收费
    clinicIsAcceptedThisMonth(query, resultList);

    // 诊所被代收（非本期）-- 非查询时间段内本门诊账单不在门诊收费
    clinicIsAcceptedNotThisMonth(query, resultList);

    return resultList;
  }

  /**
   * 计算出入账合计
   *
   * @param resultList 结果集
   */
  private void calculateInboundAndOutbound(List<ClinicInboundAndOutboundVO> resultList) {
    List<StatementPaymentVO> inboundPayments = new ArrayList<>();
    List<StatementPaymentVO> outboundPayments = new ArrayList<>();
    int bound = resultList.size();
    for (int i = 0; i < bound; i++) {
      if (i <= 7) {
        List<StatementPaymentVO> paymentInfoList = resultList.get(i).getPaymentInfoList();
        inboundPayments.addAll(paymentInfoList);
      } else if (i <= 11) {
        List<StatementPaymentVO> paymentInfoList = resultList.get(i).getPaymentInfoList();
        outboundPayments.addAll(paymentInfoList);
      }
    }
    // 入账支付信息分组求和
    List<StatementPaymentVO> inboundPaymentResult =
        groupAndCalculateStatementPayment(inboundPayments);
    // 出账支付方式分组求和¬
    List<StatementPaymentVO> outboundPaymentResult =
        groupAndCalculateStatementPayment(outboundPayments);

    for (StatementPaymentVO in : inboundPaymentResult) {
      for (StatementPaymentVO out : outboundPaymentResult) {
        if (in.getAccountItemName().equals(out.getAccountItemName())) {
          BigDecimal totalAmount = in.getTotalAmount().subtract(out.getTotalAmount());
          in.setTotalAmount(totalAmount);
        }
      }
    }

    ClinicInboundAndOutboundVO inboundAndOutboundVO = new ClinicInboundAndOutboundVO();
    inboundAndOutboundVO.setType((byte) 12);
    inboundAndOutboundVO.setName("合计");
    inboundAndOutboundVO.setPaymentInfoList(inboundPaymentResult);
    resultList.add(12, inboundAndOutboundVO);
  }

  /**
   * 对入账方式分组求和
   *
   * @param payments 支付方式列表
   * @return List<StatementPaymentVO>
   */
  private List<StatementPaymentVO> groupAndCalculateStatementPayment(
      List<StatementPaymentVO> payments) {
    List<StatementPaymentVO> paymentResult = new ArrayList<>();
    if (StringHelper.isNotEmpty(payments)) {
      // 数据分组统计处理
      payments.parallelStream()
          .collect(
              Collectors.groupingBy(StatementPaymentVO::getAccountItemName, Collectors.toList()))
          .forEach(
              (i, transfer) ->
                  transfer.stream()
                      .reduce(
                          (a, b) ->
                              new StatementPaymentVO(
                                  a.getAccountItemId(),
                                  a.getAccountItemName(),
                                  a.getTotalAmount() == null
                                      ? BigDecimal.ZERO
                                      : a.getTotalAmount()
                                          .add(
                                              b.getTotalAmount() == null
                                                  ? BigDecimal.ZERO
                                                  : b.getTotalAmount()),
                                  a.getBonusAmount()))
                      .ifPresent(paymentResult::add));
    }
    return paymentResult;
  }

  /**
   * 诊所被代收（非本期）
   *
   * @param query 查询条件
   * @param resultList 结果集
   */
  private void clinicIsAcceptedNotThisMonth(
      InboundAndOutboundStatementQuery query, List<ClinicInboundAndOutboundVO> resultList) {
    List<StatementPaymentVO> clinicIsAcceptedNotThisMonthResult =
        findClinicIsAcceptedPaymentInfoNotThisMonth(query);
    List<BaseAccountItemVO> paymentList = mapper.selectAllPaymentList();
    List<StatementPaymentVO> clinicIsAcceptedNotThisMonth = new ArrayList<>();
    if (StringHelper.isNotEmpty(paymentList)) {
      for (BaseAccountItemVO accountItem : paymentList) {
        StatementPaymentVO vo = new StatementPaymentVO();
        Integer accountItemId = accountItem.getAccountItemId();
        vo.setAccountItemId(accountItemId);
        vo.setAccountItemName(accountItem.getAccountItemName());
        vo.setTotalAmount(BigDecimal.ZERO);
        vo.setBonusAmount(BigDecimal.ZERO);
        for (StatementPaymentVO statementPayment : clinicIsAcceptedNotThisMonthResult) {
          if (accountItemId.equals(statementPayment.getAccountItemId())) {
            vo.setTotalAmount(statementPayment.getTotalAmount());
            vo.setBonusAmount(statementPayment.getBonusAmount());
          }
        }
        clinicIsAcceptedNotThisMonth.add(vo);
      }
    }
    clinicIsAcceptedNotThisMonth = reBuildStatementsPaymentList(clinicIsAcceptedNotThisMonth);
    ClinicInboundAndOutboundVO clinicIsAcceptedNotThisMonthVO = new ClinicInboundAndOutboundVO();
    clinicIsAcceptedNotThisMonthVO.setType((byte) 14);
    clinicIsAcceptedNotThisMonthVO.setName("诊所被代收（非本期）");
    clinicIsAcceptedNotThisMonthVO.setPaymentInfoList(clinicIsAcceptedNotThisMonth);
    resultList.add(14, clinicIsAcceptedNotThisMonthVO);
  }

  /**
   * 诊所被代收（本期）
   *
   * @param query 查询条件
   * @param resultList 结果集
   */
  private void clinicIsAcceptedThisMonth(
      InboundAndOutboundStatementQuery query, List<ClinicInboundAndOutboundVO> resultList) {
    List<StatementPaymentVO> clinicIsAcceptedThisMonthResult =
        findClinicIsAcceptedPaymentInfoThisMonth(query);
    List<BaseAccountItemVO> paymentList = mapper.selectAllPaymentList();
    List<StatementPaymentVO> clinicIsAcceptedThisMonth = new ArrayList<>();
    if (StringHelper.isNotEmpty(paymentList)) {
      for (BaseAccountItemVO accountItem : paymentList) {
        StatementPaymentVO vo = new StatementPaymentVO();
        Integer accountItemId = accountItem.getAccountItemId();
        vo.setAccountItemId(accountItemId);
        vo.setAccountItemName(accountItem.getAccountItemName());
        vo.setTotalAmount(BigDecimal.ZERO);
        vo.setBonusAmount(BigDecimal.ZERO);
        for (StatementPaymentVO statementPayment : clinicIsAcceptedThisMonthResult) {
          if (accountItemId.equals(statementPayment.getAccountItemId())) {
            vo.setTotalAmount(statementPayment.getTotalAmount());
            vo.setBonusAmount(statementPayment.getBonusAmount());
          }
        }
        clinicIsAcceptedThisMonth.add(vo);
      }
    }
    clinicIsAcceptedThisMonth = reBuildStatementsPaymentList(clinicIsAcceptedThisMonth);
    ClinicInboundAndOutboundVO clinicIsAcceptedThisMonthVO = new ClinicInboundAndOutboundVO();
    clinicIsAcceptedThisMonthVO.setType((byte) 13);
    clinicIsAcceptedThisMonthVO.setName("诊所被代收（本期）");
    clinicIsAcceptedThisMonthVO.setPaymentInfoList(clinicIsAcceptedThisMonth);
    resultList.add(13, clinicIsAcceptedThisMonthVO);
  }

  /**
   * 预付款退费
   *
   * @param query 查询条件
   * @param resultList 结果集
   */
  private void prepaidRefund(
      InboundAndOutboundStatementQuery query, List<ClinicInboundAndOutboundVO> resultList) {
    List<StatementPaymentVO> prepaidRefundResult = findPrepaidRefundPaymentInfo(query);
    List<BaseAccountItemVO> paymentList = mapper.selectAllPaymentList();
    List<StatementPaymentVO> prepaidRefund = new ArrayList<>();
    if (StringHelper.isNotEmpty(paymentList)) {
      for (BaseAccountItemVO accountItem : paymentList) {
        StatementPaymentVO vo = new StatementPaymentVO();
        Integer accountItemId = accountItem.getAccountItemId();
        vo.setAccountItemId(accountItemId);
        vo.setAccountItemName(accountItem.getAccountItemName());
        vo.setTotalAmount(BigDecimal.ZERO);
        vo.setBonusAmount(BigDecimal.ZERO);
        for (StatementPaymentVO statementPayment : prepaidRefundResult) {
          if (accountItemId.equals(statementPayment.getAccountItemId())) {
            vo.setTotalAmount(statementPayment.getTotalAmount());
            vo.setBonusAmount(statementPayment.getBonusAmount());
          }
        }
        prepaidRefund.add(vo);
      }
    }
    prepaidRefund = reBuildStatementsPaymentList(prepaidRefund);
    ClinicInboundAndOutboundVO prepaidRefundVO = new ClinicInboundAndOutboundVO();
    prepaidRefundVO.setType((byte) 11);
    prepaidRefundVO.setName("预付款退费");
    prepaidRefundVO.setPaymentInfoList(prepaidRefund);
    resultList.add(11, prepaidRefundVO);
  }

  /**
   * 会员卡退费
   *
   * @param query 查询条件
   * @param resultList 结果集
   */
  private void memberRefund(
      InboundAndOutboundStatementQuery query, List<ClinicInboundAndOutboundVO> resultList) {
    List<StatementPaymentVO> memberRefundResult = findMemberRefundPaymentInfo(query);
    List<BaseAccountItemVO> paymentList = mapper.selectAllPaymentList();
    List<StatementPaymentVO> memberRefund = new ArrayList<>();
    if (StringHelper.isNotEmpty(paymentList)) {
      for (BaseAccountItemVO accountItem : paymentList) {
        StatementPaymentVO vo = new StatementPaymentVO();
        Integer accountItemId = accountItem.getAccountItemId();
        vo.setAccountItemId(accountItemId);
        vo.setAccountItemName(accountItem.getAccountItemName());
        vo.setTotalAmount(BigDecimal.ZERO);
        vo.setBonusAmount(BigDecimal.ZERO);
        for (StatementPaymentVO statementPayment : memberRefundResult) {
          if (accountItemId.equals(statementPayment.getAccountItemId())) {
            vo.setTotalAmount(statementPayment.getTotalAmount());
            vo.setBonusAmount(statementPayment.getBonusAmount());
          }
        }
        memberRefund.add(vo);
      }
    }
    memberRefund = reBuildStatementsPaymentList(memberRefund);
    ClinicInboundAndOutboundVO memberRefundVO = new ClinicInboundAndOutboundVO();
    memberRefundVO.setType((byte) 10);
    memberRefundVO.setName("会员卡退费");
    memberRefundVO.setPaymentInfoList(memberRefund);
    resultList.add(10, memberRefundVO);
  }

  /**
   * 账单退费（非本期）
   *
   * @param query 查询条件
   * @param resultList 结果集
   */
  private void billRefundNotThisMonth(
      InboundAndOutboundStatementQuery query, List<ClinicInboundAndOutboundVO> resultList) {
    List<StatementPaymentVO> billRefundNotThisMonthResult =
        findBillRefundPaymentInfoNotThisMonth(query);
    List<BaseAccountItemVO> paymentList = mapper.selectAllPaymentList();
    List<StatementPaymentVO> billRefundNotThisMonth = new ArrayList<>();
    if (StringHelper.isNotEmpty(paymentList)) {
      for (BaseAccountItemVO accountItem : paymentList) {
        StatementPaymentVO vo = new StatementPaymentVO();
        Integer accountItemId = accountItem.getAccountItemId();
        vo.setAccountItemId(accountItemId);
        vo.setAccountItemName(accountItem.getAccountItemName());
        vo.setTotalAmount(BigDecimal.ZERO);
        vo.setBonusAmount(BigDecimal.ZERO);
        for (StatementPaymentVO statementPayment : billRefundNotThisMonthResult) {
          if (accountItemId.equals(statementPayment.getAccountItemId())) {
            vo.setTotalAmount(statementPayment.getTotalAmount());
            vo.setBonusAmount(statementPayment.getBonusAmount());
          }
        }
        billRefundNotThisMonth.add(vo);
      }
    }
    billRefundNotThisMonth = reBuildStatementsPaymentList(billRefundNotThisMonth);
    ClinicInboundAndOutboundVO billRefundNotThisMonthVO = new ClinicInboundAndOutboundVO();
    billRefundNotThisMonthVO.setType((byte) 9);
    billRefundNotThisMonthVO.setName("账单退费（非本期）");
    billRefundNotThisMonthVO.setPaymentInfoList(billRefundNotThisMonth);
    resultList.add(9, billRefundNotThisMonthVO);
  }

  /**
   * 账单退费（本期）
   *
   * @param query 查询条件
   * @param resultList 结果集
   */
  private void billRefundThisMonth(
      InboundAndOutboundStatementQuery query, List<ClinicInboundAndOutboundVO> resultList) {
    List<StatementPaymentVO> billRefundThisMonthResult = findBillRefundPaymentInfoThisMonth(query);
    List<BaseAccountItemVO> paymentList = mapper.selectAllPaymentList();
    List<StatementPaymentVO> billRefundThisMonth = new ArrayList<>();
    if (StringHelper.isNotEmpty(paymentList)) {
      for (BaseAccountItemVO accountItem : paymentList) {
        StatementPaymentVO vo = new StatementPaymentVO();
        Integer accountItemId = accountItem.getAccountItemId();
        vo.setAccountItemId(accountItemId);
        vo.setAccountItemName(accountItem.getAccountItemName());
        vo.setTotalAmount(BigDecimal.ZERO);
        vo.setBonusAmount(BigDecimal.ZERO);
        for (StatementPaymentVO statementPayment : billRefundThisMonthResult) {
          if (accountItemId.equals(statementPayment.getAccountItemId())) {
            vo.setTotalAmount(statementPayment.getTotalAmount());
            vo.setBonusAmount(statementPayment.getBonusAmount());
          }
        }
        billRefundThisMonth.add(vo);
      }
    }
    billRefundThisMonth = reBuildStatementsPaymentList(billRefundThisMonth);
    ClinicInboundAndOutboundVO billRefundThisMonthVO = new ClinicInboundAndOutboundVO();
    billRefundThisMonthVO.setType((byte) 8);
    billRefundThisMonthVO.setName("账单退费（本期）");
    billRefundThisMonthVO.setPaymentInfoList(billRefundThisMonth);
    resultList.add(8, billRefundThisMonthVO);
  }

  /**
   * 诊所代收（非本期）
   *
   * @param query 查询条件
   * @param resultList 结果集
   */
  private void clinicCollectionNotThisMonth(
      InboundAndOutboundStatementQuery query, List<ClinicInboundAndOutboundVO> resultList) {
    List<StatementPaymentVO> clinicCollectionNotThisMonthResult =
        findClinicCollectionPaymentInfoNotThisMonth(query);
    List<BaseAccountItemVO> paymentList = mapper.selectAllPaymentList();
    List<StatementPaymentVO> clinicCollectionNotThisMonth = new ArrayList<>();
    if (StringHelper.isNotEmpty(paymentList)) {
      for (BaseAccountItemVO accountItem : paymentList) {
        StatementPaymentVO vo = new StatementPaymentVO();
        Integer accountItemId = accountItem.getAccountItemId();
        vo.setAccountItemId(accountItemId);
        vo.setAccountItemName(accountItem.getAccountItemName());
        vo.setTotalAmount(BigDecimal.ZERO);
        vo.setBonusAmount(BigDecimal.ZERO);
        for (StatementPaymentVO statementPayment : clinicCollectionNotThisMonthResult) {
          if (accountItemId.equals(statementPayment.getAccountItemId())) {
            vo.setTotalAmount(statementPayment.getTotalAmount());
            vo.setBonusAmount(statementPayment.getBonusAmount());
          }
        }
        clinicCollectionNotThisMonth.add(vo);
      }
    }
    clinicCollectionNotThisMonth = reBuildStatementsPaymentList(clinicCollectionNotThisMonth);
    ClinicInboundAndOutboundVO clinicCollectionNotThisMonthVO = new ClinicInboundAndOutboundVO();
    clinicCollectionNotThisMonthVO.setType((byte) 7);
    clinicCollectionNotThisMonthVO.setName("诊所代收（非本期）");
    clinicCollectionNotThisMonthVO.setPaymentInfoList(clinicCollectionNotThisMonth);
    resultList.add(7, clinicCollectionNotThisMonthVO);
  }

  /**
   * 诊所代收（本期）
   *
   * @param query 查询条件
   * @param resultList 结果集
   */
  private void clinicCollectionThisMonth(
      InboundAndOutboundStatementQuery query, List<ClinicInboundAndOutboundVO> resultList) {
    List<StatementPaymentVO> clinicCollectionThisMonthResult =
        findClinicCollectionPaymentInfoThisMonth(query);
    List<BaseAccountItemVO> paymentList = mapper.selectAllPaymentList();
    List<StatementPaymentVO> clinicCollectionThisMonth = new ArrayList<>();
    if (StringHelper.isNotEmpty(paymentList)) {
      for (BaseAccountItemVO accountItem : paymentList) {
        StatementPaymentVO vo = new StatementPaymentVO();
        Integer accountItemId = accountItem.getAccountItemId();
        vo.setAccountItemId(accountItemId);
        vo.setAccountItemName(accountItem.getAccountItemName());
        vo.setTotalAmount(BigDecimal.ZERO);
        vo.setBonusAmount(BigDecimal.ZERO);
        for (StatementPaymentVO statementPayment : clinicCollectionThisMonthResult) {
          if (accountItemId.equals(statementPayment.getAccountItemId())) {
            vo.setTotalAmount(statementPayment.getTotalAmount());
            vo.setBonusAmount(statementPayment.getBonusAmount());
          }
        }
        clinicCollectionThisMonth.add(vo);
      }
    }
    clinicCollectionThisMonth = reBuildStatementsPaymentList(clinicCollectionThisMonth);
    ClinicInboundAndOutboundVO clinicCollectionThisMonthVO = new ClinicInboundAndOutboundVO();
    clinicCollectionThisMonthVO.setType((byte) 6);
    clinicCollectionThisMonthVO.setName("诊所代收（本期）");
    clinicCollectionThisMonthVO.setPaymentInfoList(clinicCollectionThisMonth);
    resultList.add(6, clinicCollectionThisMonthVO);
  }

  /**
   * 产品售出
   *
   * @param query 查询条件
   * @param resultList 结果集
   */
  private void productSold(
      InboundAndOutboundStatementQuery query, List<ClinicInboundAndOutboundVO> resultList) {
    List<StatementPaymentVO> productSoldResult = findProductSoldPaymentInfo(query);
    List<BaseAccountItemVO> paymentList = mapper.selectAllPaymentList();
    List<StatementPaymentVO> productSold = new ArrayList<>();
    if (StringHelper.isNotEmpty(paymentList)) {
      for (BaseAccountItemVO accountItem : paymentList) {
        StatementPaymentVO vo = new StatementPaymentVO();
        Integer accountItemId = accountItem.getAccountItemId();
        vo.setAccountItemId(accountItemId);
        vo.setAccountItemName(accountItem.getAccountItemName());
        vo.setTotalAmount(BigDecimal.ZERO);
        vo.setBonusAmount(BigDecimal.ZERO);
        for (StatementPaymentVO statementPayment : productSoldResult) {
          if (accountItemId.equals(statementPayment.getAccountItemId())) {
            vo.setTotalAmount(statementPayment.getTotalAmount());
            vo.setBonusAmount(statementPayment.getBonusAmount());
          }
        }
        productSold.add(vo);
      }
    }
    productSold = reBuildStatementsPaymentList(productSold);
    ClinicInboundAndOutboundVO productSoldVO = new ClinicInboundAndOutboundVO();
    productSoldVO.setType((byte) 5);
    productSoldVO.setName("产品售出");
    productSoldVO.setPaymentInfoList(productSold);
    resultList.add(5, productSoldVO);
  }

  /**
   * 门诊预付款充值
   *
   * @param query 查询条件
   * @param resultList 结果集
   */
  private void prePaidCharge(
      InboundAndOutboundStatementQuery query, List<ClinicInboundAndOutboundVO> resultList) {
    List<PatientDepositAccountTypeEnum> typeEnums = PatientDepositAccountTypeEnum.prepaymentValues();
    for (int i = 0; i < typeEnums.size(); i++) {
      // 预付款 + 专项预付款
      prePaidCharge(query, typeEnums.get(i), i+4, resultList);
    }
  }

    private void prePaidCharge(
            InboundAndOutboundStatementQuery query, PatientDepositAccountTypeEnum typeEnum, Integer index, List<ClinicInboundAndOutboundVO> resultList) {
    List<StatementPaymentVO> prePaidChargeResult = findPrePaidChargePaymentInfo(query, typeEnum.getType());
    List<BaseAccountItemVO> paymentList = mapper.selectAllPaymentList();
    List<StatementPaymentVO> prePaidCharge = new ArrayList<>();
    if (StringHelper.isNotEmpty(paymentList)) {
      for (BaseAccountItemVO accountItem : paymentList) {
        StatementPaymentVO vo = new StatementPaymentVO();
        Integer accountItemId = accountItem.getAccountItemId();
        vo.setAccountItemId(accountItemId);
        vo.setAccountItemName(accountItem.getAccountItemName());
        vo.setTotalAmount(BigDecimal.ZERO);
        vo.setBonusAmount(BigDecimal.ZERO);
        for (StatementPaymentVO statementPayment : prePaidChargeResult) {
          if (accountItemId.equals(statementPayment.getAccountItemId())) {
            vo.setTotalAmount(statementPayment.getTotalAmount());
            vo.setBonusAmount(statementPayment.getBonusAmount());
          }
        }
        prePaidCharge.add(vo);
      }
    }
    prePaidCharge = reBuildStatementsPaymentList(prePaidCharge);
    ClinicInboundAndOutboundVO prePaidChargeVO = new ClinicInboundAndOutboundVO();
    prePaidChargeVO.setType((byte) index.intValue());
    prePaidChargeVO.setName(typeEnum.getName() + "充值");
    prePaidChargeVO.setPaymentInfoList(prePaidCharge);
    resultList.add(index, prePaidChargeVO);
  }

  /**
   * 门诊会员充值
   *
   * @param query 查询条件
   * @param resultList 结果集
   */
  private void memberCharge(
      InboundAndOutboundStatementQuery query, List<ClinicInboundAndOutboundVO> resultList) {
    List<StatementPaymentVO> memberChargeResult = findMemberChargePaymentInfo(query);
    List<BaseAccountItemVO> paymentList = mapper.selectAllPaymentList();
    List<StatementPaymentVO> memberCharge = new ArrayList<>();
    if (StringHelper.isNotEmpty(paymentList)) {
      for (BaseAccountItemVO accountItem : paymentList) {
        StatementPaymentVO vo = new StatementPaymentVO();
        Integer accountItemId = accountItem.getAccountItemId();
        vo.setAccountItemId(accountItemId);
        vo.setAccountItemName(accountItem.getAccountItemName());
        vo.setTotalAmount(BigDecimal.ZERO);
        vo.setBonusAmount(BigDecimal.ZERO);
        for (StatementPaymentVO statementPayment : memberChargeResult) {
          if (accountItemId.equals(statementPayment.getAccountItemId())) {
            vo.setTotalAmount(statementPayment.getTotalAmount());
            vo.setBonusAmount(statementPayment.getBonusAmount());
          }
        }
        memberCharge.add(vo);
      }
    }
    memberCharge = reBuildStatementsPaymentList(memberCharge);
    ClinicInboundAndOutboundVO memberChargeVO = new ClinicInboundAndOutboundVO();
    memberChargeVO.setType((byte) 3);
    memberChargeVO.setName("会员充值");
    memberChargeVO.setPaymentInfoList(memberCharge);
    resultList.add(3, memberChargeVO);
  }

  /**
   * 门诊收欠费（非本期）
   *
   * @param query 查询条件
   * @param resultList 结果集
   */
  private void collectArrearsNotThisMonth(
      InboundAndOutboundStatementQuery query, List<ClinicInboundAndOutboundVO> resultList) {
    List<StatementPaymentVO> collectArrearsNotThisMonthResult =
        findCollectArrearsPaymentInfoNotThisMonth(query);
    List<BaseAccountItemVO> paymentList = mapper.selectAllPaymentList();
    List<StatementPaymentVO> collectArrearsNotThisMonth = new ArrayList<>();
    if (StringHelper.isNotEmpty(paymentList)) {
      for (BaseAccountItemVO accountItem : paymentList) {
        StatementPaymentVO vo = new StatementPaymentVO();
        Integer accountItemId = accountItem.getAccountItemId();
        vo.setAccountItemId(accountItemId);
        vo.setAccountItemName(accountItem.getAccountItemName());
        vo.setTotalAmount(BigDecimal.ZERO);
        vo.setBonusAmount(BigDecimal.ZERO);
        for (StatementPaymentVO statementPayment : collectArrearsNotThisMonthResult) {
          if (accountItemId.equals(statementPayment.getAccountItemId())) {
            vo.setTotalAmount(statementPayment.getTotalAmount());
            vo.setBonusAmount(statementPayment.getBonusAmount());
          }
        }
        collectArrearsNotThisMonth.add(vo);
      }
    }
    collectArrearsNotThisMonth = reBuildStatementsPaymentList(collectArrearsNotThisMonth);
    ClinicInboundAndOutboundVO collectArrearsNotThisMonthVO = new ClinicInboundAndOutboundVO();
    collectArrearsNotThisMonthVO.setType((byte) 2);
    collectArrearsNotThisMonthVO.setName("收欠费（非本期）");
    collectArrearsNotThisMonthVO.setPaymentInfoList(collectArrearsNotThisMonth);
    resultList.add(2, collectArrearsNotThisMonthVO);
  }

  /**
   * 门诊收欠费（本期）
   *
   * @param query 查询条件
   * @param resultList 结果集
   */
  private void collectArrearsThisMonth(
      InboundAndOutboundStatementQuery query, List<ClinicInboundAndOutboundVO> resultList) {
    List<StatementPaymentVO> collectArrearsThisMonthResult =
        findCollectArrearsPaymentInfoThisMonth(query);
    List<BaseAccountItemVO> paymentList = mapper.selectAllPaymentList();
    List<StatementPaymentVO> collectArrearsThisMonth = new ArrayList<>();
    if (StringHelper.isNotEmpty(paymentList)) {
      for (BaseAccountItemVO accountItem : paymentList) {
        StatementPaymentVO vo = new StatementPaymentVO();
        Integer accountItemId = accountItem.getAccountItemId();
        vo.setAccountItemId(accountItemId);
        vo.setAccountItemName(accountItem.getAccountItemName());
        vo.setTotalAmount(BigDecimal.ZERO);
        vo.setBonusAmount(BigDecimal.ZERO);
        for (StatementPaymentVO statementPayment : collectArrearsThisMonthResult) {
          if (accountItemId.equals(statementPayment.getAccountItemId())) {
            vo.setTotalAmount(statementPayment.getTotalAmount());
            vo.setBonusAmount(statementPayment.getBonusAmount());
          }
        }
        collectArrearsThisMonth.add(vo);
      }
    }
    collectArrearsThisMonth = reBuildStatementsPaymentList(collectArrearsThisMonth);
    ClinicInboundAndOutboundVO collectArrearsOfThisMonthVO = new ClinicInboundAndOutboundVO();
    collectArrearsOfThisMonthVO.setType((byte) 1);
    collectArrearsOfThisMonthVO.setName("收欠费（本期）");
    collectArrearsOfThisMonthVO.setPaymentInfoList(collectArrearsThisMonth);
    resultList.add(1, collectArrearsOfThisMonthVO);
  }

  /**
   * 门诊账单收费
   *
   * @param query 查询条件
   * @param resultList 结果集
   */
  public void billChargeThisMonth(
      InboundAndOutboundStatementQuery query, List<ClinicInboundAndOutboundVO> resultList) {
    List<StatementPaymentVO> billChargeThisMonthResult = findBillChargePaymentInfoThisMonth(query);
    List<BaseAccountItemVO> paymentList = mapper.selectAllPaymentList();
    List<StatementPaymentVO> billChargeThisMonth = new ArrayList<>();
    if (StringHelper.isNotEmpty(paymentList)) {
      for (BaseAccountItemVO accountItem : paymentList) {
        StatementPaymentVO vo = new StatementPaymentVO();
        Integer accountItemId = accountItem.getAccountItemId();
        vo.setAccountItemId(accountItemId);
        String accountItemName = accountItem.getAccountItemName();
        vo.setAccountItemName(accountItemName);
        vo.setTotalAmount(BigDecimal.ZERO);
        vo.setBonusAmount(BigDecimal.ZERO);
        for (StatementPaymentVO statementPayment : billChargeThisMonthResult) {
          if (accountItemId.equals(statementPayment.getAccountItemId())) {
            vo.setTotalAmount(statementPayment.getTotalAmount());
            vo.setBonusAmount(statementPayment.getBonusAmount());
          }
        }
        billChargeThisMonth.add(vo);
      }
    }
    billChargeThisMonth = reBuildStatementsPaymentList(billChargeThisMonth);
    ClinicInboundAndOutboundVO billChargeVO = new ClinicInboundAndOutboundVO();
    billChargeVO.setType((byte) 0);
    billChargeVO.setName("账单收费（本期）");
    billChargeVO.setPaymentInfoList(billChargeThisMonth);
    resultList.add(0, billChargeVO);
  }

  /**
   * 重构支付方式汇总列表
   *
   * @param type "收支明细分类:0-账单收费（本期）；1-收欠费（本期）；2-收欠费（非本期）；3-会员充值；4-预付款充值；5-正畸预付款；6-美白预付款；7-产品售出；"
   *     "8-诊所代收（本期）；9-诊所代收（非本期）；10-账单退费（本期）；11-账单退费（非本期）；"
   *     "12-会员卡退费；13-预付款退费；14-诊所被代收账（本期）；15-诊所被代收帐（非本期）"
   * @param list 支付方式列表
   * @param query 会员卡/预付款本金赠金查询参数
   */
  private void reBuildStatementsPaymentList(
      Byte type, List<StatementPaymentVO> list, InboundAndOutboundStatementQuery query) {
    setPaymentListValue(list, type, query);
  }

  /**
   * 重构支付方式列表
   *
   * @param statementPayments 支付方式列表
   */
  private List<StatementPaymentVO> reBuildStatementsPaymentList(
      List<StatementPaymentVO> statementPayments) {
    List<StatementPaymentVO> statementPaymentResult = new ArrayList<>();
    BaseBillPayBiz.setStatementPaymentValue(statementPaymentResult, statementPayments);
    return statementPaymentResult;
  }

  /**
   * 设置账单收费支付方式列表信息
   *
   * @param list 支付方式列表
   * @param type 收支类型
   * @param query 会员卡/预付款本金赠金查询参数
   */
  private void setPaymentListValue(
      List<StatementPaymentVO> list, Byte type, InboundAndOutboundStatementQuery query) {
    if (StringHelper.isNotEmpty(list)) {
      Integer[] memberAccountItem = new Integer[1];
      Integer[] prePaymentAccountItem = new Integer[1];
      for (StatementPaymentVO statementPaymentVO : list) {
        String accountItemName = statementPaymentVO.getAccountItemName();
        if (ACCOUNT_ITEM_OF_MEMBER.equals(accountItemName)) {
          memberAccountItem[0] = statementPaymentVO.getAccountItemId();
        }
        if (ACCOUNT_ITEM_OF_PREPARE.equals(accountItemName)) {
          prePaymentAccountItem[0] = statementPaymentVO.getAccountItemId();
        }
      }
      Iterator<StatementPaymentVO> iterator = list.iterator();
      while (iterator.hasNext()) {
        StatementPaymentVO vo = iterator.next();
        String accountItemName = vo.getAccountItemName();
        if (ACCOUNT_ITEM_OF_MEMBER.equals(accountItemName)
            || ACCOUNT_ITEM_OF_PREPARE.equals(accountItemName)) {
          iterator.remove();
        }
      }
      if (memberAccountItem[0] != null) {
        StatementPaymentVO memberPrincipal = new StatementPaymentVO();
        memberPrincipal.setAccountItemId(memberAccountItem[0]);
        memberPrincipal.setAccountItemName("会员卡本金");
        setPrincipalAmount(type, query, memberAccountItem, memberPrincipal);
        list.add(0, memberPrincipal);
        StatementPaymentVO memberBonus = new StatementPaymentVO();
        memberBonus.setAccountItemId(memberAccountItem[0]);
        memberBonus.setAccountItemName("会员卡赠金");
        setBonusAmount(type, query, memberAccountItem, memberBonus);
        list.add(1, memberBonus);
      }
      if (prePaymentAccountItem[0] != null) {
        StatementPaymentVO prepaidPrincipal = new StatementPaymentVO();
        prepaidPrincipal.setAccountItemId(prePaymentAccountItem[0]);
        prepaidPrincipal.setAccountItemName("预付款本金");
        setPrincipalAmount(type, query, prePaymentAccountItem, prepaidPrincipal);
        list.add(2, prepaidPrincipal);
        StatementPaymentVO prepaidBonus = new StatementPaymentVO();
        prepaidBonus.setAccountItemId(prePaymentAccountItem[0]);
        prepaidBonus.setAccountItemName("预付款赠金");
        setBonusAmount(type, query, prePaymentAccountItem, prepaidBonus);
        list.add(3, prepaidBonus);
      }
    }
  }

  /**
   * 设置会员卡/预付款本金
   *
   * @param type 收支明细类型
   * @param query 查询条件
   * @param accountItem 支付方式ID
   * @param boundPayment 支付方式对象
   */
  private void setPrincipalAmount(
      Byte type,
      InboundAndOutboundStatementQuery query,
      Integer[] accountItem,
      StatementPaymentVO boundPayment) {
    switch (type) {
      case 0: // 本月账单
        query.setIsCurMonth((byte) 1);
        BigDecimal billChargeMemberPrincipalAmount =
            mapper.selectBillChargePrincipal(accountItem[0], query);
        boundPayment.setTotalAmount(billChargeMemberPrincipalAmount);
        break;
      case 1: // 本月收欠费
        query.setIsCurMonth((byte) 1);
        BigDecimal collectArrearsMemberPrincipalAmount =
            mapper.selectCollectArrearsPrincipal(accountItem[0], query);
        boundPayment.setTotalAmount(collectArrearsMemberPrincipalAmount);
        break;
      case 2: // 非本月收欠费
        query.setIsCurMonth((byte) 0);
        BigDecimal notCurMonthcollectArrearsMemberPrincipalAmount =
            mapper.selectCollectArrearsPrincipal(accountItem[0], query);
        boundPayment.setTotalAmount(notCurMonthcollectArrearsMemberPrincipalAmount);
        break;
      case 6: // 本月门诊代收
        query.setIsCurMonth((byte) 1);
        BigDecimal clinicCollectionMemberPrincipalAmount =
            mapper.selectClinicCollectionPrincipal(accountItem[0], query);
        boundPayment.setTotalAmount(clinicCollectionMemberPrincipalAmount);
        break;
      case 7: // 非本月门诊代收
        query.setIsCurMonth((byte) 0);
        BigDecimal notCurMonthClinicCollectionMemberPrincipalAmount =
            mapper.selectClinicCollectionPrincipal(accountItem[0], query);
        boundPayment.setTotalAmount(notCurMonthClinicCollectionMemberPrincipalAmount);
        break;
      case 8: // 本月账单退费
        query.setIsCurMonth((byte) 1);
        BigDecimal billRefundMemberPrincipalAmount =
            mapper.selectBillRefundPrincipal(accountItem[0], query);
        boundPayment.setTotalAmount(billRefundMemberPrincipalAmount);
        break;
      case 9: // 非本月账单退费
        query.setIsCurMonth((byte) 0);
        BigDecimal notCurMonthBillRefundMemberPrincipalAmount =
            mapper.selectBillRefundPrincipal(accountItem[0], query);
        boundPayment.setTotalAmount(notCurMonthBillRefundMemberPrincipalAmount);
        break;
      case 12: // 本月门诊被代收
        query.setIsCurMonth((byte) 1);
        BigDecimal clinicIsAcceptedMemberPrincipalAmount =
            mapper.selectClinicIsAcceptedPrincipal(accountItem[0], query);
        boundPayment.setTotalAmount(clinicIsAcceptedMemberPrincipalAmount);
        break;
      case 13: // 非本月门诊被代收
        query.setIsCurMonth((byte) 0);
        BigDecimal notCurMonthClinicIsAcceptedMemberPrincipalAmount =
            mapper.selectClinicIsAcceptedPrincipal(accountItem[0], query);
        boundPayment.setTotalAmount(notCurMonthClinicIsAcceptedMemberPrincipalAmount);
        break;
      default:
        break;
    }
  }

  /**
   * 设置会员卡/预付款赠金
   *
   * @param type 收支明细类型
   * @param query 查询条件
   * @param accountItem 支付方式ID
   * @param boundPayment 支付方式对象
   */
  private void setBonusAmount(
      Byte type,
      InboundAndOutboundStatementQuery query,
      Integer[] accountItem,
      StatementPaymentVO boundPayment) {
    switch (type) {
      case 0: // 本月账单收费
        query.setIsCurMonth((byte) 1);
        BigDecimal billChargeMemberBonusAmount =
            mapper.selectBillChargeBonus(accountItem[0], query);
        boundPayment.setTotalAmount(billChargeMemberBonusAmount);
        break;
      case 1: // 本月收欠费
        query.setIsCurMonth((byte) 1);
        BigDecimal collectArrearsMemberBonusAmount =
            mapper.selectCollectArrearsBonus(accountItem[0], query);
        boundPayment.setTotalAmount(collectArrearsMemberBonusAmount);
        break;
      case 2: // 非本月收欠费
        query.setIsCurMonth((byte) 0);
        BigDecimal notCurMonthcollectArrearsMemberBonusAmount =
            mapper.selectCollectArrearsBonus(accountItem[0], query);
        boundPayment.setTotalAmount(notCurMonthcollectArrearsMemberBonusAmount);
        break;
      case 6: // 本月门诊代收
        query.setIsCurMonth((byte) 1);
        BigDecimal clinicCollectionMemberBonusAmount =
            mapper.selectClinicCollectionBonus(accountItem[0], query);
        boundPayment.setTotalAmount(clinicCollectionMemberBonusAmount);
        break;
      case 7: // 非本月门诊代收
        query.setIsCurMonth((byte) 0);
        BigDecimal notCurMonthclinicCollectionMemberBonusAmount =
            mapper.selectClinicCollectionBonus(accountItem[0], query);
        boundPayment.setTotalAmount(notCurMonthclinicCollectionMemberBonusAmount);
        break;
      case 8: // 本月账单退费
        query.setIsCurMonth((byte) 1);
        BigDecimal billRefundMemberBonusAmount =
            mapper.selectBillRefundBonus(accountItem[0], query);
        boundPayment.setTotalAmount(billRefundMemberBonusAmount);
        break;
      case 9: // 非本月账单退费
        query.setIsCurMonth((byte) 0);
        BigDecimal notCurMonthbillRefundMemberBonusAmount =
            mapper.selectBillRefundBonus(accountItem[0], query);
        boundPayment.setTotalAmount(notCurMonthbillRefundMemberBonusAmount);
        break;
      case 12:
        query.setIsCurMonth((byte) 1);
        BigDecimal clinicIsAcceptedMemberBonusAmount =
            mapper.selectClinicIsAcceptedBonus(accountItem[0], query);
        boundPayment.setTotalAmount(clinicIsAcceptedMemberBonusAmount);
        break;
      case 13:
        query.setIsCurMonth((byte) 0);
        BigDecimal notCurMonthclinicIsAcceptedMemberBonusAmount =
            mapper.selectClinicIsAcceptedBonus(accountItem[0], query);
        boundPayment.setTotalAmount(notCurMonthclinicIsAcceptedMemberBonusAmount);
        break;
      default:
        break;
    }
  }

  /**
   * 根据条件查询门诊本月账单收费的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  private List<StatementPaymentVO> findBillChargePaymentInfoThisMonth(
      InboundAndOutboundStatementQuery query) {
    List<StatementPaymentVO> resultList = mapper.selectBillChargePaymentInfoThisMonth(query);
    return resultList;
  }

  /**
   * 根据条件查询门诊本月账单收欠费的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  private List<StatementPaymentVO> findCollectArrearsPaymentInfoThisMonth(
      InboundAndOutboundStatementQuery query) {
    List<StatementPaymentVO> resultList = mapper.selectCollectArrearsPaymentInfoThisMonth(query);
    return resultList;
  }

  /**
   * 根据条件查询门诊非本月账单收欠费的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  private List<StatementPaymentVO> findCollectArrearsPaymentInfoNotThisMonth(
      InboundAndOutboundStatementQuery query) {
    List<StatementPaymentVO> resultList = mapper.selectCollectArrearsPaymentInfoNotThisMonth(query);
    return resultList;
  }

  /**
   * 根据条件查询门诊会员卡充值的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  private List<StatementPaymentVO> findMemberChargePaymentInfo(
      InboundAndOutboundStatementQuery query) {
    List<StatementPaymentVO> resultList = mapper.selectMemberChargePaymentInfo(query);
    return resultList;
  }

  /**
   * 根据条件查询门诊预付款充值的入账方式分组信息
   *
   * @param query 查询条件
   * @param type
   * @return List<ClinicInboundAndOutboundVO>
   */
  private List<StatementPaymentVO> findPrePaidChargePaymentInfo(
          InboundAndOutboundStatementQuery query, Integer type) {
    List<StatementPaymentVO> resultList = mapper.selectPaidChargePaymentInfo(query, type);
    return resultList;
  }

  /**
   * 根据条件查询门诊产品售出的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  private List<StatementPaymentVO> findProductSoldPaymentInfo(
      InboundAndOutboundStatementQuery query) {
    List<StatementPaymentVO> resultList = mapper.selectProductSoldPaymentInfo(query);
    return resultList;
  }

  /**
   * 根据条件查询门诊本月代收的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  private List<StatementPaymentVO> findClinicCollectionPaymentInfoThisMonth(
      InboundAndOutboundStatementQuery query) {
    List<StatementPaymentVO> resultList = mapper.selectClinicCollectionPaymentInfoThisMonth(query);
    return resultList;
  }

  /**
   * 根据条件查询门诊非本月代收的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  private List<StatementPaymentVO> findClinicCollectionPaymentInfoNotThisMonth(
      InboundAndOutboundStatementQuery query) {
    List<StatementPaymentVO> resultList =
        mapper.selectClinicCollectionPaymentInfoNotThisMonth(query);
    return resultList;
  }

  /**
   * 根据条件查询门诊本月账单退费的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  private List<StatementPaymentVO> findBillRefundPaymentInfoThisMonth(
      InboundAndOutboundStatementQuery query) {
    List<StatementPaymentVO> resultList = mapper.selectBillRefundPaymentInfoThisMonth(query);
    return resultList;
  }

  /**
   * 根据条件查询门诊非本月账单退费的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  private List<StatementPaymentVO> findBillRefundPaymentInfoNotThisMonth(
      InboundAndOutboundStatementQuery query) {
    List<StatementPaymentVO> resultList = mapper.selectBillRefundPaymentInfoNotThisMonth(query);
    return resultList;
  }

  /**
   * 根据条件查询门诊会员卡退费的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  private List<StatementPaymentVO> findMemberRefundPaymentInfo(
      InboundAndOutboundStatementQuery query) {
    List<StatementPaymentVO> resultList = mapper.selectMemberRefundPaymentInfo(query);
    return resultList;
  }

  /**
   * 根据条件查询门诊预付款退费的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  private List<StatementPaymentVO> findPrepaidRefundPaymentInfo(
      InboundAndOutboundStatementQuery query) {
    List<StatementPaymentVO> resultList = mapper.selectPrepaidRefundPaymentInfo(query);
    return resultList;
  }

  /**
   * 根据条件查询门诊本月被代收的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  private List<StatementPaymentVO> findClinicIsAcceptedPaymentInfoThisMonth(
      InboundAndOutboundStatementQuery query) {
    List<StatementPaymentVO> resultList = mapper.selectClinicIsAcceptedPaymentInfoThisMonth(query);
    return resultList;
  }

  /**
   * 根据条件查询门诊非本月被代收的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  private List<StatementPaymentVO> findClinicIsAcceptedPaymentInfoNotThisMonth(
      InboundAndOutboundStatementQuery query) {
    List<StatementPaymentVO> resultList =
        mapper.selectClinicIsAcceptedPaymentInfoNotThisMonth(query);
    return resultList;
  }

  /**
   * 根据条件导出门诊出入账对账单
   *
   * @param query 查询条件
   * @return Map<String, Object>
   */
  public void inboundAndOutboundStatementExport(InboundAndOutboundStatementQuery query, HttpServletResponse response) throws IOException {
    List<ClinicInboundAndOutboundVO> list = findInboundAndOutboundStatement(query);
    Map<String, String> dynamicTitles = new LinkedHashMap<>(16);
    List<JSONObject> data = toDynamicData(list, dynamicTitles);
    ExcelUtil<JSONObject> excelUtil = new ExcelUtil<>(JSONObject.class);
    String fileName = excelUtil.getFileName(query.getStartDate(), query.getEndDate(), getAbbreviationById(query.getOrgId()), "对账单");
    excelUtil.exportExcel(response,data,"对账单",fileName,dynamicTitles);
  }

  public String getAbbreviationById(Integer orgId) {
    BaseOrganization organizationInfo = organizationMapper.selectByPrimaryKey(orgId);
    return organizationInfo.getAbbreviation();
  }

  /**
   * 转换成动态列格式
   *
   * @param list
   * @param dynamicTitles
   * @return
   */
  private List<JSONObject> toDynamicData(List<ClinicInboundAndOutboundVO> list, Map<String, String> dynamicTitles) {
    List<JSONObject> data = new ArrayList<>();
    if (StringHelper.isNotEmpty(list)) {
      list.forEach(vo->{
        JSONObject object = new JSONObject();
        object.put("name", vo.getName());
        dynamicTitles.put("name", "收支明细");
        List<StatementPaymentVO> paymentList = vo.getPaymentInfoList();
        BigDecimal total = BigDecimal.ZERO;
        if (StringHelper.isNotEmpty(paymentList)) {
          for (StatementPaymentVO payment : paymentList) {
            String key = payment.getAccountItemName();
            BigDecimal totalAmount = payment.getTotalAmount().setScale(2, BigDecimal.ROUND_HALF_UP);
            object.put(key, totalAmount);
            dynamicTitles.put(key, key);
            total = total.add(totalAmount);
          }
        }
        object.put("total", total);
        dynamicTitles.put("total", "小计");
        data.add(object);
      });
    }
    return data;
  }
}
