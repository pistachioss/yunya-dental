package com.yunya.modules.clinic_base.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Joiner;
import com.yunya.feign.clinic_base.domain.form.CashBalanceForm;
import com.yunya.feign.clinic_base.domain.model.CashBalanceModel;
import com.yunya.feign.clinic_base.domain.query.CashBalanceQuery;
import com.yunya.feign.clinic_base.domain.query.PeriodCashQuery;
import com.yunya.feign.clinic_base.domain.vo.CashBalanceDetailVO;
import com.yunya.feign.clinic_base.domain.vo.CashBalanceVO;
import com.yunya.feign.discount.RemoteDiscountFeign;
import com.yunya.feign.discount.domain.form.CertificatesForm;
import com.yunya.feign.patient_central.RemotePatientCentralServiceFeign;
import com.yunya.feign.treatment.RemoteTreatmentServiceFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.clinic_base.CashBalance;
import com.yunya.modules.clinic_base.mapper.CashBalanceMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.yunya.framework.common.constant.OperationCodeConstants.PARAMETERS_IS_ILLEGAL;

/**
 * 简介: 现金结存业务层
 *
 * @author: chow
 * @date: 2020/12/18 16:34
 * @description:
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CashBalanceBiz extends BaseBiz<CashBalanceMapper, CashBalance> {

  /** 折扣服务调用 */
  @Autowired private RemoteDiscountFeign discountFeign;
  /** 患者服务调用 */
  @Autowired private RemotePatientCentralServiceFeign patientCentralServiceFeign;
  /** 就诊收费服务调用 */
  @Autowired private RemoteTreatmentServiceFeign treatmentServiceFeign;

  /**
   * 获取门诊新增期初现金结余金额
   *
   * @param orgId 组织ID
   * @return BigDecimal
   */
  public BigDecimal findBeginningCash(Integer orgId) {
    CashBalance balance = mapper.selectRecentCashBalance(orgId);
    return null != balance ? balance.getEndingBalanceCash() : BigDecimal.valueOf(0);
  }

  /**
   * 获取门诊当前天期间现金收款
   *
   * @param query 查询条件
   * @return BigDecimal
   */
  public BigDecimal findPeriodCash(PeriodCashQuery query) {
    Integer orgId = query.getOrgId();
    Date settlementDate = new DateTime(query.getSettlementDate()).toDate();
    CashBalance balance = mapper.selectRecentCashBalance(orgId);
    Date startDate = null;
    if (null != balance) {
      startDate = balance.getSettlementDate();
    }
    return getPeriodCollectionCash(orgId, startDate, settlementDate);
  }

  /**
   * 新增现金结存
   *
   * @param model 新增参数
   */
  public void saveCashBalance(CashBalanceModel model) {
    Date settlementDate = new DateTime(model.getSettlementDate()).toDate();
    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    checkSettlementDate(settlementDate);
    CashBalance balance = mapper.selectRecentCashBalance(orgId);
    // 期初结余
    BigDecimal beginningBalanceCash;
    // 期间结余
    BigDecimal periodCollection;
    if (null != balance) {
      Date balanceSettlementDate = balance.getSettlementDate();
      compareSettlementDate(settlementDate, balanceSettlementDate);
      beginningBalanceCash = balance.getEndingBalanceCash();
      periodCollection = getPeriodCollectionCash(orgId, balanceSettlementDate, settlementDate);
    } else {
      beginningBalanceCash = BigDecimal.ZERO;
      periodCollection = getPeriodCollectionCash(orgId, null, settlementDate);
    }
    CashBalance entity = new CashBalance();
    entity.setOrgId(orgId);
    entity.setSettlementDate(settlementDate);
    entity.setBeginningBalanceCash(beginningBalanceCash);
    entity.setPeriodCollection(periodCollection);
    // 本日现金存款
    BigDecimal depositedCash = model.getDepositedCash();
    entity.setDepositedCash(depositedCash);
    // 期末现金结余 = 期初现金结余 + 现金收款 - 现金退费 -现金存款 + 调整差额
    BigDecimal adjustment = model.getBalanceAdjustment();
    BigDecimal endingBalanceCash =
        beginningBalanceCash.add(periodCollection).add(adjustment).subtract(depositedCash);
    entity.setEndingBalanceCash(endingBalanceCash);
    entity.setBalanceAdjustment(adjustment);
    entity.setAdjustRemark(model.getAdjustRemark());
    String[] certificates = model.getCertificates();
    if (StringHelper.isNotEmpty(certificates)) {
      Joiner joiner = Joiner.on(",");
      entity.setUri(joiner.join(model.getCertificates()));
    }
    Integer userId = Integer.valueOf(BaseContextHandler.getUserID());
    entity.setCrtId(userId);
    String name = BaseContextHandler.getName();
    entity.setCrtName(name);
    entity.setUpdId(userId);
    entity.setUpdName(name);
    mapper.insertSelective(entity);
  }

  /**
   * 获取期间现金收款（账单的现金收款，产品售卖的现金收款、会员充值的现金收款、预付款充值的现金收款之和）
   *
   * @param orgId 组织ID
   * @param startDate 最近结存日期
   * @param endDate 当前结存日期
   * @return BigDecimal
   */
  private BigDecimal getPeriodCollectionCash(Integer orgId, Date startDate, Date endDate) {

    return BigDecimal.valueOf(0);
  }

  /**
   * 比较结存日期与系统最近结存日期
   *
   * @param settlementDate 结存日期
   * @param lastSettlementDate 系统最近结存日期
   */
  private void compareSettlementDate(Date settlementDate, Date lastSettlementDate) {
    if (settlementDate.compareTo(lastSettlementDate) <= 0) {
      throw new ClientServiceException(
          "新增失败，当前结存日期不能早于或等于系统最近的结存日期：" + lastSettlementDate, PARAMETERS_IS_ILLEGAL);
    }
  }

  /**
   * 校验结存日期
   *
   * @param settlementDate 结存日期
   */
  private void checkSettlementDate(Date settlementDate) {
    if (settlementDate.after(new Date())) {
      throw new ClientServiceException("新增失败，结存日期不能晚于当前天！", PARAMETERS_IS_ILLEGAL);
    }
  }

  /**
   * 根据条件查询现金结存列表
   *
   * @param query 查询条件
   * @return PageInfo<CashBalanceVO>
   */
  public PageInfo<CashBalanceVO> findCashBalanceList(CashBalanceQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<CashBalanceVO> resultList = mapper.selectCashBalanceList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 导出门诊现金结存列表
   *
   * @param response http响应
   * @param query 查询条件
   * @throws IOException
   */
  public void exportBalanceList(HttpServletResponse response, CashBalanceQuery query)
      throws IOException {
    ExcelUtil<CashBalanceVO> excelUtil = new ExcelUtil<>(CashBalanceVO.class);
    List<CashBalanceVO> resultList = mapper.selectCashBalanceList(query);
    excelUtil.exportExcel(response, resultList, "现金结存列表");
  }

  /**
   * 根据结存记录ID查询现金结存详情
   *
   * @param id 结存记录ID
   * @return CashBalanceDetailVO
   */
  public CashBalanceDetailVO findCashBalanceDetailById(Integer id) {
    CashBalanceDetailVO resultData = new CashBalanceDetailVO();
    CashBalance balance = mapper.selectByPrimaryKey(id);
    if (null != balance) {
      resultData.setId(balance.getId());
      resultData.setSettlementDate(
          new DateTime(balance.getSettlementDate()).toString("yyyy-MM-dd"));
      resultData.setBeginningBalanceCash(balance.getBeginningBalanceCash());
      resultData.setPeriodCollectionCash(balance.getPeriodCollection());
      resultData.setDepositedCash(balance.getDepositedCash());
      resultData.setBalanceAdjustmentCash(balance.getBalanceAdjustment());
      resultData.setBalanceAdjustmentRemark(balance.getAdjustRemark());
      resultData.setEndingBalanceCash(balance.getEndingBalanceCash());
      String uri = balance.getUri();
      if (StringHelper.isNotBlank(uri)) {
        String[] certificates = uri.split(",");
        resultData.setCertificates(certificates);
      }
    }
    return resultData;
  }

  /**
   * 根据ID修改现金结存记录
   *
   * @param id 结存记录ID
   * @param form 修改参数
   */
  public void modify(Integer id, CashBalanceForm form) {
    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    CashBalance balance = mapper.selectRecentCashBalance(orgId);
    if (null != balance) {
      Integer balanceId = balance.getId();
      if (!id.equals(balanceId)) {
        throw new ClientServiceException("修改失败，只允许修改最近的一条现金结存记录！", PARAMETERS_IS_ILLEGAL);
      }
      Date settlementDate = new DateTime(form.getSettlementDate()).toDate();
      Date lastSettlementDate = balance.getSettlementDate();
      BigDecimal periodCollectionCash = balance.getPeriodCollection();
      if (!settlementDate.equals(lastSettlementDate)) {
        checkSettlementDate(settlementDate);
        compareSettlementDate(settlementDate, lastSettlementDate);
        // 重新计算期间现金收款
        periodCollectionCash = getPeriodCollectionCash(orgId, lastSettlementDate, settlementDate);
        balance.setPeriodCollection(periodCollectionCash);
      }
      balance.setSettlementDate(settlementDate);
      BigDecimal depositedCash = form.getDepositedCash();
      balance.setDepositedCash(depositedCash);
      BigDecimal balanceAdjustment = form.getBalanceAdjustment();
      balance.setBalanceAdjustment(balanceAdjustment);
      BigDecimal endingBalanceCash =
          balance
              .getBeginningBalanceCash()
              .add(periodCollectionCash)
              .add(balanceAdjustment)
              .subtract(depositedCash);
      balance.setEndingBalanceCash(endingBalanceCash);
      balance.setAdjustRemark(form.getAdjustRemark());
      String[] certificates = form.getCertificates();
      if (StringHelper.isNotEmpty(certificates)) {
        Joiner joiner = Joiner.on(",");
        balance.setUri(joiner.join(form.getCertificates()));
      }
      balance.setUpdId(Integer.valueOf(BaseContextHandler.getUserID()));
      balance.setUpdName(BaseContextHandler.getName());
      mapper.updateByPrimaryKeySelective(balance);
    } else {
      throw new ClientServiceException("修改失败，请选择正确的结存记录进行修改！", PARAMETERS_IS_ILLEGAL);
    }
  }

  /**
   * 更新现金存款结存凭证
   *
   * @param form 更新参数
   */
  public void updateCashBalanceCertificates(CertificatesForm form) {
    Integer id = form.getId();
    CashBalance balance = mapper.selectByPrimaryKey(id);
    if (null == balance) {
      throw new ClientServiceException("更新失败，请选择正确的结存记录进行更新！", PARAMETERS_IS_ILLEGAL);
    }
    String[] certificates = form.getCertificates();
    if (StringHelper.isNotEmpty(certificates)) {
      Joiner joiner = Joiner.on(",");
      balance.setUri(joiner.join(certificates));
    }
    mapper.updateByPrimaryKeySelective(balance);
  }

  /**
   * 根据ID删除现金结存记录
   *
   * @param id 结存记录ID
   */
  public void deleteCashBalance(Integer id) {
    Integer orgId = Integer.valueOf(BaseContextHandler.getOrgId());
    CashBalance balance = mapper.selectRecentCashBalance(orgId);
    if (null != balance) {
      Integer balanceId = balance.getId();
      if (balanceId.equals(id)) {
        mapper.deleteByPrimaryKey(id);
      } else {
        throw new ClientServiceException("删除失败，只允许删除最近的一条结存记录！", PARAMETERS_IS_ILLEGAL);
      }
    }
  }
}
