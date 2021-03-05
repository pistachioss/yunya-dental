package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.BusinessConstants;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.report.BaseBillDetail;
import com.yunya.models.report.BaseBillPayDetail;
import com.yunya.models.report.BaseOrganization;
import com.yunya.models.system.AccountItem;
import com.yunya.report.ultimate.mapper.BaseBillDetailMapper;
import com.yunya.report.ultimate.mapper.BaseBillPayDetailMapper;
import com.yunya.report.ultimate.mapper.BaseOrganizationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 简介: 账单开单详情业务层
 *
 * @author: chow
 * @date: 2020/10/29 11:18
 * @description:
 * @since: 1.0.0
 */
@Service
public class BaseBillDetailBiz extends BaseBiz<BaseBillDetailMapper, BaseBillDetail> {

  /** 组织 */
  @Autowired private BaseOrganizationMapper organizationMapper;
  /** 账单收费明细 */
  @Autowired private BaseBillPayDetailMapper baseBillPayDetailMapper;
  /** 缓存 */
  @Autowired private RedisUtils redisUtils;
  /** 系统 */
  @Autowired private RemoteSystemServiceFeign systemServiceFeign;

  /**
   * 根据条件查询账单收入详情列表
   *
   * @param query 查询条件
   * @return list
   */
  public PageInfo<BillTariffIncomeDetailVO> findBillDetailIncomeList(
      BillDetailIncomeDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<BillTariffIncomeDetailVO> resultList = mapper.selectBillDetailIncomeList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 导出项目收入明细列表
   *
   * @param response 响应
   * @param query 查询条件
   * @throws IOException
   */
  public void exportBillDetailIncome(
      HttpServletResponse response, BillDetailIncomeDetailQuery query) throws IOException {
    String fileName = "门诊项目收入明细";
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    if (null != organization) {
      fileName = organization.getAbbreviation() + fileName;
    }
    List<BillTariffIncomeDetailVO> list = mapper.selectBillDetailIncomeList(query);
    ExcelUtil<BillTariffIncomeDetailVO> excelUtil = new ExcelUtil<>(BillTariffIncomeDetailVO.class);
    excelUtil.exportExcel(response, list, fileName, fileName);
  }

  /**
   * 根据条件查询员工工作量报表
   *
   * @param query 查询条件
   * @return
   */
  public PageInfo<EmployeeWorkloadOfPersonnelVO> findEmployeeWorkloadListOfPersonnel(
      EmployeeWorkloadQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<EmployeeWorkloadOfPersonnelVO> resultList =
        mapper.selectEmployeeWorkloadListOfPersonnel(query);
    if (StringHelper.isNotEmpty(resultList)) {
      resultList.forEach(
          vo -> {
            // 奖金系数
            BigDecimal bonusCoefficient = vo.getBonusCoefficient();
            // 补入工作量
            BigDecimal supplementWorkload = vo.getSupplementWorkload();
            // 实收工作量
            BigDecimal actualWorkload = vo.getActualWorkload();
            // 退费工作量
            BigDecimal refundWorkload = vo.getRefundWorkload();
            // 加工费
            BigDecimal processingFee = vo.getProcessingFee();
            // 大额材料费
            BigDecimal largeMaterialCost = vo.getLargeMaterialCost();
            // 基础工作量
            BigDecimal baseWorkload = vo.getBaseWorkload();
            // 已收工作量
            BigDecimal receivedWorkload = vo.getReceivedWorkload();
            BigDecimal actualBonusBase =
                actualWorkload
                    .add(supplementWorkload)
                    .subtract(refundWorkload)
                    .subtract(processingFee)
                    .subtract(largeMaterialCost)
                    .subtract(baseWorkload);
            vo.setActualBonusBase(actualBonusBase);
            vo.setActualBonus(actualBonusBase.multiply(bonusCoefficient));
            BigDecimal receivedBonusBase =
                receivedWorkload
                    .add(supplementWorkload)
                    .subtract(refundWorkload)
                    .subtract(processingFee)
                    .subtract(largeMaterialCost)
                    .subtract(baseWorkload);
            vo.setReceivedBonusBase(receivedBonusBase);
            vo.setReceivedBonus(receivedBonusBase.multiply(bonusCoefficient));
          });
      assemblyFreepayment(resultList);
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件查询员工工作量列表（运营报表）
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeWorkloadOfOperationVO>
   */
  public PageInfo<EmployeeWorkloadOfOperationVO> findEmployeeWorkloadListOfOperation(
      EmployeeWorkloadQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<EmployeeWorkloadOfOperationVO> resultList =
        mapper.selectEmployeeWorkloadListOfOperation(query);

    // 免单支付工作量
    if (StringHelper.isNotEmpty(resultList)) {
      assemblyFreepayment(resultList);
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 查询并装配员工执行的免单支付总额
   *
   * @param resultList
   */
  private void assemblyFreepayment(List<? extends EmployeeWorkloadOfOperationVO> resultList) {
    Map<Integer, BigDecimal> userIds = resultList.stream().collect(Collectors.toMap(EmployeeWorkloadOfOperationVO::getEmployeeId,v->BigDecimal.ZERO));
    List<BaseBillDetail> details = mapper.selectBillDetailByExecutorIds(userIds.keySet());
    Map<Integer, BigDecimal> total = new HashMap<>(16);//每个账单的执行实收总额
    Set<Integer> billIds = new HashSet<>();
    details.forEach(detail -> {
      Integer billId = detail.getBillId();
      BigDecimal sum = total.get(billId);
      if (sum == null) {
        sum = BigDecimal.ZERO;
      }
      total.put(billId, sum.add(detail.getReceivedAmount()));
      billIds.add(billId);
    });

    List<BaseBillPayDetail> freePayments = baseBillPayDetailMapper.sumPayDetailList(billIds, getFreePaymentIds());
    Map<Integer, BigDecimal> freePaymentMap = freePayments.stream().collect(Collectors.toMap(BaseBillPayDetail::getBillId, BaseBillPayDetail::getPrincipalAmount));
    details.forEach(detail -> {
      Integer executorId = detail.getExecutorId();
      if (userIds.containsKey(executorId)) {
        Integer billId = detail.getBillId();
        BigDecimal sum = total.get(billId);
        BigDecimal amount = detail.getReceivedAmount()
                .divide(sum, 2, BigDecimal.ROUND_HALF_UP)
                .multiply(freePaymentMap.get(billId));
        BigDecimal frees = userIds.get(executorId);
        userIds.put(executorId, amount.add(frees));
      }
    });
    resultList.forEach(vo -> {
      Integer employeeId = vo.getEmployeeId();
      vo.setFreePaymentWorkload(userIds.get(employeeId));
    });
  }

  /**
   * 获取免单支付的支付id列表
   * @return
   */
  protected Set<Integer> getFreePaymentIds() {
    Set<Integer> payIds = new HashSet<>();
    List<AccountItem> accountItems = redisUtils.getJSONArray(RedisConstants.REDIS_KEY_ACCOUNT_ITEM_LIST, AccountItem.class);
    if (StringHelper.isEmpty(accountItems)) {
      AccountItem model = new AccountItem();
      model.setAccountTypeId(BusinessConstants.FREE_PAYMENT_ID);
      model.setInservice(true);
      accountItems = systemServiceFeign.findAccountItemList(model);
      redisUtils.set(RedisConstants.REDIS_KEY_ACCOUNT_ITEM_LIST, accountItems);
    }
    accountItems.forEach(item -> {
      Integer acountTypeId = item.getAccountTypeId();
      if (BusinessConstants.FREE_PAYMENT_ID.equals(acountTypeId)) {
        payIds.add(item.getId());
      }
    });
    if (StringHelper.isEmpty(payIds)) {
      payIds.add(-1);
    }
    return payIds;
  }

  /**
   * 根据条件导出员工工作量列表（人事报表）
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportEmployeeWorkloadListOfPersonnel(
      HttpServletResponse response, EmployeeWorkloadQuery query) throws IOException {
    query.setWhetherPage(false);
    PageInfo<EmployeeWorkloadOfPersonnelVO> workloadList =
        findEmployeeWorkloadListOfPersonnel(query);
    List<EmployeeWorkloadOfPersonnelVO> resultList = workloadList.getList();
    ExcelUtil<EmployeeWorkloadOfPersonnelVO> excelUtil =
        new ExcelUtil<>(EmployeeWorkloadOfPersonnelVO.class);
    String fileName = query.getQueryDate() + query.getQueryDate() + "员工工作量统计";
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    if (null != organization) {
      fileName = organization.getAbbreviation() + fileName;
    }
    excelUtil.exportExcel(response, resultList, "员工工作量（人事报表）", fileName);
  }

  /**
   * 根据条件导出员工工作量列表（运营报表）
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportEmployeeWorkloadListOfOperation(
      HttpServletResponse response, EmployeeWorkloadQuery query) throws IOException {
    query.setWhetherPage(false);
    PageInfo<EmployeeWorkloadOfOperationVO> workloadList =
        findEmployeeWorkloadListOfOperation(query);
    List<EmployeeWorkloadOfOperationVO> resultList = workloadList.getList();
    ExcelUtil<EmployeeWorkloadOfOperationVO> excelUtil =
        new ExcelUtil<>(EmployeeWorkloadOfOperationVO.class);
    String fileName = query.getQueryDate() + "员工工作量统计";
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    if (null != organization) {
      fileName = organization.getAbbreviation() + fileName;
    }
    excelUtil.exportExcel(response, resultList, "员工工作量（运营报表）", fileName);
  }

  /**
   * 根据条件查询项目分类收入汇总列表
   *
   * @param query 查询条件
   * @return PageInfo<CategoryInfoIncomeVO>
   */
  public PageInfo<CategoryInfoIncomeVO> findCategoryIncomeList(BillCategoryIncomeQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<CategoryInfoIncomeVO> resultList = mapper.selectCategoryIncomeList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 公司端报表-财务报表-分类收入汇总-导出
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportCategoryIncome(HttpServletResponse response, BillCategoryIncomeQuery query)
      throws IOException {
    List<CategoryInfoIncomeVO> list = mapper.selectCategoryIncomeList(query);
    ExcelUtil<CategoryInfoIncomeVO> excelUtil = new ExcelUtil<>(CategoryInfoIncomeVO.class);
    excelUtil.exportExcel(response, list, "门诊分类收入汇总", "门诊分类收入汇总");
  }

  /**
   * 根据条件查询员工个人实收工作量明细列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeePersonalActualWorkloadDetailVO>
   */
  public PageInfo<EmployeePersonalActualWorkloadDetailVO>
      findEmployeePersonalActualWorkloadDetailList(EmployeePersonalWorkloadDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<EmployeePersonalActualWorkloadDetailVO> resultList =
        mapper.selectEmployeePersonalActualWorkloadDetailList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出员工个人实收工作量明细列表
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportEmployeePersonalActualWorkloadDetailList(
      HttpServletResponse response, EmployeePersonalWorkloadDetailQuery query) throws IOException {
    query.setWhetherPage(false);
    PageInfo<EmployeePersonalActualWorkloadDetailVO> pageInfo =
        findEmployeePersonalActualWorkloadDetailList(query);
    List<EmployeePersonalActualWorkloadDetailVO> resultList = pageInfo.getList();
    ExcelUtil<EmployeePersonalActualWorkloadDetailVO> excelUtil =
        new ExcelUtil<>(EmployeePersonalActualWorkloadDetailVO.class);
    String fileName = query.getOrderDate() + "实收工作量统计明细表";
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    if (null != organization) {
      fileName = organization.getAbbreviation() + fileName;
    }
    excelUtil.exportExcel(response, resultList, "员工个人实收工作量明细列表", fileName);
  }

  /**
   * 根据条件查询员工工作量开单明细列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeOrderDetailWorkloadVO>
   */
  public PageInfo<EmployeeOrderDetailWorkloadVO> findOrderDetailWorkloadList(
      EmployeeWorkloadDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<EmployeeOrderDetailWorkloadVO> resultList =
        mapper.selectEmployeeOrderDetailWorkloadList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件查询员工个人已收工作量明细列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeePersonalReceivedWorkloadDetailVO>
   */
  public PageInfo<EmployeePersonalReceivedWorkloadDetailVO>
      findEmployeePersonalReceivedWorkloadDetailList(EmployeePersonalWorkloadDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<EmployeePersonalReceivedWorkloadDetailVO> resultList =
        mapper.selectEmployeePersonalReceivedWorkloadDetailList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出员工个人已收工作量明细列表
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportEmployeePersonalReceivedWorkloadDetailList(
      HttpServletResponse response, EmployeePersonalWorkloadDetailQuery query) throws IOException {
    query.setWhetherPage(false);
    PageInfo<EmployeePersonalReceivedWorkloadDetailVO> pageInfo =
        findEmployeePersonalReceivedWorkloadDetailList(query);
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    String fileName = query.getQueryDate() + "已收工作量统计明细表";
    if (null != organization) {
      fileName = organization.getAbbreviation();
    }
    List<EmployeePersonalReceivedWorkloadDetailVO> resultList = pageInfo.getList();
    ExcelUtil<EmployeePersonalReceivedWorkloadDetailVO> excelUtil =
        new ExcelUtil<>(EmployeePersonalReceivedWorkloadDetailVO.class);
    excelUtil.exportExcel(response, resultList, "员工个人已收工作量明细列表", fileName);
  }

  /**
   * 根据条件查询员工已收工作量明细列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeReceivedDetailWorkloadVO>
   */
  public PageInfo<EmployeeReceivedDetailWorkloadVO> findReceivedDetailList(
      EmployeeWorkloadDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<EmployeeReceivedDetailWorkloadVO> resultList =
        mapper.selectEmployeeReceivedDetailList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件查询员工补入工作量明细
   *
   * @param query 查询条件
   * @return PageInfo<EmployeePersonalSupplyWorkloadDetailVO>
   */
  public PageInfo<EmployeePersonalSupplyWorkloadDetailVO> findSupplyWorkloadDetailList(
      EmployeePersonalWorkloadDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<EmployeePersonalSupplyWorkloadDetailVO> resultList =
        mapper.selectEmployeePersonalSupplyWorkloadDetailList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出员工个人补入工作量明细
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportEmployeePersonalSupplyWorkloadDetailList(
      HttpServletResponse response, EmployeePersonalWorkloadDetailQuery query) throws IOException {
    PageInfo<EmployeePersonalSupplyWorkloadDetailVO> pageInfo = findSupplyWorkloadDetailList(query);
    List<EmployeePersonalSupplyWorkloadDetailVO> resultList = pageInfo.getList();
    ExcelUtil<EmployeePersonalSupplyWorkloadDetailVO> excelUtil =
        new ExcelUtil<>(EmployeePersonalSupplyWorkloadDetailVO.class);
    String fileName = query.getQueryDate() + "补入工作量统计明细表";
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    if (null != organization) {
      fileName = organization.getAbbreviation() + fileName;
    }
    excelUtil.exportExcel(response, resultList, "员工个人补入工作量明细列表", fileName);
  }

  /**
   * 根据条件查询员工补入工作量开单明细列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeSupplyDetailWorkloadVO>
   */
  public PageInfo<EmployeeSupplyDetailWorkloadVO> findSupplyDetailList(
      EmployeeWorkloadDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<EmployeeSupplyDetailWorkloadVO> resultList = mapper.selectEmployeeSupplyDetailList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件查询助手实收工作量明细列表
   *
   * @param query 查询条件
   * @return PageInfo<AssistantActualWorkloadDetailVO>
   */
  public PageInfo<AssistantActualWorkloadDetailVO> findAssistantActualWorkloadDetailList(
      AssistantActualWorkloadDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<AssistantActualWorkloadDetailVO> resultList =
        mapper.selectAssistantActualWorkloadDetailList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件查询开单项目数量信息列表
   *
   * @param query 查询条件
   * @return PageInfo<BillingItemInfoVO>
   */
  public PageInfo<BillingItemInfoVO> findBillingItemInfoVOList(BillingItemStatisticsQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<BillingItemInfoVO> resultList = mapper.selectBillingItemInfoList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出开单项目数量列表
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportBillingItemInfoList(
      HttpServletResponse response, BillingItemStatisticsQuery query) throws IOException {
    query.setWhetherPage(false);
    ExcelUtil<BillingItemInfoVO> excelUtil = new ExcelUtil<>(BillingItemInfoVO.class);
    List<BillingItemInfoVO> resultList = mapper.selectBillingItemInfoList(query);
    String fileName = query.getStartDate() + query.getEndDate() + "开单项目数量统计表";
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    if (null != organization) {
      fileName = organization.getAbbreviation() + fileName;
    }
    excelUtil.exportExcel(response, resultList, "开单项目数量统计列表", fileName);
  }

  /**
   * 根据条件查询开单项目统计明细列表
   *
   * @param query 查询条件
   * @return PageInfo<BillingItemDetailVO>
   */
  public PageInfo<BillingItemDetailVO> findBillingItemDetailList(BillingItemDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<BillingItemDetailVO> resultList = mapper.selectBillingItemDetailList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出开单项目统计明细列表
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportBillingItemDetailList(
      HttpServletResponse response, BillingItemDetailQuery query) throws IOException {
    query.setWhetherPage(false);
    ExcelUtil<BillingItemDetailVO> excelUtil = new ExcelUtil<>(BillingItemDetailVO.class);
    List<BillingItemDetailVO> resultList = mapper.selectBillingItemDetailList(query);
    String fileName = query.getStartDate() + query.getEndDate() + "开单项目统计明细表";
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    if (null != organization) {
      fileName = organization.getAbbreviation() + fileName;
    }
    excelUtil.exportExcel(response, resultList, "开单项目统计明细列表", fileName);
  }

  /**
   * 根据条件查询门诊工作量总览
   *
   * @param query 查询条件
   * @return WorkloadStatisticsVO
   */
  public WorkloadStatisticsVO findClinicWorkloadStatistic(DataStatisticsQuery query) {
    WorkloadStatisticsVO resultData = mapper.selectClinicWorkloadStatistic(query);
    return resultData;
  }

  /**
   * 根据条件导出门诊当月账单明细
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportCurrentMonthBillDetail(
      HttpServletResponse response, CurrentMonthBillInfoQuery query) throws IOException {
    List<CurrentMonthBillDetailVO> resultList = mapper.selectCurrentMonthBillDetail(query);
    ExcelUtil<CurrentMonthBillDetailVO> excelUtil = new ExcelUtil<>(CurrentMonthBillDetailVO.class);
    excelUtil.exportExcel(response, resultList, "账单明细记录");
  }

  /**
   * 根据条件导出门诊当月账单当月收费记录
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportCurrentMonthBillPayRecord(
      HttpServletResponse response, CurrentMonthBillInfoQuery query) throws IOException {
    List<CurrentMonthBillPayRecordVO> resultList = mapper.selectCurrentMonthBillPayRecord(query);
    ExcelUtil<CurrentMonthBillPayRecordVO> excelUtil =
        new ExcelUtil<>(CurrentMonthBillPayRecordVO.class);
    excelUtil.exportExcel(response, resultList, "门诊当月账单当月收费记录");
  }

  /**
   * 根据条件查询员工个人免单支付工作量明细列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeFreepaymentWorkloadDetailVO>
   */
  public PageInfo<EmployeeFreepaymentWorkloadDetailVO> findEmployeeFreepaymentWorkloadDetailList(EmployeePersonalWorkloadDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<EmployeeFreepaymentWorkloadDetailVO> resultList =
            mapper.selectEmployeeFreepaymentWorkloadDetailList(query);
    // 免单支付
    if (StringHelper.isNotEmpty(resultList)) {
      Map<Integer, BigDecimal> billIdMap = resultList.stream().collect(Collectors.toMap(EmployeeFreepaymentWorkloadDetailVO::getBillId,v->BigDecimal.ZERO));
      Set<Integer> billIds = billIdMap.keySet();
      List<BaseBillDetail> details = mapper.selectBillDetailByBillIds(billIds);
      Map<Integer, BigDecimal> total = new HashMap<>(16);//每个账单的执行实收总额
      details.forEach(detail -> {
        Integer billId = detail.getBillId();
        BigDecimal sum = total.get(billId);
        if (sum == null) {
          sum = BigDecimal.ZERO;
        }
        total.put(billId, sum.add(detail.getReceivedAmount()));
      });

      List<BaseBillPayDetail> freePayments = baseBillPayDetailMapper.sumPayDetailList(billIds, getFreePaymentIds());
      Map<Integer, BigDecimal> freePaymentMap = freePayments.stream().collect(Collectors.toMap(BaseBillPayDetail::getBillId, BaseBillPayDetail::getPrincipalAmount));
      details.forEach(detail -> {
        Integer executorId = detail.getExecutorId();
        if (query.getEmployeeId().equals(executorId)) {
          Integer billId = detail.getBillId();
          BigDecimal sum = total.get(billId);
          BigDecimal amount = detail.getReceivedAmount()
                  .divide(sum, 2, BigDecimal.ROUND_HALF_UP)
                  .multiply(freePaymentMap.get(billId));
          BigDecimal frees = billIdMap.get(billId);
          billIdMap.put(billId, amount.add(frees));
        }
      });
      resultList.forEach(vo -> {
        Integer billId = vo.getBillId();
        vo.setFreePaymentWorkload(billIdMap.get(billId));
      });
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出员工个人已收工作量明细列表
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportEmployeeFreepaymentdWorkloadDetailList(
          HttpServletResponse response, EmployeePersonalWorkloadDetailQuery query) throws IOException {
    query.setWhetherPage(false);
    PageInfo<EmployeeFreepaymentWorkloadDetailVO> pageInfo =
            findEmployeeFreepaymentWorkloadDetailList(query);
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    String fileName = query.getQueryDate() + "已收工作量统计明细表";
    if (null != organization) {
      fileName = organization.getAbbreviation();
    }
    List<EmployeeFreepaymentWorkloadDetailVO> resultList = pageInfo.getList();
    ExcelUtil<EmployeeFreepaymentWorkloadDetailVO> excelUtil =
            new ExcelUtil<>(EmployeeFreepaymentWorkloadDetailVO.class);
    excelUtil.exportExcel(response, resultList, "员工个人免单支付工作量明细列表", fileName);
  }

  /**
   * 根据条件查询员工免单支付工作量明细项目列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeReceivedDetailWorkloadVO>
   */
  public PageInfo<EmployeeReceivedDetailWorkloadVO> findFreePaymentDetailList(
          EmployeeWorkloadDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<EmployeeReceivedDetailWorkloadVO> resultList =
            mapper.selectEmployeeReceivedDetailList(query);

    // 免单支付
    if (StringHelper.isNotEmpty(resultList)) {
      List<Integer> billIds = Arrays.asList(query.getBillId());
      List<BaseBillDetail> details = mapper.selectBillDetailByBillIds(billIds);
      BigDecimal total = BigDecimal.ZERO;//账单的执行实收总额
      for (BaseBillDetail detail : details) {
        total = total.add(detail.getReceivedAmount());
      }

      List<BaseBillPayDetail> freePayments = baseBillPayDetailMapper.sumPayDetailList(billIds, getFreePaymentIds());
      BigDecimal freePayment = freePayments.get(0).getPrincipalAmount();
      Map<String, BigDecimal> amounts = new HashMap<>(16);
      for (BaseBillDetail detail : details) {
        Integer executorId = detail.getExecutorId();
        Integer itemId = detail.getItemId();
        Byte itemType = detail.getItemType();
        if (query.getEmployeeId().equals(executorId)) {
          BigDecimal amount = detail.getReceivedAmount()
                  .divide(total, 2, BigDecimal.ROUND_HALF_UP)
                  .multiply(freePayment);
          amounts.put(itemId+","+itemType, amount);
        }
      }
      resultList.forEach(vo -> {
        Integer itemId = vo.getItemId();
        Byte itemType = vo.getItemType();
        BigDecimal amount = amounts.get(itemId + "," + itemType);
        if (amount == null) {
          amount = BigDecimal.ZERO;
        }
        vo.setFreePaymentWorkload(amount);
      });
    }
    return new PageInfo<>(resultList);
  }

  public List<BaseBillDetailVO> selectBillDetailByBillId(Integer billId) {
    return mapper.selectBillDetailByBillId(billId);
  }
}
