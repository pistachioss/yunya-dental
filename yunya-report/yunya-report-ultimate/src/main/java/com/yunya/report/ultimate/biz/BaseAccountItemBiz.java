package com.yunya.report.ultimate.biz;

import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.query.InboundAndOutboundStatementQuery;
import com.yunya.feign.report.domain.vo.BaseAccountItemVO;
import com.yunya.feign.report.domain.vo.ClinicInboundAndOutboundVO;
import com.yunya.feign.report.domain.vo.StatementPaymentVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.report.BaseAccountItem;
import com.yunya.report.ultimate.mapper.BaseAccountItemMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

  /**
   * 获取全部支付方式表头
   *
   * @return List<BaseAccountItemVO>
   */
  public List<BaseAccountItemVO> findAllPaymentList() {
    List<BaseAccountItemVO> resultList = mapper.selectAllPaymentList();
    if (StringHelper.isNotEmpty(resultList)) {
      Integer[] memberAccountItem = new Integer[1];
      Integer[] prePaymentAccountItem = new Integer[1];
      resultList.forEach(
          vo -> {
            String accountItemName = vo.getAccountItemName();
            if (ACCOUNT_ITEM_OF_MEMBER.equals(accountItemName)) {
              memberAccountItem[0] = vo.getAccountItemId();
            }
            if (ACCOUNT_ITEM_OF_PREPARE.equals(accountItemName)) {
              prePaymentAccountItem[0] = vo.getAccountItemId();
            }
          });
      Iterator<BaseAccountItemVO> iterator = resultList.iterator();
      while (iterator.hasNext()) {
        BaseAccountItemVO vo = iterator.next();
        String accountItemName = vo.getAccountItemName();
        if (ACCOUNT_ITEM_OF_MEMBER.equals(accountItemName)
            || ACCOUNT_ITEM_OF_PREPARE.equals(accountItemName)) {
          iterator.remove();
        }
      }
      if (memberAccountItem[0] != null) {
        BaseAccountItemVO memberPrincipal = new BaseAccountItemVO();
        memberPrincipal.setAccountItemId(memberAccountItem[0]);
        memberPrincipal.setAccountItemName("会员卡本金");
        resultList.add(0, memberPrincipal);
        BaseAccountItemVO memberBonus = new BaseAccountItemVO();
        memberBonus.setAccountItemId(memberAccountItem[0]);
        memberBonus.setAccountItemName("会员卡赠金");
        resultList.add(1, memberBonus);
      }
      if (prePaymentAccountItem[0] != null) {
        BaseAccountItemVO prePaymentPrincipal = new BaseAccountItemVO();
        prePaymentPrincipal.setAccountItemId(prePaymentAccountItem[0]);
        prePaymentPrincipal.setAccountItemName("预付款本金");
        resultList.add(2, prePaymentPrincipal);
        BaseAccountItemVO prePaymentBonus = new BaseAccountItemVO();
        prePaymentBonus.setAccountItemId(prePaymentAccountItem[0]);
        prePaymentBonus.setAccountItemName("预付款赠金");
        resultList.add(3, prePaymentBonus);
      }
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

    // 门诊收欠费（本月）-- 查询时间段内本门诊账单非首次收费
    collectArrearsThisMonth(query, resultList);

    // 门诊收欠费（非本月）-- 非查询时间段内本门诊账单的非首次收费
    collectArrearsNotThisMonth(query, resultList);

    // 门诊会员充值 -- 查询时间段内本门诊会员卡充值
    memberCharge(query, resultList);

    // 门诊预付款充值 -- 查询时间段内本门诊预付款充值
    prePaidCharge(query, resultList);

    // 产品售出 -- 查询时间段内本门诊产品售出
    productSold(query, resultList);

    // 诊所代收（本月）-- 查询时间段内非本门诊账单在本门诊收费
    clinicCollectionThisMonth(query, resultList);

    // 诊所代收（非本月）-- 非查询时间段内非本门诊账单在本门诊收费
    clinicCollectionNotThisMonth(query, resultList);

    // 账单退费（本月）-- 查询时间段内本门诊账单退费
    billRefundThisMonth(query, resultList);

    // 账单退费（非本月）-- 非查询时间段内本门诊账单退费
    billRefundNotThisMonth(query, resultList);

    // 会员卡退费 -- 查询时间段内本门诊会员卡充值退费
    memberRefund(query, resultList);

    // 预付款退费 -- 查询时间段内本门诊预付款充值退费
    prepaidRefund(query, resultList);

    // 计算出入账合计
    calculateInboundAndOutbound(resultList);


    // 诊所被代收（本月）-- 查询时间段内本门诊账单不在本门诊收费
    clinicIsAcceptedThisMonth(query, resultList);

    // 诊所被代收（非本月）-- 非查询时间段内本门诊账单不在门诊收费
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
    IntStream.range(0, resultList.size())
        .forEach(
            i -> {
              if (i <= 7) {
                List<StatementPaymentVO> paymentInfoList = resultList.get(i).getPaymentInfoList();
                inboundPayments.addAll(paymentInfoList);
              } else if (i <= 11) {
                List<StatementPaymentVO> paymentInfoList = resultList.get(i).getPaymentInfoList();
                outboundPayments.addAll(paymentInfoList);
              }
            });
    // 入账支付信息分组求和
    List<StatementPaymentVO> inboundPaymentResult =
        groupAndCalculateStatementPayment(inboundPayments);
    // 出账支付方式分组求和¬
    List<StatementPaymentVO> outboundPaymentResult =
        groupAndCalculateStatementPayment(outboundPayments);

    for (StatementPaymentVO in : inboundPaymentResult) {
      for (StatementPaymentVO out : outboundPaymentResult) {
        if (in.getAccountItemId().equals(out.getAccountItemId())) {
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
          .collect(Collectors.groupingBy(StatementPaymentVO::getAccountItemId, Collectors.toList()))
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
   * 诊所被代收（非本月）
   *
   * @param query 查询条件
   * @param resultList 结果集
   */
  private void clinicIsAcceptedNotThisMonth(
      InboundAndOutboundStatementQuery query, List<ClinicInboundAndOutboundVO> resultList) {
    List<StatementPaymentVO> clinicIsAcceptedNotThisMonth =
        findClinicIsAcceptedPaymentInfoNotThisMonth(query);
    reBuildStatementsPaymentList((byte) 14, clinicIsAcceptedNotThisMonth, query);
    ClinicInboundAndOutboundVO clinicIsAcceptedNotThisMonthVO = new ClinicInboundAndOutboundVO();
    clinicIsAcceptedNotThisMonthVO.setType((byte) 14);
    clinicIsAcceptedNotThisMonthVO.setName("诊所被代收（非本月）");
    clinicIsAcceptedNotThisMonthVO.setPaymentInfoList(clinicIsAcceptedNotThisMonth);
    resultList.add(14, clinicIsAcceptedNotThisMonthVO);
  }

  /**
   * 诊所被代收（本月）
   *
   * @param query 查询条件
   * @param resultList 结果集
   */
  private void clinicIsAcceptedThisMonth(
      InboundAndOutboundStatementQuery query, List<ClinicInboundAndOutboundVO> resultList) {
    List<StatementPaymentVO> clinicIsAcceptedThisMonth =
        findClinicIsAcceptedPaymentInfoThisMonth(query);
    reBuildStatementsPaymentList((byte) 13, clinicIsAcceptedThisMonth, query);
    ClinicInboundAndOutboundVO clinicIsAcceptedThisMonthVO = new ClinicInboundAndOutboundVO();
    clinicIsAcceptedThisMonthVO.setType((byte) 13);
    clinicIsAcceptedThisMonthVO.setName("诊所被代收（本月）");
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
    List<StatementPaymentVO> prepaidRefund = findPrepaidRefundPaymentInfo(query);
    reBuildStatementsPaymentList((byte) 11, prepaidRefund, query);
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
    List<StatementPaymentVO> memberRefund = findMemberRefundPaymentInfo(query);
    reBuildStatementsPaymentList((byte) 10, memberRefund, query);
    ClinicInboundAndOutboundVO memberRefundVO = new ClinicInboundAndOutboundVO();
    memberRefundVO.setType((byte) 10);
    memberRefundVO.setName("会员卡退费");
    memberRefundVO.setPaymentInfoList(memberRefund);
    resultList.add(10, memberRefundVO);
  }

  /**
   * 账单退费（非本月）
   *
   * @param query 查询条件
   * @param resultList 结果集
   */
  private void billRefundNotThisMonth(
      InboundAndOutboundStatementQuery query, List<ClinicInboundAndOutboundVO> resultList) {
    List<StatementPaymentVO> billRefundNotThisMonth = findBillRefundPaymentInfoNotThisMonth(query);
    reBuildStatementsPaymentList((byte) 9, billRefundNotThisMonth, query);
    ClinicInboundAndOutboundVO billRefundNotThisMonthVO = new ClinicInboundAndOutboundVO();
    billRefundNotThisMonthVO.setType((byte) 9);
    billRefundNotThisMonthVO.setName("账单退费（非本月）");
    billRefundNotThisMonthVO.setPaymentInfoList(billRefundNotThisMonth);
    resultList.add(9, billRefundNotThisMonthVO);
  }

  /**
   * 账单退费（本月）
   *
   * @param query 查询条件
   * @param resultList 结果集
   */
  private void billRefundThisMonth(
      InboundAndOutboundStatementQuery query, List<ClinicInboundAndOutboundVO> resultList) {
    List<StatementPaymentVO> billRefundThisMonth = findBillRefundPaymentInfoThisMonth(query);
    reBuildStatementsPaymentList((byte) 8, billRefundThisMonth, query);
    ClinicInboundAndOutboundVO billRefundThisMonthVO = new ClinicInboundAndOutboundVO();
    billRefundThisMonthVO.setType((byte) 8);
    billRefundThisMonthVO.setName("账单退费（本月）");
    billRefundThisMonthVO.setPaymentInfoList(billRefundThisMonth);
    resultList.add(8, billRefundThisMonthVO);
  }

  /**
   * 诊所代收（非本月）
   *
   * @param query 查询条件
   * @param resultList 结果集
   */
  private void clinicCollectionNotThisMonth(
      InboundAndOutboundStatementQuery query, List<ClinicInboundAndOutboundVO> resultList) {
    List<StatementPaymentVO> clinicCollectionNotThisMonth =
        findClinicCollectionPaymentInfoNotThisMonth(query);
    reBuildStatementsPaymentList((byte) 7, clinicCollectionNotThisMonth, query);
    ClinicInboundAndOutboundVO clinicCollectionNotThisMonthVO = new ClinicInboundAndOutboundVO();
    clinicCollectionNotThisMonthVO.setType((byte) 7);
    clinicCollectionNotThisMonthVO.setName("诊所代收（非本月）");
    clinicCollectionNotThisMonthVO.setPaymentInfoList(clinicCollectionNotThisMonth);
    resultList.add(7, clinicCollectionNotThisMonthVO);
  }

  /**
   * 诊所代收（本月）
   *
   * @param query 查询条件
   * @param resultList 结果集
   */
  private void clinicCollectionThisMonth(
      InboundAndOutboundStatementQuery query, List<ClinicInboundAndOutboundVO> resultList) {
    List<StatementPaymentVO> clinicCollectionThisMonth =
        findClinicCollectionPaymentInfoThisMonth(query);
    reBuildStatementsPaymentList((byte) 6, clinicCollectionThisMonth, query);
    ClinicInboundAndOutboundVO clinicCollectionThisMonthVO = new ClinicInboundAndOutboundVO();
    clinicCollectionThisMonthVO.setType((byte) 6);
    clinicCollectionThisMonthVO.setName("诊所代收（本月）");
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
    List<StatementPaymentVO> productSold = findProductSoldPaymentInfo(query);
    reBuildStatementsPaymentList((byte) 5, productSold, query);
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
    List<StatementPaymentVO> prePaidCharge = findPrePaidChargePaymentInfo(query);
    reBuildStatementsPaymentList((byte) 4, prePaidCharge, query);
    ClinicInboundAndOutboundVO prePaidChargeVO = new ClinicInboundAndOutboundVO();
    prePaidChargeVO.setType((byte) 4);
    prePaidChargeVO.setName("预付款充值");
    prePaidChargeVO.setPaymentInfoList(prePaidCharge);
    resultList.add(4, prePaidChargeVO);
  }

  /**
   * 门诊会员充值
   *
   * @param query 查询条件
   * @param resultList 结果集
   */
  private void memberCharge(
      InboundAndOutboundStatementQuery query, List<ClinicInboundAndOutboundVO> resultList) {
    List<StatementPaymentVO> memberCharge = findMemberChargePaymentInfo(query);
    reBuildStatementsPaymentList((byte) 3, memberCharge, query);
    ClinicInboundAndOutboundVO memberChargeVO = new ClinicInboundAndOutboundVO();
    memberChargeVO.setType((byte) 3);
    memberChargeVO.setName("会员充值");
    memberChargeVO.setPaymentInfoList(memberCharge);
    resultList.add(3, memberChargeVO);
  }

  /**
   * 门诊收欠费（非本月）
   *
   * @param query 查询条件
   * @param resultList 结果集
   */
  private void collectArrearsNotThisMonth(
      InboundAndOutboundStatementQuery query, List<ClinicInboundAndOutboundVO> resultList) {
    List<StatementPaymentVO> collectArrearsNotThisMonth =
        findCollectArrearsPaymentInfoNotThisMonth(query);
    reBuildStatementsPaymentList((byte) 2, collectArrearsNotThisMonth, query);
    ClinicInboundAndOutboundVO collectArrearsNotThisMonthVO = new ClinicInboundAndOutboundVO();
    collectArrearsNotThisMonthVO.setType((byte) 2);
    collectArrearsNotThisMonthVO.setName("收欠费（非本月）");
    collectArrearsNotThisMonthVO.setPaymentInfoList(collectArrearsNotThisMonth);
    resultList.add(2, collectArrearsNotThisMonthVO);
  }

  /**
   * 门诊收欠费（本月）
   *
   * @param query 查询条件
   * @param resultList 结果集
   */
  private void collectArrearsThisMonth(
      InboundAndOutboundStatementQuery query, List<ClinicInboundAndOutboundVO> resultList) {
    List<StatementPaymentVO> collectArrearsThisMonth =
        findCollectArrearsPaymentInfoThisMonth(query);
    reBuildStatementsPaymentList((byte) 1, collectArrearsThisMonth, query);
    ClinicInboundAndOutboundVO collectArrearsOfThisMonthVO = new ClinicInboundAndOutboundVO();
    collectArrearsOfThisMonthVO.setType((byte) 1);
    collectArrearsOfThisMonthVO.setName("收欠费（本月）");
    collectArrearsOfThisMonthVO.setPaymentInfoList(collectArrearsThisMonth);
    resultList.add(1, collectArrearsOfThisMonthVO);
  }

  /**
   * 门诊账单收费
   *
   * @param query 查询条件
   * @param resultList 结果集
   */
  private void billChargeThisMonth(
      InboundAndOutboundStatementQuery query, List<ClinicInboundAndOutboundVO> resultList) {
    List<StatementPaymentVO> billChargeThisMonth = findBillChargePaymentInfoThisMonth(query);
    reBuildStatementsPaymentList((byte) 0, billChargeThisMonth, query);
    ClinicInboundAndOutboundVO billChargeVO = new ClinicInboundAndOutboundVO();
    billChargeVO.setType((byte) 0);
    billChargeVO.setName("账单收费（本月）");
    billChargeVO.setPaymentInfoList(billChargeThisMonth);
    resultList.add(0, billChargeVO);
  }

  /**
   * 重构支付方式汇总列表
   *
   * @param type "收支明细分类:0-账单收费（本月）；1-收欠费（本月）；2-收欠费（非本月）；3-会员充值；4-预付款充值；5-产品售出；"
   *     "6-诊所代收（本月）；7-诊所代收（非本月）；8-账单退费（本月）；9-账单退费（非本月）；"
   *     "10-会员卡退费；11-预付款退费；12-诊所被代收账（本月）；13-诊所被代收帐（非本月）"
   * @param list 支付方式列表
   * @param query 会员卡/预付款本金赠金查询参数
   */
  private void reBuildStatementsPaymentList(
      Byte type, List<StatementPaymentVO> list, InboundAndOutboundStatementQuery query) {
    setPaymentListValue(list, type, query);
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
      list.forEach(
          vo -> {
            String accountItemName = vo.getAccountItemName();
            if (ACCOUNT_ITEM_OF_MEMBER.equals(accountItemName)) {
              memberAccountItem[0] = vo.getAccountItemId();
            }
            if (ACCOUNT_ITEM_OF_PREPARE.equals(accountItemName)) {
              prePaymentAccountItem[0] = vo.getAccountItemId();
            }
          });
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
        BigDecimal notCurMonthclinicCollectionMemberPrincipalAmount =
            mapper.selectClinicCollectionPrincipal(accountItem[0], query);
        boundPayment.setTotalAmount(notCurMonthclinicCollectionMemberPrincipalAmount);
        break;
      case 8: // 本月账单退费
        query.setIsCurMonth((byte) 1);
        BigDecimal billRefundMemberPrincipalAmount =
            mapper.selectBillRefundPrincipal(accountItem[0], query);
        boundPayment.setTotalAmount(billRefundMemberPrincipalAmount);
        break;
      case 9: // 非本月账单退费
        query.setIsCurMonth((byte) 0);
        BigDecimal notCurMonthbillRefundMemberPrincipalAmount =
            mapper.selectBillRefundPrincipal(accountItem[0], query);
        boundPayment.setTotalAmount(notCurMonthbillRefundMemberPrincipalAmount);
        break;
      case 12: // 本月门诊被代收
        query.setIsCurMonth((byte) 1);
        BigDecimal clinicIsAcceptedMemberPrincipalAmount =
            mapper.selectClinicIsAcceptedPrincipal(accountItem[0], query);
        boundPayment.setTotalAmount(clinicIsAcceptedMemberPrincipalAmount);
        break;
      case 13: // 非本月门诊被代收
        query.setIsCurMonth((byte) 0);
        BigDecimal notCurMonthclinicIsAcceptedMemberPrincipalAmount =
            mapper.selectClinicIsAcceptedPrincipal(accountItem[0], query);
        boundPayment.setTotalAmount(notCurMonthclinicIsAcceptedMemberPrincipalAmount);
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
   * @return List<ClinicInboundAndOutboundVO>
   */
  private List<StatementPaymentVO> findPrePaidChargePaymentInfo(
      InboundAndOutboundStatementQuery query) {
    List<StatementPaymentVO> resultList = mapper.selectPaidChargePaymentInfo(query);
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
}
