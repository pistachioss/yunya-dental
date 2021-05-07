package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.MemberQueryForm;
import com.yunya.feign.report.domain.query.PrepaidQueryForm;
import com.yunya.feign.report.domain.query.StatementPatientCardRechargeDetailInfoQuery;
import com.yunya.feign.report.domain.query.StatementPatientCardRefundDetailQuery;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BaseOrganization;
import com.yunya.models.report.BasePatientMemberOccurLog;
import com.yunya.report.ultimate.mapper.BaseOrganizationMapper;
import com.yunya.report.ultimate.mapper.BasePatientMemberOccurLogMapper;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;

import static com.yunya.framework.common.constant.BusinessConstants.*;

/**
 * 简介:会员卡/预付款概况控制层
 *
 * @author: WY
 * @date: 2020/10/24 13:37
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MemberOccurLogBiz
    extends BaseBiz<BasePatientMemberOccurLogMapper, BasePatientMemberOccurLog> {

  @Resource private BaseOrganizationMapper baseOrganizationMapper;

  /**
   * 会员卡充值查询
   *
   * @param form 会员卡充值查询Form
   * @return List<MemberRechargeLogBizVo>
   */
  public PageInfo<BaseMemberRechargeLogVo> memberRechargeList(MemberQueryForm form) {
    if (StringHelper.isNotEmpty(form.getEndDate())) {
      String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
      form.setEndDate(endDate);
    }
    if (form.getWhetherPage()) {
      PageHelper.startPage(form.getPageNum(), form.getPageSize());
    }

    List<BaseMemberRechargeLogVo> memberRechargeLogBizVos = mapper.selectMemberRechargeList(form);
    return new PageInfo<>(memberRechargeLogBizVos);
  }

  /**
   * 导出会员充值列表
   *
   * @param response 导出响应
   * @param form 条件
   */
  public void exportMemberRechargeList(HttpServletResponse response, MemberQueryForm form)
      throws IOException {
    if (StringHelper.isNotEmpty(form.getEndDate())) {
      String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
      form.setEndDate(endDate);
    }
    List<BaseMemberRechargeLogVo> memberRechargeLogBizVos = mapper.selectMemberRechargeList(form);
    ExcelUtil<BaseMemberRechargeLogVo> excelUtil = new ExcelUtil<>(BaseMemberRechargeLogVo.class);
    if (StringHelper.isNotNull(form.getOrgId())) {
      BaseOrganization baseOrganization = new BaseOrganization();
      baseOrganization.setOrgId(form.getOrgId());
      BaseOrganization baseOrganizationv = baseOrganizationMapper.selectOne(baseOrganization);
      if (baseOrganizationv != null) {
        excelUtil.exportExcel(
            response,
            memberRechargeLogBizVos,
            "会员卡充值记录表",
            baseOrganizationv.getAbbreviation() + "会员卡充值记录表");
      } else {
        excelUtil.exportExcel(response, memberRechargeLogBizVos, "会员卡充值记录表", "会员卡充值记录表");
      }
    } else {
      excelUtil.exportExcel(response, memberRechargeLogBizVos, "会员卡充值记录表", "会员卡充值记录表");
    }
  }

  /**
   * 会员卡消费查询
   *
   * @param form 会员卡消费查询Form
   * @return 会员卡消费记录
   */
  public PageInfo<BaseMemberExpendLogVo> memberExpendList(MemberQueryForm form) {
    if (StringHelper.isNotEmpty(form.getEndDate())) {
      String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
      form.setEndDate(endDate);
    }

    if (form.getWhetherPage()) {
      PageHelper.startPage(form.getPageNum(), form.getPageSize());
    }
    List<BaseMemberExpendLogVo> baseMemberExpendLogVos = mapper.selectMemberExpendtList(form);
    return new PageInfo<>(baseMemberExpendLogVos);
  }

  /**
   * 导出会员消费列表
   *
   * @param response 导出响应
   * @param form 条件
   */
  public void exportMemberExpendList(HttpServletResponse response, MemberQueryForm form)
      throws IOException {
    if (StringHelper.isNotEmpty(form.getEndDate())) {
      String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
      form.setEndDate(endDate);
    }
    List<BaseMemberExpendLogVo> baseMemberExpendLogVos = mapper.selectMemberExpendtList(form);
    ExcelUtil<BaseMemberExpendLogVo> excelUtil = new ExcelUtil<>(BaseMemberExpendLogVo.class);
    if (StringHelper.isNotNull(form.getOrgId())) {
      BaseOrganization baseOrganization = new BaseOrganization();
      baseOrganization.setOrgId(form.getOrgId());
      BaseOrganization baseOrganizationv = baseOrganizationMapper.selectOne(baseOrganization);
      if (baseOrganizationv != null) {
        excelUtil.exportExcel(
            response,
            baseMemberExpendLogVos,
            "会员卡消费记录表",
            baseOrganizationv.getAbbreviation() + "会员卡消费记录表");
      } else {
        excelUtil.exportExcel(response, baseMemberExpendLogVos, "会员卡消费记录表", "会员卡消费记录表");
      }
    } else {
      excelUtil.exportExcel(response, baseMemberExpendLogVos, "会员卡消费记录表", "会员卡消费记录表");
    }
  }

  /**
   * 会员卡退费查询
   *
   * @param form 会员卡退费查询Form
   * @return 会员卡退费记录
   */
  public PageInfo<BaseMemberReturnLogVo> memberReturnList(MemberQueryForm form) {
    if (StringHelper.isNotEmpty(form.getEndDate())) {
      String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
      form.setEndDate(endDate);
    }
    if (form.getWhetherPage()) {
      PageHelper.startPage(form.getPageNum(), form.getPageSize());
    }
    List<BaseMemberReturnLogVo> baseMemberReturnLogVos = mapper.selectMemberReturnList(form);
    return new PageInfo<>(baseMemberReturnLogVos);
  }

  /**
   * 导出会员退费列表
   *
   * @param response 导出响应
   * @param form 条件
   */
  public void exportMemberReturnList(HttpServletResponse response, MemberQueryForm form)
      throws IOException {
    if (StringHelper.isNotEmpty(form.getEndDate())) {
      String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
      form.setEndDate(endDate);
    }
    List<BaseMemberReturnLogVo> baseMemberReturnLogVos = mapper.selectMemberReturnList(form);
    ExcelUtil<BaseMemberReturnLogVo> excelUtil = new ExcelUtil<>(BaseMemberReturnLogVo.class);
    if (StringHelper.isNotNull(form.getOrgId())) {
      BaseOrganization baseOrganization = new BaseOrganization();
      baseOrganization.setOrgId(form.getOrgId());
      BaseOrganization baseOrganizationv = baseOrganizationMapper.selectOne(baseOrganization);
      if (baseOrganizationv != null) {
        excelUtil.exportExcel(
            response,
            baseMemberReturnLogVos,
            "会员卡退费记录表",
            baseOrganizationv.getAbbreviation() + "会员卡退费记录表");
      } else {
        excelUtil.exportExcel(response, baseMemberReturnLogVos, "会员卡退费记录表", "会员卡退费记录表");
      }
    } else {
      excelUtil.exportExcel(response, baseMemberReturnLogVos, "会员卡退费记录表", "会员卡退费记录表");
    }
  }

  /**
   * 预付款充值查询
   *
   * @param form 预付款充值form
   * @return 预付款充值记录
   */
  public PageInfo<BasePrepaidRechargeLogVo> prepaidRechargeList(PrepaidQueryForm form) {
    if (StringHelper.isNotEmpty(form.getEndDate())) {
      String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
      form.setEndDate(endDate);
    }
    if (form.getWhetherPage()) {
      PageHelper.startPage(form.getPageNum(), form.getPageSize());
    }
    List<BasePrepaidRechargeLogVo> basePrepaidRechargeLogVoList =
        mapper.selectPrepaidRechargeList(form);
    return new PageInfo<>(basePrepaidRechargeLogVoList);
  }

  /**
   * 导出预付款充值列表
   *
   * @param response 导出响应
   * @param form 条件
   */
  public void exportPrepaidRechargeList(HttpServletResponse response, PrepaidQueryForm form)
      throws IOException {
    if (StringHelper.isNotEmpty(form.getEndDate())) {
      String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
      form.setEndDate(endDate);
    }
    List<BasePrepaidRechargeLogVo> basePrepaidRechargeLogVoList =
        mapper.selectPrepaidRechargeList(form);
    ExcelUtil<BasePrepaidRechargeLogVo> excelUtil = new ExcelUtil<>(BasePrepaidRechargeLogVo.class);
    if (StringHelper.isNotNull(form.getOrgId())) {
      BaseOrganization baseOrganization = new BaseOrganization();
      baseOrganization.setOrgId(form.getOrgId());
      BaseOrganization baseOrganizationv = baseOrganizationMapper.selectOne(baseOrganization);
      if (baseOrganizationv != null) {
        excelUtil.exportExcel(
            response,
            basePrepaidRechargeLogVoList,
            "预付款充值记录表",
            baseOrganizationv.getAbbreviation() + "预付款充值记录表");
      } else {
        excelUtil.exportExcel(response, basePrepaidRechargeLogVoList, "预付款充值记录表", "预付款消费记录表");
      }
    } else {
      excelUtil.exportExcel(response, basePrepaidRechargeLogVoList, "预付款充值记录表", "预付款消费记录表");
    }
  }

  /**
   * 预付款消费查询
   *
   * @param form 预付款消费form
   * @return List<PrepaidExpendLogBizVo>
   */
  public PageInfo<BasePrepaidExpendLogVo> prepaidExpendList(PrepaidQueryForm form) {
    if (StringHelper.isNotEmpty(form.getEndDate())) {
      String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
      form.setEndDate(endDate);
    }
    if (form.getWhetherPage()) {
      PageHelper.startPage(form.getPageNum(), form.getPageSize());
    }
    List<BasePrepaidExpendLogVo> basePrepaidExpendLogVoList = mapper.selectPrepaidExpendList(form);
    return new PageInfo<>(basePrepaidExpendLogVoList);
  }

  /**
   * 导出预付款消费列表
   *
   * @param response 导出响应
   * @param form 条件
   */
  public void exportPrepaidExpendList(HttpServletResponse response, PrepaidQueryForm form)
      throws IOException {
    if (StringHelper.isNotEmpty(form.getEndDate())) {
      String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
      form.setEndDate(endDate);
    }
    List<BasePrepaidExpendLogVo> basePrepaidExpendLogVoList = mapper.selectPrepaidExpendList(form);
    ExcelUtil<BasePrepaidExpendLogVo> excelUtil = new ExcelUtil<>(BasePrepaidExpendLogVo.class);
    if (StringHelper.isNotNull(form.getOrgId())) {
      BaseOrganization baseOrganization = new BaseOrganization();
      baseOrganization.setOrgId(form.getOrgId());
      BaseOrganization baseOrganizationv = baseOrganizationMapper.selectOne(baseOrganization);
      if (baseOrganizationv != null) {
        excelUtil.exportExcel(
            response,
            basePrepaidExpendLogVoList,
            "预付款消费记录表",
            baseOrganizationv.getAbbreviation() + "预付款消费记录表");
      } else {
        excelUtil.exportExcel(response, basePrepaidExpendLogVoList, "预付款消费记录表", "预付款消费记录表");
      }
    } else {
      excelUtil.exportExcel(response, basePrepaidExpendLogVoList, "预付款消费记录表", "预付款消费记录表");
    }
  }

  /**
   * 预付款退款查询
   *
   * @param form 预付款退款form
   * @return 预付款退款记录
   */
  public PageInfo<BasePrepaidReturnLogVo> prepaidReturnList(PrepaidQueryForm form) {
    if (StringHelper.isNotEmpty(form.getEndDate())) {
      String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
      form.setEndDate(endDate);
    }
    if (form.getWhetherPage()) {
      PageHelper.startPage(form.getPageNum(), form.getPageSize());
    }
    List<BasePrepaidReturnLogVo> basePrepaidReturnLogVoList = mapper.selectPrepaidReturnList(form);
    return new PageInfo<>(basePrepaidReturnLogVoList);
  }

  /**
   * 导出预付款退款列表
   *
   * @param response 导出响应
   * @param form 条件
   */
  public void exportPrepaidReturnList(HttpServletResponse response, PrepaidQueryForm form)
      throws IOException {
    if (StringHelper.isNotEmpty(form.getEndDate())) {
      String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
      form.setEndDate(endDate);
    }
    List<BasePrepaidReturnLogVo> basePrepaidReturnLogVoList = mapper.selectPrepaidReturnList(form);
    ExcelUtil<BasePrepaidReturnLogVo> excelUtil = new ExcelUtil<>(BasePrepaidReturnLogVo.class);
    if (StringHelper.isNotNull(form.getOrgId())) {
      BaseOrganization baseOrganization = new BaseOrganization();
      baseOrganization.setOrgId(form.getOrgId());
      BaseOrganization baseOrganizationv = baseOrganizationMapper.selectOne(baseOrganization);
      if (baseOrganizationv != null) {
        excelUtil.exportExcel(
            response,
            basePrepaidReturnLogVoList,
            "预付款退款记录表",
            baseOrganizationv.getAbbreviation() + "预付款退费记录表");
      } else {
        excelUtil.exportExcel(response, basePrepaidReturnLogVoList, "预付款退款记录表", "预付款退费记录表");
      }
    } else {
      excelUtil.exportExcel(response, basePrepaidReturnLogVoList, "预付款退款记录表", "预付款退费记录表");
    }
  }

  /**
   * 查询所有门诊信息
   *
   * @return List<BaseOrganization>
   */
  public List<BaseOrganization> orgList() {
    return baseOrganizationMapper.selectAll();
  }

  /**
   * 查询会员or预付款余额结存信息列表
   *
   * @param form 条件
   * @return 查询会员or预付款余额结存信息列表
   */
  public PageInfo<BaseMemberBalanceInfoVo> memberBalanceList(MemberQueryForm form) {
    if (StringHelper.isNotEmpty(form.getEndDate())) {
      String endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
      form.setEndDate(endDate);
    }
    if (form.getWhetherPage()) {
      PageHelper.startPage(form.getPageNum(), form.getPageSize());
    }
    List<BaseMemberBalanceInfoVo> basePrepaidReturnLogVoList = mapper.selectMemberBalanceList(form);
    return new PageInfo<>(basePrepaidReturnLogVoList);
  }
  /*

  * */

  /**
   * 导出会员余/预付款余额结存信息记录列表
   *
   * @param response 导出响应
   * @param form 条件
   */
  public void exportMemberBalanceList(HttpServletResponse response, MemberQueryForm form)
      throws IOException {
    String endDate = form.getEndDate();
    String formEndDate = form.getEndDate();
    if (StringHelper.isNotEmpty(endDate)) {
      endDate = new DateTime(form.getEndDate()).plusDays(1).toString("yyyy-MM-dd");
      form.setEndDate(endDate);
    }
    List<BaseMemberBalanceInfoVo> resultList = mapper.selectMemberBalanceList(form);
    if (form.getType() == 0) {
      ExcelUtil<ExcelBaseMemberBalanceInfoVo> excelUtil =
          new ExcelUtil<>(ExcelBaseMemberBalanceInfoVo.class);
      List<ExcelBaseMemberBalanceInfoVo> build =
          EntityUtils.build(resultList, ExcelBaseMemberBalanceInfoVo.class);
      excelUtil.exportExcel(
          response, build, "会员余额结存表", (form.getStartDate() + "-" + formEndDate) + "会员余额结存表");
    } else {
      ExcelUtil<ExcelBasePrepaymentsBalanceInfoVo> excelUtil =
          new ExcelUtil<>(ExcelBasePrepaymentsBalanceInfoVo.class);
      List<ExcelBasePrepaymentsBalanceInfoVo> build =
          EntityUtils.build(resultList, ExcelBasePrepaymentsBalanceInfoVo.class);
      excelUtil.exportExcel(
          response, build, "预付款余额结存表", (form.getStartDate() + "-" + formEndDate) + "预付款余额结存表");
    }
  }

  /**
   * 根据条件查询患者储值卡（会员卡/预付款卡）充值记录信息列表
   *
   * @param query 查询条件
   * @return 患者储值卡（会员卡/预付款卡）充值记录信息列表
   */
  public PageInfo<StatementPatientCardRechargeDetailVO> findPatientCardRechargeDetailList(
      StatementPatientCardRechargeDetailInfoQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<StatementPatientCardRechargeDetailVO> resultList =
        mapper.selectPatientCardRechargeDetailList(query);
    if (StringHelper.isNotEmpty(resultList)) {
      for (StatementPatientCardRechargeDetailVO vo : resultList) {
        Integer rechargeRecordId = vo.getRechargeRecordId();
        StatementPaymentVO statementPaymentVO =
            mapper.selectStatementPaymentByOperateRecordId(
                (byte) 0, rechargeRecordId, query.getCardType(), (byte) 1);
        // 设置充值记录支付方式金额
        if (null != statementPaymentVO) {
          setRechargeDetailAmountValue(statementPaymentVO, vo);
        }
      }
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 设置充值记录支付方式金额
   *
   * @param statementPaymentVO 充值记录支付方式
   * @param vo 充值记录明细
   */
  private void setRechargeDetailAmountValue(
      StatementPaymentVO statementPaymentVO, StatementPatientCardRechargeDetailVO vo) {
    String accountItemName = statementPaymentVO.getAccountItemName();
    BigDecimal totalAmount = statementPaymentVO.getTotalAmount();
    switch (accountItemName) {
      case ACCOUNT_ITEM_OF_CASH:
        vo.setCashAmount(totalAmount);
        break;
      case ACCOUNT_ITEM_OF_WECHAT:
        vo.setWeChatAmount(totalAmount);
        break;
      case ACCOUNT_ITEM_OF_ALIPAY:
        vo.setAliPayAmount(totalAmount);
        break;
      case ACCOUNT_ITEM_OF_BANK:
        vo.setBankAmount(totalAmount);
        break;
      default:
        break;
    }
  }

  /**
   * 根据条件导出门诊患者储值卡充值记录明细
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportMemberRechargeDetailList(
      HttpServletResponse response, StatementPatientCardRechargeDetailInfoQuery query)
      throws IOException {
    PageInfo<StatementPatientCardRechargeDetailVO> pageInfo =
        findPatientCardRechargeDetailList(query);
    List<StatementPatientCardRechargeDetailVO> list = pageInfo.getList();
    ExcelUtil<StatementPatientCardRechargeDetailVO> excelUtil =
        new ExcelUtil<>(StatementPatientCardRechargeDetailVO.class);
    String fileName = "门诊患者储值卡充值记录明细列表";
    BaseOrganization organization = baseOrganizationMapper.selectByPrimaryKey(query.getOrgId());
    if (null != organization) {
      fileName =
          MessageFormat.format(
              "{0}{1}-{2}{3}",
              organization.getAbbreviation(), query.getStartDate(), query.getEndDate(), fileName);
    }
    excelUtil.exportExcel(response, list, "门诊患者储值卡充值记录明细", fileName);
  }

  /**
   * 根据条件查询患者储值卡（会员卡、预付款）退费明细记录列表
   *
   * @param query 查询条件
   * @return 患者储值卡（会员卡、预付款）退费明细记录列表
   */
  public PageInfo<StatementPatientCardRefundDetailVO> findPatientCardRefundDetailList(
      StatementPatientCardRefundDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<StatementPatientCardRefundDetailVO> resultList =
        mapper.selectPatientCardRefundDetailList(query);
    if (StringHelper.isNotEmpty(resultList)) {
      for (StatementPatientCardRefundDetailVO vo : resultList) {
        Integer refundRecordId = vo.getRefundRecordId();
        StatementPaymentVO statementPaymentVO =
            mapper.selectStatementPaymentByOperateRecordId(
                null, refundRecordId, query.getCardType(), (byte) 3);
        if (null != statementPaymentVO) {
          setRefundDetailAmountValue(statementPaymentVO, vo);
        }
      }
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 设置患者储值卡退费支付方式明细
   *
   * @param statementPaymentVO 退费支付方式
   * @param vo 退费明细
   */
  private void setRefundDetailAmountValue(
      StatementPaymentVO statementPaymentVO, StatementPatientCardRefundDetailVO vo) {
    String accountItemName = statementPaymentVO.getAccountItemName();
    BigDecimal totalAmount = statementPaymentVO.getTotalAmount();
    switch (accountItemName) {
      case ACCOUNT_ITEM_OF_CASH:
        vo.setCashAmount(totalAmount);
        break;
      case ACCOUNT_ITEM_OF_WECHAT:
        vo.setWeChatAmount(totalAmount);
        break;
      case ACCOUNT_ITEM_OF_ALIPAY:
        vo.setAliPayAmount(totalAmount);
        break;
      case ACCOUNT_ITEM_OF_BANK:
        vo.setBankAmount(totalAmount);
        break;
      default:
        break;
    }
  }

  /**
   * 根据条件导出门诊患者储值卡充值记录明细
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportPatientCardRefundDetailList(
      HttpServletResponse response, StatementPatientCardRefundDetailQuery query)
      throws IOException {
    PageInfo<StatementPatientCardRefundDetailVO> pageInfo = findPatientCardRefundDetailList(query);
    List<StatementPatientCardRefundDetailVO> list = pageInfo.getList();
    ExcelUtil<StatementPatientCardRefundDetailVO> excelUtil =
        new ExcelUtil<>(StatementPatientCardRefundDetailVO.class);
    String fileName = "门诊患者储值卡退费记录明细列表";
    BaseOrganization organization = baseOrganizationMapper.selectByPrimaryKey(query.getOrgId());
    if (null != organization) {
      fileName =
          MessageFormat.format(
              "{0}{1}-{2}{3}",
              organization.getAbbreviation(), query.getStartDate(), query.getEndDate(), fileName);
    }
    excelUtil.exportExcel(response, list, "患者储值卡退费记录明细", fileName);
  }
}
