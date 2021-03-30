package com.yunya.report.ultimate.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.clinic_base.RemoteClinicBaseServiceFeign;
import com.yunya.feign.clinic_base.domain.query.BusinessGoalCompletedInfoQuery;
import com.yunya.feign.clinic_base.domain.vo.BusinessGoalVO;
import com.yunya.feign.report.domain.bo.ClinicWorkloadGroupInfoVO;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.OrganizationModel;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BaseBillDetail;
import com.yunya.models.report.BaseOrganization;
import com.yunya.report.ultimate.mapper.BaseBillDetailMapper;
import com.yunya.report.ultimate.mapper.BaseBillMapper;
import com.yunya.report.ultimate.mapper.BaseBillPayDetailMapper;
import com.yunya.report.ultimate.mapper.BaseOrganizationMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.yunya.framework.common.constant.BusinessConstants.FREE_PAYMENT_ID;

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
  /** 账单 */
  @Autowired private BaseBillMapper baseBillMapper;
  /** 收费记录 */
  @Autowired private BaseBillPayBiz baseBillPayBiz;
  /** 账单收费明细 */
  @Autowired private BaseBillPayDetailMapper baseBillPayDetailMapper;
  /** 诊所基础信息 */
  @Autowired private RemoteClinicBaseServiceFeign clinicBaseServiceFeign;
  /** 系统服务 */
  @Autowired private RemoteSystemServiceFeign remoteSystemServiceFeign;

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
            // 正畸加工费
            BigDecimal orthodonticsFee = vo.getOrthodonticsFee();
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
                    .subtract(orthodonticsFee)
                    .subtract(largeMaterialCost)
                    .subtract(baseWorkload);
            vo.setActualBonusBase(actualBonusBase);
            vo.setActualBonus(actualBonusBase.multiply(bonusCoefficient));
            BigDecimal receivedBonusBase =
                receivedWorkload
                    .add(supplementWorkload)
                    .subtract(refundWorkload)
                    .subtract(processingFee)
                    .subtract(orthodonticsFee)
                    .subtract(largeMaterialCost)
                    .subtract(baseWorkload);
            vo.setReceivedBonusBase(receivedBonusBase);
            vo.setReceivedBonus(receivedBonusBase.multiply(bonusCoefficient));
          });
      assemblyFreepayment(resultList, query);
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
      assemblyFreepayment(resultList, query);
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 查询并装配员工执行的免单支付总额
   *
   * @param resultList
   * @param query
   */
  private void assemblyFreepayment(
      List<? extends EmployeeWorkloadOfOperationVO> resultList, EmployeeWorkloadQuery query) {
    Integer orgId = query.getOrgId();
    Map<String, BigDecimal> userOrgs = new HashMap<>(16);
    Set<Integer> userIds = new HashSet<>();
    resultList.forEach(
        vo -> {
          Integer employeeId = vo.getEmployeeId();
          if (orgId == null) {
            userOrgs.put(employeeId + "," + vo.getOrgId(), BigDecimal.ZERO);
          } else {
            userOrgs.put(employeeId + "," + orgId, BigDecimal.ZERO);
          }
          userIds.add(employeeId);
        });
    query.setEmployeeIds(userIds.toArray(new Integer[0]));
    assemblyFreepayment(userOrgs, query);
    resultList.forEach(
        vo -> {
          BigDecimal free = userOrgs.get(vo.getEmployeeId() + "," + vo.getOrgId());
          vo.setFreePaymentWorkload(free == null ? BigDecimal.ZERO : free);
        });
  }

  private void assemblyFreepayment(
      EmployeeWorkloadQuery query, List<PersonalWorkloadVO> resultList) {
    Integer orgId = query.getOrgId();
    Map<String, BigDecimal> userOrgs = new HashMap<>(16);
    Set<Integer> userIds = new HashSet<>();
    resultList.forEach(
        vo -> {
          Integer employeeId = vo.getEmployeeId();
          if (orgId == null) {
            userOrgs.put(employeeId + "," + vo.getOrgId(), BigDecimal.ZERO);
          } else {
            userOrgs.put(employeeId + "," + orgId, BigDecimal.ZERO);
          }
          userIds.add(employeeId);
        });
    query.setEmployeeIds(userIds.toArray(new Integer[0]));
    assemblyFreepayment(userOrgs, query);
    resultList.forEach(
        vo -> {
          BigDecimal free = userOrgs.get(vo.getEmployeeId() + "," + vo.getOrgId());
          vo.setFreePaymentWorkload(free == null ? BigDecimal.ZERO : free);
        });
  }

  private void assemblyFreepayment(Map<String, BigDecimal> userIds, EmployeeWorkloadQuery query) {
    List<BaseBillDetail> details = mapper.selectBillDetailByQuery(query);
    details =
        details.stream()
            .filter(
                vo ->
                    vo.getReceivedAmount().compareTo(BigDecimal.ZERO) > 0
                        && userIds.containsKey(vo.getExecutorId() + "," + vo.getOrgId()))
            .collect(Collectors.toList());
    Set<Integer> billIds = computePercentage(details);
    Map<Integer, BigDecimal> freePaymentMap = sumFreePaymentMap(billIds);
    details.forEach(
        detail -> {
          String key = detail.getExecutorId() + "," + detail.getOrgId();
          if (userIds.containsKey(key)) {
            Integer billId = detail.getBillId();
            BigDecimal free = freePaymentMap.get(billId);
            BigDecimal amount =
                detail.getDiscountAmount().multiply(free == null ? BigDecimal.ZERO : free);
            userIds.put(key, amount.add(userIds.get(key)));
          }
        });
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
    WorkloadStatisticsVO resultData = new WorkloadStatisticsVO();
    ClinicWorkloadGroupInfoVO workloadInfo = baseBillPayBiz.generateClinicWorkloadInfo(query);
    BigDecimal totalReceivedWorkload =
        workloadInfo
            .getFirstReceivedWorkload()
            .add(workloadInfo.getFirstCouponWorkload())
            .subtract(workloadInfo.getFirstFreePayWorkload());
    totalReceivedWorkload =
        totalReceivedWorkload.add(
            workloadInfo
                .getArrearsReceivedWorkload()
                .add(
                    workloadInfo
                        .getArrearsCouponWorkload()
                        .subtract(workloadInfo.getArrearsFreePayWorkload())));
    totalReceivedWorkload =
        totalReceivedWorkload.add(
            workloadInfo
                .getBeCollectedReceivedWorkload()
                .add(
                    workloadInfo
                        .getBeCollectedCouponWorkload()
                        .subtract(workloadInfo.getBeCollectedFreePayWorkload())));
    BigDecimal totalRefundWorkload = workloadInfo.getTotalRefundWorkload();
    resultData.setTotalClinicActualWorkload(totalReceivedWorkload.subtract(totalRefundWorkload));
    resultData.setTotalReceivedWorkload(totalReceivedWorkload);
    resultData.setTotalBillRefundWorkload(totalRefundWorkload);
    resultData.setTotalClinicReceivedWorkload(workloadInfo.getFirstReceivedWorkload());
    resultData.setTotalFreePaymentWorkload(workloadInfo.getFirstFreePayWorkload());
    resultData.setTotalClinicCouponWorkload(workloadInfo.getFirstCouponWorkload());
    resultData.setTotalClinicBeCollectedReceivedWorkload(
        workloadInfo.getBeCollectedReceivedWorkload());
    resultData.setTotalClinicBeCollectedFreePaymentWorkload(
        workloadInfo.getBeCollectedFreePayWorkload());
    resultData.setTotalClinicBeCollectedCouponWorkload(workloadInfo.getBeCollectedCouponWorkload());
    resultData.setTotalClinicArrearsReceivedWorkload(workloadInfo.getArrearsReceivedWorkload());
    resultData.setTotalClinicArrearsCouponWorkload(workloadInfo.getArrearsCouponWorkload());
    resultData.setTotalClinicArrearsFreePaymentWorkload(workloadInfo.getArrearsFreePayWorkload());
    resultData.setTotalClinicReceivedNotWorkload(workloadInfo.getFirstReceivedNotWorkload());
    resultData.setTotalClinicArrearsReceivedNotWorkload(workloadInfo.getArrearsNotWorkload());
    resultData.setTotalBeCollectedNotWorkload(workloadInfo.getBeCollectedNotWorkload());
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
  public PageInfo<EmployeeFreepaymentWorkloadDetailVO> findEmployeeFreepaymentWorkloadDetailList(
      EmployeePersonalWorkloadDetailQuery query) {
    EmployeeWorkloadQuery workloadQuery = new EmployeeWorkloadQuery();
    workloadQuery.setWhetherPage(false);
    workloadQuery.setDateType(query.getDateType());
    workloadQuery.setQueryDate(query.getQueryDate());
    workloadQuery.setEmployeeIds(new Integer[] {query.getEmployeeId()});
    List<BaseBillDetail> billDetails = mapper.selectBillDetailByQuery(workloadQuery);
    if (StringHelper.isEmpty(billDetails)) {
      return new PageInfo<>(new ArrayList<>());
    }
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    Set<Integer> billIds =
        billDetails.stream().map(BaseBillDetail::getBillId).collect(Collectors.toSet());
    List<EmployeeFreepaymentWorkloadDetailVO> resultList =
        mapper.selectEmployeeFreepaymentWorkloadDetailList(query, billIds, FREE_PAYMENT_ID);
    // 免单支付
    if (StringHelper.isNotEmpty(resultList)) {
      Integer employeeId = query.getEmployeeId();
      Map<Integer, BigDecimal> billPayIdMap =
          resultList.stream()
              .collect(
                  Collectors.toMap(
                      EmployeeFreepaymentWorkloadDetailVO::getBillPayId, v -> BigDecimal.ZERO));
      billIds =
          resultList.stream()
              .map(EmployeeFreepaymentWorkloadDetailVO::getBillId)
              .collect(Collectors.toSet());
      Set<Integer> billPayIds = billPayIdMap.keySet();
      List<BaseBillDetail> details = mapper.selectBillDetailByBillIds(billIds);
      details =
          details.stream()
              .filter(vo -> vo.getReceivedAmount().compareTo(BigDecimal.ZERO) > 0)
              .collect(Collectors.toList());
      computePercentage(details);
      Map<Integer, List<BaseBillPayDetailVO>> freePaymentMap =
          sumFreePaymentMapByBillPayIds(billPayIds);
      details.forEach(
          detail -> {
            Integer executorId = detail.getExecutorId();
            if (employeeId.equals(executorId)) {
              Integer billId = detail.getBillId();
              List<BaseBillPayDetailVO> list = freePaymentMap.get(billId);
              list.forEach(
                  vo -> {
                    Integer billPayId = vo.getBillPayId();
                    BigDecimal free = vo.getPrincipalAmount();
                    BigDecimal amount =
                        detail.getDiscountAmount().multiply(free == null ? BigDecimal.ZERO : free);
                    billPayIdMap.put(billPayId, amount.add(billPayIdMap.get(billPayId)));
                  });
            }
          });
      resultList.forEach(
          vo -> {
            Integer billPayId = vo.getBillPayId();
            BigDecimal free = billPayIdMap.get(billPayId);
            vo.setFreePaymentWorkload(free == null ? BigDecimal.ZERO : free);
            vo.setEmployeeId(employeeId);
          });
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 账单的所有免单支付的收费
   *
   * @param billPayId
   * @return
   */
  private BaseBillPayDetailVO sumFreePayments(Integer billPayId) {
    List<BaseBillPayDetailVO> list =
        baseBillPayDetailMapper.sumPayDetailListByBillPayIds(
            Arrays.asList(billPayId), FREE_PAYMENT_ID);
    if (StringHelper.isEmpty(list)) {
      return null;
    }
    return list.get(0);
  }

  /**
   * 返回账单的所有免单支付金额
   *
   * @param billIds
   * @return
   */
  private Map<Integer, BigDecimal> sumFreePaymentMap(Collection<Integer> billIds) {
    Map<Integer, BigDecimal> result = new HashMap<>(16);
    List<BaseBillPayDetailVO> freePayments =
        baseBillPayDetailMapper.sumPayDetailListByBillIds(billIds, FREE_PAYMENT_ID);
    freePayments.forEach(
        vo -> {
          Integer billId = vo.getBillId();
          BigDecimal amount = result.get(billId);
          if (amount == null) {
            amount = BigDecimal.ZERO;
          }
          result.put(billId, amount.add(vo.getPrincipalAmount()));
        });
    return result;
  }

  /**
   * 返回给定收费的所有免单支付金额
   *
   * @param billPayIds
   * @return
   */
  private Map<Integer, List<BaseBillPayDetailVO>> sumFreePaymentMapByBillPayIds(
      Collection<Integer> billPayIds) {
    Map<Integer, List<BaseBillPayDetailVO>> freePaymentMap = new HashMap<>(16);
    List<BaseBillPayDetailVO> freePayments =
        baseBillPayDetailMapper.sumPayDetailListByBillPayIds(billPayIds, FREE_PAYMENT_ID);
    freePayments.forEach(
        vo -> {
          Integer billId = vo.getBillId();
          List<BaseBillPayDetailVO> baseBillDetailVOS = freePaymentMap.get(billId);
          if (baseBillDetailVOS == null) {
            baseBillDetailVOS = new ArrayList<>();
          }
          baseBillDetailVOS.add(vo);
          freePaymentMap.put(billId, baseBillDetailVOS);
        });
    return freePaymentMap;
  }

  /**
   * 计算每个账单的执行实收的各项占比
   *
   * @param details
   * @return
   */
  private Set<Integer> computePercentage(List<BaseBillDetail> details) {
    Set<Integer> billIds = new HashSet<>();
    // 每个账单的执行实收总额
    Map<Integer, BigDecimal[]> total = new HashMap<>(16);
    details.forEach(
        detail -> {
          Integer billId = detail.getBillId();
          BigDecimal[] sum = total.get(billId);
          if (sum == null) {
            sum = new BigDecimal[] {BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
          }
          sum[0] = sum[0].add(detail.getReceivedAmount());
          total.put(billId, sum);
          billIds.add(billId);
        });
    details.forEach(
        detail -> {
          BigDecimal receivedAmount = detail.getReceivedAmount();
          Integer billId = detail.getBillId();
          BigDecimal[] sum = total.get(billId);
          sum[1] = sum[1].add(receivedAmount);
          BigDecimal amount = receivedAmount.divide(sum[0], 2, BigDecimal.ROUND_HALF_UP);
          if (sum[0].compareTo(sum[1]) == 0) { // 最后一个占比项目
            amount = BigDecimal.ONE.subtract(sum[2]);
          }
          sum[2] = sum[2].add(amount);
          detail.setDiscountAmount(amount);
        });
    return billIds;
  }

  /**
   * 根据条件导出员工免单支付工作量明细列表
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
    List<EmployeeFreepaymentWorkloadDetailVO> resultList = pageInfo.getList();
    ExcelUtil<EmployeeFreepaymentWorkloadDetailVO> excelUtil =
        new ExcelUtil<>(EmployeeFreepaymentWorkloadDetailVO.class);
    String fileName =
        excelUtil.getFileName(
            query.getQueryDate(), null, organization.getAbbreviation(), "免单支付工作量明细");
    excelUtil.exportExcel(response, resultList, "免单支付工作量明细", fileName);
  }

  /**
   * 根据条件查询员工免单支付工作量明细项目列表
   *
   * @param query 查询条件
   * @return PageInfo<EmployeeReceivedDetailWorkloadVO>
   */
  public PageInfo<EmployeeReceivedDetailWorkloadVO> findFreePaymentDetailList(
      EmployeeFreePaymentWorkloadDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<EmployeeReceivedDetailWorkloadVO> resultList =
        mapper.selectEmployeeFreePaymentDetailList(query);

    // 免单支付
    if (StringHelper.isNotEmpty(resultList)) {
      List<BaseBillDetail> details =
          mapper.selectBillDetailByBillIds(Arrays.asList(query.getBillId()));
      details =
          details.stream()
              .filter(vo -> vo.getReceivedAmount().compareTo(BigDecimal.ZERO) > 0)
              .collect(Collectors.toList());
      computePercentage(details);
      BaseBillPayDetailVO free = sumFreePayments(query.getBillPayId());
      Map<String, BigDecimal> amounts = new HashMap<>(16);
      for (BaseBillDetail detail : details) {
        Integer executorId = detail.getExecutorId();
        Integer itemId = detail.getItemId();
        Byte itemType = detail.getItemType();
        if (query.getEmployeeId().equals(executorId)) {
          amounts.put(
              itemId + "," + itemType,
              detail
                  .getDiscountAmount()
                  .multiply(free == null ? BigDecimal.ZERO : free.getPrincipalAmount()));
        }
      }
      resultList.forEach(
          vo -> {
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

  public List<OperationDataBusinessGoalVO> selectWorkloadCompletedList(DataStatisticsQuery query) {
    return mapper.selectWorkloadCompletedList(query);
  }

  /**
   * 根据条件查询个人工作量列表
   *
   * @param query
   * @return
   */
  public PageInfo<PersonalWorkloadVO> personalWorkloadList(EmployeeWorkloadQuery query) {
    List<Integer> billIds = baseBillMapper.distinctBillIdByOrderDate(query);
    if (StringHelper.isEmpty(billIds)) {
      return new PageInfo<>(new ArrayList<>());
    }
    query.setBillIds(billIds);
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<PersonalWorkloadVO> resultList = mapper.selectPersonalWorkloadList(query);
    // 免单支付工作量
    if (StringHelper.isNotEmpty(resultList)) {
      assemblyFreepayment(query, resultList);
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件查询个人工作量列表导出
   *
   * @param query
   * @return
   */
  public void personalWorkloadExport(EmployeeWorkloadQuery query, HttpServletResponse response)
      throws IOException {
    query.setWhetherPage(false);
    PageInfo<PersonalWorkloadVO> pageInfo = personalWorkloadList(query);
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    List<PersonalWorkloadVO> resultList = pageInfo.getList();
    ExcelUtil<PersonalWorkloadVO> excelUtil = new ExcelUtil<>(PersonalWorkloadVO.class);
    String fileName =
        excelUtil.getFileName(
            query.getQueryDate(), null, organization.getAbbreviation(), "员工工作量统计");
    excelUtil.exportExcel(response, resultList, "员工工作量统计", fileName);
  }

  /**
   * 根据条件查询个人开单数量及金额列表
   *
   * @param query
   * @return
   */
  public PageInfo<BillItemStatisticsVO> billItemStatistics(BillItemInfoQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    Collection<Integer[]> items = query.getCategoryItems();
    if (StringHelper.isNotEmpty(items)) {
      Set<Integer> categoryIds = new HashSet<>();
      Set<Integer> itemIds = new HashSet<>();
      items.forEach(
          vo -> {
            categoryIds.add(vo[0]);
            itemIds.add(vo[1]);
          });
      query.setCategoryIds(categoryIds);
      query.setItemIds(itemIds);
    }
    List<BillItemStatisticsVO> resultList = mapper.billItemStatistics(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件查询个人开单数量及金额列表导出
   *
   * @param query
   * @return
   */
  public void billItemStatisticsExport(BillItemInfoQuery query, HttpServletResponse response)
      throws IOException {
    query.setWhetherPage(false);
    PageInfo<BillItemStatisticsVO> pageInfo = billItemStatistics(query);
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    List<BillItemStatisticsVO> resultList = pageInfo.getList();
    ExcelUtil<BillItemStatisticsVO> excelUtil = new ExcelUtil<>(BillItemStatisticsVO.class);
    String fileName =
        excelUtil.getFileName(
            organization.getAbbreviation(), query.getStartDate(), query.getEndDate(), "开单项目数量统计表");
    excelUtil.exportExcel(response, resultList, "开单项目数量统计表", fileName);
  }

  /**
   * 根据条件查询开单数量及金额统计明细列表
   *
   * @param query 查询条件
   * @return PageInfo<BillingItemDetailVO>
   */
  public PageInfo<BillItemStatisticsDetailVO> billItemStatiticsDetail(BillItemDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<BillItemStatisticsDetailVO> resultList = mapper.billItemStatisticsDetail(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件查询开单数量及金额统计明细列表导出
   *
   * @param query 查询条件
   * @param response
   * @return PageInfo<BillingItemDetailVO>
   */
  public void billItemStatiticsDetailExport(BillItemDetailQuery query, HttpServletResponse response)
      throws IOException {
    query.setWhetherPage(false);
    PageInfo<BillItemStatisticsDetailVO> pageInfo = billItemStatiticsDetail(query);
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    List<BillItemStatisticsDetailVO> resultList = pageInfo.getList();
    ExcelUtil<BillItemStatisticsDetailVO> excelUtil =
        new ExcelUtil<>(BillItemStatisticsDetailVO.class);
    String sDate = query.getBillStartDate();
    String eDate = query.getBillEndDate();
    if (StringHelper.isEmpty(sDate) || StringHelper.isEmpty(eDate)) {
      sDate = query.getStartDate();
      eDate = query.getEndDate();
    }
    String fileName =
        excelUtil.getFileName(sDate, eDate, organization.getAbbreviation(), "开单项目数量统计表");
    excelUtil.exportExcel(response, resultList, "开单项目数量统计表", fileName);
  }

  /**
   * 根据订单ID列表查询订单工作量
   *
   * @param billIds 订单ID列表
   * @return list
   */
  public List<BillRecordWorkloadVO> findBillWorkloadInfoByBillIds(Set<Integer> billIds) {
    return mapper.selectBillTotalWorkload(billIds);
  }

  /**
   * 根据订单ID列表查询订单非工作量
   *
   * @param billIds 订单ID列表
   * @return list
   */
  public List<BillRecordWorkloadVO> findBillNotWorkloadInfoByBillIds(Set<Integer> billIds) {
    return mapper.selectBillTotalNotWorkload(billIds);
  }

  /**
   * 根据订单ID查询订单明细工作量
   *
   * @param billId 订单ID
   * @return list
   */
  public List<BaseBillDetailToWorkloadVO> findBillDetailForWorkload(Integer billId) {
    return mapper.selectBillDetailForWorkload(billId);
  }

  /**
   * 根据订单ID查询订单明细非工作量
   *
   * @param billId 订单ID
   * @return list
   */
  public List<BaseBillDetailToWorkloadVO> findBillDetailForNotWorkload(Integer billId) {
    return mapper.selectBillDetailForNotWorkload(billId);
  }

  /**
   * 根据条件查询产品使用报表
   *
   * @param query
   * @return
   */
  public PageInfo<CouponExecutoredVO> couponExecutoredList(CouponExecutoredQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<CouponExecutoredVO> list = mapper.couponExecutoredList(query);
    return new PageInfo<>(list);
  }

  /**
   * 根据条件查询产品使用报表导出
   *
   * @param query
   * @return
   */
  public void couponExecutoredExport(CouponExecutoredQuery query, HttpServletResponse response)
      throws IOException {
    query.setWhetherPage(false);
    PageInfo<CouponExecutoredVO> pageInfo = couponExecutoredList(query);
    List<CouponExecutoredVO> resultList = pageInfo.getList();
    ExcelUtil<CouponExecutoredVO> excelUtil = new ExcelUtil<>(CouponExecutoredVO.class);
    String fileName = excelUtil.getFileName(query.getStartDate(), query.getEndDate(), "", "产品使用报表");
    excelUtil.exportExcel(response, resultList, "产品使用报表", fileName);
  }

  /**
   * 根据条件查询产品使用报表明细
   *
   * @param query
   * @return
   */
  public PageInfo<CouponExecutoredDetailVO> couponExecutoredDetails(
      CouponExecutoredDetailQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<CouponExecutoredDetailVO> list = mapper.couponExecutoredDetails(query);
    return new PageInfo<>(list);
  }

  /**
   * 月工作量完成度导出
   *
   * @param response
   * @throws IOException
   */
  public void workloadMonthGoalCompletedExport(HttpServletResponse response) throws IOException {
    DateTime now = new DateTime();
    String startDate = now.dayOfMonth().withMinimumValue().toString("yyyy-MM-dd");
    String curDate = now.toString("yyyy-MM-dd");
    List<WorkloadMonthGoalCompletedVO> resultList = workloadCompleted(startDate, curDate);
    ExcelUtil<WorkloadMonthGoalCompletedVO> excelUtil =
        new ExcelUtil<>(WorkloadMonthGoalCompletedVO.class);
    String fileName = excelUtil.getFileName(startDate, curDate, "", "月营业目标完成度报表");
    excelUtil.exportExcel(response, resultList, "月营业目标完成度报表", fileName);
  }

  /**
   * 查询组织信息列表
   *
   * @return
   */
  private List<OrganizationInfoDetail> getOrganizationList() {
    OrganizationModel model = new OrganizationModel();
    model.setWhetherPage(false);
    model.setTypes(new Byte[] {2});
    return remoteSystemServiceFeign.findOrgInfoList(model);
  }

  /**
   * 各个门诊的月工作量和日工作量合计
   *
   * @return
   * @param startDate
   * @param curDate
   */
  public List<WorkloadMonthGoalCompletedVO> workloadCompleted(String startDate, String curDate) {
    Map<Integer, BigDecimal> workloadGoalMap = workloadMonthGoal();
    Integer[] orgIds = {26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37};
    DataStatisticsQuery query = new DataStatisticsQuery();
    query.setDateType((byte) 0);
    query.setStartDate(startDate);
    query.setEndDate(curDate);
    query.setOrgIds(orgIds);
    Map<Integer, BigDecimal[]> workloadCompleted = baseBillPayBiz.computeWorkloadGroupOrgId(query);
    WorkloadMonthGoalCompletedVO goalVO = new WorkloadMonthGoalCompletedVO();
    goalVO.setItemTitle("目标值");
    WorkloadMonthGoalCompletedVO monthVO = new WorkloadMonthGoalCompletedVO();
    monthVO.setItemTitle("实际值");
    WorkloadMonthGoalCompletedVO percentageVO = new WorkloadMonthGoalCompletedVO();
    percentageVO.setItemTitle("完成度");
    WorkloadMonthGoalCompletedVO curVO = new WorkloadMonthGoalCompletedVO();
    curVO.setItemTitle("今日完成");
    BigDecimal goalTotal = BigDecimal.ZERO;
    BigDecimal monthTotal = BigDecimal.ZERO;
    BigDecimal percentageTotal = BigDecimal.ZERO;
    BigDecimal curTotal = BigDecimal.ZERO;
    for (Integer orgId : orgIds) {
      BigDecimal goal = workloadGoalMap.get(orgId); // 目标值
      if (goal == null) {
        goal = BigDecimal.ZERO;
      }
      BigDecimal[] workloads = workloadCompleted.get(orgId);
      if (workloads == null) {
        workloads = new BigDecimal[] {BigDecimal.ZERO, BigDecimal.ZERO};
      }
      BigDecimal monthWorkload = workloads[0].setScale(2, BigDecimal.ROUND_HALF_UP); // 实际值
      BigDecimal curWorkload = workloads[1]; // 今日完成
      BigDecimal completedPercentage = BigDecimal.ZERO; // 完成度
      if (goal.compareTo(BigDecimal.ZERO) != 0) {
        completedPercentage =
            monthWorkload.divide(goal, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
      }
      goalTotal = goalTotal.add(goal);
      monthTotal = monthTotal.add(monthWorkload);
      percentageTotal = percentageTotal.add(completedPercentage);
      curTotal = curTotal.add(curWorkload);
      setOrgTotal(
          orgId,
          goal,
          monthWorkload,
          completedPercentage,
          curWorkload,
          goalVO,
          monthVO,
          percentageVO,
          curVO);
    }
    goalVO.setTotal(goalTotal.toString());
    monthVO.setTotal(monthTotal.toString());
    percentageVO.setTotal(percentageTotal.toString() + "%");
    curVO.setTotal(curTotal.toString());
    return Arrays.asList(goalVO, monthVO, percentageVO, curVO);
  }

  private void setOrgTotal(
      Integer orgId,
      BigDecimal goal,
      BigDecimal monthWorkload,
      BigDecimal completedPercentage,
      BigDecimal curWorkload,
      WorkloadMonthGoalCompletedVO goalVO,
      WorkloadMonthGoalCompletedVO monthVO,
      WorkloadMonthGoalCompletedVO percentageVO,
      WorkloadMonthGoalCompletedVO curVO) {
    switch (orgId) {
      case 26:
        { // 古墩路
          goalVO.setGuDunRoad(goal.toString());
          monthVO.setGuDunRoad(monthWorkload.toString());
          percentageVO.setGuDunRoad(completedPercentage.toString() + "%");
          curVO.setGuDunRoad(curWorkload.toString());
          break;
        }
      case 27:
        { // 金山大道
          goalVO.setJinShaRoad(goal.toString());
          monthVO.setJinShaRoad(monthWorkload.toString());
          percentageVO.setJinShaRoad(completedPercentage.toString() + "%");
          curVO.setJinShaRoad(curWorkload.toString());
          break;
        }
      case 28:
        { // 乾元
          goalVO.setQianYuan(goal.toString());
          monthVO.setQianYuan(monthWorkload.toString());
          percentageVO.setQianYuan(completedPercentage.toString() + "%");
          curVO.setQianYuan(curWorkload.toString());
          break;
        }
      case 29:
        { // 常春藤
          goalVO.setChangChunTeng(goal.toString());
          monthVO.setChangChunTeng(monthWorkload.toString());
          percentageVO.setChangChunTeng(completedPercentage.toString() + "%");
          curVO.setChangChunTeng(curWorkload.toString());
          break;
        }
      case 30:
        { // 西溪路
          goalVO.setXiXiRoad(goal.toString());
          monthVO.setXiXiRoad(monthWorkload.toString());
          percentageVO.setXiXiRoad(completedPercentage.toString() + "%");
          curVO.setXiXiRoad(curWorkload.toString());
          break;
        }
      case 31:
        { // 春花江月
          goalVO.setChunJiangHuaYue(goal.toString());
          monthVO.setChunJiangHuaYue(monthWorkload.toString());
          percentageVO.setChunJiangHuaYue(completedPercentage.toString() + "%");
          curVO.setChunJiangHuaYue(curWorkload.toString());
          break;
        }
      case 32:
        { // 鲲鹏
          goalVO.setKunPengRoad(goal.toString());
          monthVO.setKunPengRoad(monthWorkload.toString());
          percentageVO.setKunPengRoad(completedPercentage.toString() + "%");
          curVO.setKunPengRoad(curWorkload.toString());
          break;
        }
      case 33:
        { // 滨江龙湖
          goalVO.setLongHu(goal.toString());
          monthVO.setLongHu(monthWorkload.toString());
          percentageVO.setLongHu(completedPercentage.toString() + "%");
          curVO.setLongHu(curWorkload.toString());
          break;
        }
      case 34:
        { // 雅文
          goalVO.setYaWen(goal.toString());
          monthVO.setYaWen(monthWorkload.toString());
          percentageVO.setYaWen(completedPercentage.toString() + "%");
          curVO.setYaWen(curWorkload.toString());
          break;
        }
      case 35:
        { // 博方
          goalVO.setBoFang(goal.toString());
          monthVO.setBoFang(monthWorkload.toString());
          percentageVO.setBoFang(completedPercentage.toString() + "%");
          curVO.setBoFang(curWorkload.toString());
          break;
        }
      case 36:
        { // 艾芃
          goalVO.setAiPeng(goal.toString());
          monthVO.setAiPeng(monthWorkload.toString());
          percentageVO.setAiPeng(completedPercentage.toString() + "%");
          curVO.setAiPeng(curWorkload.toString());
          break;
        }
      case 37:
        { // 文二西路
          goalVO.setWenErXiRoad(goal.toString());
          monthVO.setWenErXiRoad(monthWorkload.toString());
          percentageVO.setWenErXiRoad(completedPercentage.toString() + "%");
          curVO.setWenErXiRoad(curWorkload.toString());
          break;
        }
      default:
    }
  }

  /**
   * 查询所有门诊当前月份的工作量目标
   *
   * @return
   */
  private Map<Integer, BigDecimal> workloadMonthGoal() {
    String date = DateUtil.parseDateToStr("yyyy-MM", new Date());
    BusinessGoalCompletedInfoQuery query = new BusinessGoalCompletedInfoQuery();
    query.setBusinessType((byte) 1); // 工作量
    query.setBusinessTypes(new Byte[] {1}); // 工作量
    query.setDateType((byte) 1); // 按月查
    query.setStartDate("1");
    query.setEndDate("1");
    query.setOrgId(0);
    query.setDateRange(Arrays.asList(date));
    List<BusinessGoalVO> goalVOS = clinicBaseServiceFeign.businessGoalList(query);
    if (StringHelper.isNotEmpty(goalVOS)) {
      return goalVOS.stream()
          .collect(Collectors.toMap(BusinessGoalVO::getBelongId, BusinessGoalVO::getBusinessGoal));
    }
    return new HashMap<>(16);
  }
}
