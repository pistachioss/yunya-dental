package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BaseOrganization;
import com.yunya.models.report.BaseRefund;
import com.yunya.report.ultimate.mapper.BaseOrganizationMapper;
import com.yunya.report.ultimate.mapper.BaseRefundMapper;
import com.yunya.report.ultimate.mapper.BaseRefundPayDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static com.yunya.framework.common.constant.BusinessConstants.*;

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

  /** 组织 */
  @Autowired private BaseOrganizationMapper organizationMapper;
  /** 账单退费付款明细 */
  @Autowired private BaseRefundPayDetailMapper refundPayDetailMapper;

  /**
   * 根据条件查询账单退费记录列表
   *
   * @param query 查询条件
   * @return PageInfo<BillOfRefundRecordVO>
   */
  public PageInfo<BillOfRefundRecordInfoVO> findBillRefundRecord(BillRefundRecordQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<BillOfRefundRecordInfoVO> resultList = mapper.selectBillRefundRecord(query);
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
    List<BillOfRefundRecordInfoVO> list = mapper.selectBillRefundRecord(query);
    ExcelUtil<BillOfRefundRecordInfoVO> excelUtil = new ExcelUtil<>(BillOfRefundRecordInfoVO.class);
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
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    String abbreviation = "";
    if (null != organization) {
      abbreviation = organization.getAbbreviation();
    }
    String fileName =
        excelUtil.getFileName(
            query.getOrderDate(), query.getQueryDate(), abbreviation, "退费工作量统计明细表");
    excelUtil.exportExcel(response, list, "员工账单退费明细表", fileName);
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

  /**
   * 根据条件查询门诊账单退费（本月）明细列表
   *
   * @param query 查询条件
   * @return
   */
  public PageInfo<StatementBillRefundDetailVO> findCurrentBillRefundDetailList(
      StatementBillRefundDetailInfoQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<StatementBillRefundDetailVO> resultList = mapper.selectCurrentBillRefundDetailList(query);
    if (StringHelper.isNotEmpty(resultList)) {
      generateBillRefundAccountItemValue(resultList);
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出诊所账单退费（本月）明细列表
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportCurrentBillRefundDetailList(
      HttpServletResponse response, StatementBillRefundDetailInfoQuery query) throws IOException {
    PageInfo<StatementBillRefundDetailVO> pageInfo = findCurrentBillRefundDetailList(query);
    List<StatementBillRefundDetailVO> list = pageInfo.getList();
    ExcelUtil<StatementBillRefundDetailVO> excelUtil =
        new ExcelUtil<>(StatementBillRefundDetailVO.class);
    String fileName = "诊所账单退费（本月）明细列表";
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    if (null != organization) {
      fileName =
          String.format(
              "%s%s-%s%s",
              organization.getAbbreviation(), query.getStartDate(), query.getEndDate(), fileName);
    }
    excelUtil.exportExcel(response, list, "诊所账单退费（本月）明细列表", fileName);
  }

  /**
   * 根据条件查询门诊账单退费（非本月）明细列表
   *
   * @param query 查询条件
   * @return
   */
  public PageInfo<StatementBillRefundDetailVO> findOtherBillRefundDetailList(
      StatementBillRefundDetailInfoQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<StatementBillRefundDetailVO> resultList = mapper.selectOtherBillRefundDetailList(query);
    if (StringHelper.isNotEmpty(resultList)) {
      generateBillRefundAccountItemValue(resultList);
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出诊所账单退费（非本月）明细列表
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportOtherBillRefundDetailList(
      HttpServletResponse response, StatementBillRefundDetailInfoQuery query) throws IOException {
    PageInfo<StatementBillRefundDetailVO> pageInfo = findOtherBillRefundDetailList(query);
    List<StatementBillRefundDetailVO> list = pageInfo.getList();
    ExcelUtil<StatementBillRefundDetailVO> excelUtil =
        new ExcelUtil<>(StatementBillRefundDetailVO.class);
    String fileName = "诊所账单退费（非本月）明细列表";
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    if (null != organization) {
      fileName =
          String.format(
              "%s%s-%s%s",
              organization.getAbbreviation(), query.getStartDate(), query.getEndDate(), fileName);
    }
    excelUtil.exportExcel(response, list, "诊所账单退费（非本月）明细列表", fileName);
  }

  /**
   * 构建账单退费付款方式信息
   *
   * @param resultList 账单退费记录列表
   */
  private void generateBillRefundAccountItemValue(List<StatementBillRefundDetailVO> resultList) {
    for (StatementBillRefundDetailVO vo : resultList) {
      Integer refundId = vo.getRefundId();
      List<StatementPaymentVO> payDetails =
          refundPayDetailMapper.selectBillRefundPaymentList(refundId);
      if (StringHelper.isNotEmpty(payDetails)) {
        for (StatementPaymentVO payment : payDetails) {
          String accountItemName = payment.getAccountItemName();
          BigDecimal totalAmount = payment.getTotalAmount();
          switch (accountItemName) {
            case ACCOUNT_ITEM_OF_MEMBER:
              vo.setMemberPrincipleAmount(totalAmount);
              vo.setMemberBonusAmount(payment.getBonusAmount());
              break;
            case ACCOUNT_ITEM_OF_PREPARE:
              vo.setPrepaidPrincipleAmount(totalAmount);
              vo.setPrepaidBonusAmount(payment.getBonusAmount());
              break;
            case ACCOUNT_ITEM_OF_CASH:
              vo.setCashAmount(totalAmount);
              break;
            case ACCOUNT_ITEM_OF_ALIPAY:
              vo.setAliPayAmount(totalAmount);
              break;
            case ACCOUNT_ITEM_OF_WECHAT:
              vo.setWeChatAmount(totalAmount);
              break;
            case ACCOUNT_ITEM_OF_BANK:
              vo.setBankAmount(totalAmount);
              break;
            default:
              break;
          }
        }
      }
    }
  }
}
