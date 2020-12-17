package com.yunya.report.ultimate.biz;

import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.query.InboundAndOutboundStatementQuery;
import com.yunya.feign.report.domain.vo.BaseAccountItemVO;
import com.yunya.feign.report.domain.vo.StatementPaymentVO;
import com.yunya.feign.report.domain.vo.ClinicInboundAndOutboundVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.report.BaseAccountItem;
import com.yunya.report.ultimate.mapper.BaseAccountItemMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

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
    // 门诊账单收费
    List<StatementPaymentVO> billCharge = findBillChargePaymentInfo(query);
    reBuildStatementsPaymentList((byte) 0, billCharge, query);
    ClinicInboundAndOutboundVO billChargeVO = new ClinicInboundAndOutboundVO();
    billChargeVO.setType((byte) 0);
    billChargeVO.setName("账单收费");
    billChargeVO.setPaymentInfoList(billCharge);
    resultList.add(0, billChargeVO);
    // 门诊收欠费
    List<StatementPaymentVO> collectArrears = findCollectArrearsPaymentInfo(query);
    reBuildStatementsPaymentList((byte) 1, collectArrears, query);
    ClinicInboundAndOutboundVO collectArrearsVO = new ClinicInboundAndOutboundVO();
    collectArrearsVO.setType((byte) 1);
    collectArrearsVO.setName("收欠费");
    collectArrearsVO.setPaymentInfoList(collectArrears);
    resultList.add(1, collectArrearsVO);
    // 门诊会员充值
    List<StatementPaymentVO> memberCharge = findMemberChargePaymentInfo(query);
    reBuildStatementsPaymentList((byte) 2, memberCharge, query);
    ClinicInboundAndOutboundVO memberChargeVO = new ClinicInboundAndOutboundVO();
    memberChargeVO.setType((byte) 2);
    memberChargeVO.setName("会员充值");
    memberChargeVO.setPaymentInfoList(memberCharge);
    resultList.add(2, memberChargeVO);
    // 门诊预付款充值
    List<StatementPaymentVO> prePaidCharge = findPrePaidChargePaymentInfo(query);
    reBuildStatementsPaymentList((byte) 3, prePaidCharge, query);
    ClinicInboundAndOutboundVO prePaidChargeVO = new ClinicInboundAndOutboundVO();
    prePaidChargeVO.setType((byte) 3);
    prePaidChargeVO.setName("预付款充值");
    prePaidChargeVO.setPaymentInfoList(prePaidCharge);
    resultList.add(3, prePaidChargeVO);
    // 产品售出
    List<StatementPaymentVO> productSold = findProductSoldPaymentInfo(query);
    reBuildStatementsPaymentList((byte) 4, productSold, query);
    ClinicInboundAndOutboundVO productSoldVO = new ClinicInboundAndOutboundVO();
    productSoldVO.setType((byte) 4);
    productSoldVO.setName("产品售出");
    productSoldVO.setPaymentInfoList(productSold);
    resultList.add(4, productSoldVO);
    // 诊所代收
    List<StatementPaymentVO> clinicCollection = findClinicCollectionPaymentInfo(query);
    reBuildStatementsPaymentList((byte) 5, clinicCollection, query);
    ClinicInboundAndOutboundVO clinicCollectionVO = new ClinicInboundAndOutboundVO();
    clinicCollectionVO.setType((byte) 5);
    clinicCollectionVO.setName("诊所代收");
    clinicCollectionVO.setPaymentInfoList(clinicCollection);
    resultList.add(5, clinicCollectionVO);
    // 账单退费
    List<StatementPaymentVO> billRefund = findBillRefundPaymentInfo(query);
    reBuildStatementsPaymentList((byte) 6, billRefund, query);
    ClinicInboundAndOutboundVO billRefundVO = new ClinicInboundAndOutboundVO();
    billRefundVO.setType((byte) 6);
    billRefundVO.setName("账单退费");
    billRefundVO.setPaymentInfoList(billRefund);
    resultList.add(6, billRefundVO);
    // 会员卡退费
    List<StatementPaymentVO> memberRefund = findMemberRefundPaymentInfo(query);
    reBuildStatementsPaymentList((byte) 7, memberRefund, query);
    ClinicInboundAndOutboundVO memberRefundVO = new ClinicInboundAndOutboundVO();
    memberRefundVO.setType((byte) 7);
    memberRefundVO.setName("会员卡退费");
    memberRefundVO.setPaymentInfoList(memberRefund);
    resultList.add(7, memberRefundVO);
    // 预付款退费
    List<StatementPaymentVO> prepaidRefund = findPrepaidRefundPaymentInfo(query);
    reBuildStatementsPaymentList((byte) 8, prepaidRefund, query);
    ClinicInboundAndOutboundVO prepaidRefundVO = new ClinicInboundAndOutboundVO();
    prepaidRefundVO.setType((byte) 8);
    prepaidRefundVO.setName("预付款退费");
    prepaidRefundVO.setPaymentInfoList(prepaidRefund);
    resultList.add(8, prepaidRefundVO);
    // 诊所被代收
    List<StatementPaymentVO> clinicIsAccepted = findClinicIsAcceptedPaymentInfo(query);
    reBuildStatementsPaymentList((byte) 9, clinicIsAccepted, query);
    ClinicInboundAndOutboundVO clinicIsAcceptedVO = new ClinicInboundAndOutboundVO();
    clinicIsAcceptedVO.setType((byte) 9);
    clinicIsAcceptedVO.setName("诊所被代收");
    clinicIsAcceptedVO.setPaymentInfoList(clinicIsAccepted);
    resultList.add(9, clinicIsAcceptedVO);
    return resultList;
  }

  /**
   * 重构支付方式汇总列表
   *
   * @param type 类型（0-账单收费；1-收欠费；2-会员充值；3-预付款充值；4-产品售出；5-诊所代收；6-账单退费；7-会员卡退费；8-预付款退费；9-诊所被代收）
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
      case 0:
        BigDecimal billChargeMemberPrincipalAmount =
            mapper.selectBillChargePrincipal(accountItem[0], query);
        boundPayment.setTotalAmount(billChargeMemberPrincipalAmount);
        break;
      case 1:
        BigDecimal collectArrearsMemberPrincipalAmount =
            mapper.selectCollectArrearsPrincipal(accountItem[0], query);
        boundPayment.setTotalAmount(collectArrearsMemberPrincipalAmount);
        break;
      case 5:
        BigDecimal clinicCollectionMemberPrincipalAmount =
            mapper.selectClinicCollectionPrincipal(accountItem[0], query);
        boundPayment.setTotalAmount(clinicCollectionMemberPrincipalAmount);
        break;
      case 6:
        BigDecimal billRefundMemberPrincipalAmount =
            mapper.selectBillRefundPrincipal(accountItem[0], query);
        boundPayment.setTotalAmount(billRefundMemberPrincipalAmount);
        break;
      case 9:
        BigDecimal clinicIsAcceptedMemberPrincipalAmount =
            mapper.selectClinicIsAcceptedPrincipal(accountItem[0], query);
        boundPayment.setTotalAmount(clinicIsAcceptedMemberPrincipalAmount);
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
      case 0:
        BigDecimal billChargeMemberBonusAmount =
            mapper.selectBillChargeBonus(accountItem[0], query);
        boundPayment.setTotalAmount(billChargeMemberBonusAmount);
        break;
      case 1:
        BigDecimal collectArrearsMemberBonusAmount =
            mapper.selectCollectArrearsBonus(accountItem[0], query);
        boundPayment.setTotalAmount(collectArrearsMemberBonusAmount);
        break;
      case 5:
        BigDecimal clinicCollectionMemberBonusAmount =
            mapper.selectClinicCollectionBonus(accountItem[0], query);
        boundPayment.setTotalAmount(clinicCollectionMemberBonusAmount);
        break;
      case 6:
        BigDecimal billRefundMemberBonusAmount =
            mapper.selectBillRefundBonus(accountItem[0], query);
        boundPayment.setTotalAmount(billRefundMemberBonusAmount);
        break;
      case 9:
        BigDecimal clinicIsAcceptedMemberBonusAmount =
            mapper.selectClinicIsAcceptedBonus(accountItem[0], query);
        boundPayment.setTotalAmount(clinicIsAcceptedMemberBonusAmount);
        break;
      default:
        break;
    }
  }

  /**
   * 根据条件查询门诊账单收费的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  private List<StatementPaymentVO> findBillChargePaymentInfo(InboundAndOutboundStatementQuery query) {
    List<StatementPaymentVO> resultList = mapper.selectBillChargePaymentInfo(query);
    return resultList;
  }

  /**
   * 根据条件查询门诊收欠费的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  private List<StatementPaymentVO> findCollectArrearsPaymentInfo(
      InboundAndOutboundStatementQuery query) {
    List<StatementPaymentVO> resultList = mapper.selectCollectArrearsPaymentInfo(query);
    return resultList;
  }

  /**
   * 根据条件查询门诊会员卡充值的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  private List<StatementPaymentVO> findMemberChargePaymentInfo(InboundAndOutboundStatementQuery query) {
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
  private List<StatementPaymentVO> findProductSoldPaymentInfo(InboundAndOutboundStatementQuery query) {
    List<StatementPaymentVO> resultList = mapper.selectProductSoldPaymentInfo(query);
    return resultList;
  }

  /**
   * 根据条件查询门诊代收的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  private List<StatementPaymentVO> findClinicCollectionPaymentInfo(
      InboundAndOutboundStatementQuery query) {
    List<StatementPaymentVO> resultList = mapper.selectClinicCollectionPaymentInfo(query);
    return resultList;
  }

  /**
   * 根据条件查询门诊退费的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  private List<StatementPaymentVO> findBillRefundPaymentInfo(InboundAndOutboundStatementQuery query) {
    List<StatementPaymentVO> resultList = mapper.selectBillRefundPaymentInfo(query);
    return resultList;
  }

  /**
   * 根据条件查询门诊会员卡退费的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  private List<StatementPaymentVO> findMemberRefundPaymentInfo(InboundAndOutboundStatementQuery query) {
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
   * 根据条件查询门诊被代收的入账方式分组信息
   *
   * @param query 查询条件
   * @return List<ClinicInboundAndOutboundVO>
   */
  private List<StatementPaymentVO> findClinicIsAcceptedPaymentInfo(
      InboundAndOutboundStatementQuery query) {
    List<StatementPaymentVO> resultList = mapper.selectClinicIsAcceptedPaymentInfo(query);
    return resultList;
  }
}
