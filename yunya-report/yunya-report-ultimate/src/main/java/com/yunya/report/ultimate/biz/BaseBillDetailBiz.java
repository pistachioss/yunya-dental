package com.yunya.report.ultimate.biz;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yunya.feign.clinic_base.RemoteClinicBaseServiceFeign;
import com.yunya.feign.clinic_base.domain.query.BusinessGoalCompletedInfoQuery;
import com.yunya.feign.clinic_base.domain.query.SpecialistProjectQuery;
import com.yunya.feign.clinic_base.domain.vo.BusinessGoalVO;
import com.yunya.feign.clinic_base.domain.vo.SpecialistProjectVO;
import com.yunya.feign.report.domain.bo.ClinicWorkloadGroupInfoVO;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.PageUtl;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.*;
import com.yunya.report.ultimate.mapper.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.yunya.framework.common.constant.BusinessConstants.FREE_PAYMENT_ID;
import static com.yunya.framework.common.constant.OperationCodeConstants.PARAMETERS_IS_ILLEGAL;

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
  /** 患者信息*/
  @Autowired private PatientBaseInfoBiz patientBaseInfoBiz;
  /** 患者来源*/
  @Autowired private BasePatientOriginMapper basePatientOriginMapper;
  /** 卡券*/
  @Autowired private BaseCouponMapper baseCouponMapper;
  /** 项目信息*/
  @Autowired private BaseTariffInfoBiz baseTariffInfoBiz;

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
    String fileName = query.getQueryDate() + "员工工作量统计";
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
    monthCategoryFreePayment(resultList, query);
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
    String fileName = query.getQueryDate() + "实收工作量统计明细表";
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
    String fileName = query.getStartDate() + "-" + query.getEndDate() + "开单项目数量统计表";
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
    String fileName = query.getStartDate() + "-" + query.getEndDate() + "开单项目统计明细表";
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
    String fileName = query.getCurrentMonth() + "账单明细报表";
    List<CurrentMonthBillDetailVO> resultList = mapper.selectCurrentMonthBillDetail(query);
    ExcelUtil<CurrentMonthBillDetailVO> excelUtil = new ExcelUtil<>(CurrentMonthBillDetailVO.class);
    Integer orgId = query.getOrgId();
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(orgId);
    if (organization != null) {
      fileName = organization.getAbbreviation() + fileName;
    }
    excelUtil.exportExcel(response, resultList, "账单明细记录", fileName);
  }

  /**
   * 根据条件导出门诊当月账单当月收费记录
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportCurrentMonthBillPayRecord(
          HttpServletResponse response, CurrentMonthBillInfoQuery query) throws IOException {
    String fileName = query.getCurrentMonth() + "账单当月收费记录";
    Integer orgId = query.getOrgId();
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(orgId);
    if (organization != null) {
      fileName = organization.getAbbreviation() + fileName;
    }
    List<CurrentMonthBillPayRecordVO> resultList = mapper.selectCurrentMonthBillPayRecord(query);
    ExcelUtil<CurrentMonthBillPayRecordVO> excelUtil =
            new ExcelUtil<>(CurrentMonthBillPayRecordVO.class);
    excelUtil.exportExcel(response, resultList, "门诊当月账单当月收费记录", fileName);
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
  public List<BillRecordWorkloadVO> findBillWorkloadInfoByBillIds(Collection<Integer> billIds) {
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
    DynamicHeaderPageInfo<JSONObject> pageInfo = workloadCompleted(startDate, curDate);
    List<JSONObject> resultList = pageInfo.getList();
    Map<String, String> titles = pageInfo.getMap();//表头
    ExcelUtil excelUtil = new ExcelUtil(JSONObject.class);
    String fileName = "月营业目标完成度报表";
    excelUtil.exportExcel(response, resultList, fileName, fileName, titles);
  }

  /**
   * 各个门诊的月工作量和日工作量合计
   *
   * @return
   * @param startDate
   * @param curDate
   */
  public DynamicHeaderPageInfo<JSONObject> workloadCompleted(String startDate, String curDate) {
    Map<Integer, BigDecimal> workloadGoalMap = workloadMonthGoal();
    List<BaseOrganization> orgs = getOrganization(new ClinicPerformanceBusinessQuery());
    Integer[] orgIds = orgs.stream().map(BaseOrganization::getOrgId).toArray(Integer[]::new);
    DataStatisticsQuery query = new DataStatisticsQuery();
    query.setDateType((byte) 0);
    query.setStartDate(startDate);
    query.setEndDate(curDate);
    query.setOrgIds(orgIds);
    Map<Integer, BigDecimal[]> workloadCompleted = baseBillPayBiz.computeWorkloadGroupOrgId(query);
    DynamicHeaderPageInfo pageInfo = new DynamicHeaderPageInfo();
    JSONObject goalObj = new JSONObject();
    goalObj.put("name", "目标值");
    JSONObject monthObj = new JSONObject();
    monthObj.put("name", "实际值");
    JSONObject completedObj = new JSONObject();
    completedObj.put("name", "完成度");
    JSONObject curCompletedObj = new JSONObject();
    curCompletedObj.put("name", "今日完成");
    Map<String, String> titles = new LinkedHashMap<>(16);
    titles.put("name", "门诊");
    BigDecimal goalTotal = BigDecimal.ZERO;
    BigDecimal monthTotal = BigDecimal.ZERO;
    BigDecimal percentageTotal = BigDecimal.ZERO;
    BigDecimal curTotal = BigDecimal.ZERO;
    for (BaseOrganization org : orgs) {
      Integer orgId = org.getOrgId();
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
      curTotal = curTotal.add(curWorkload);
      String key = orgId + "";
      goalObj.put(key, goal);
      monthObj.put(key, monthWorkload);
      completedObj.put(key, completedPercentage.toString() + "%");
      curCompletedObj.put(key, curWorkload);
      titles.put(key, org.getAbbreviation());
    }
    if (goalTotal.compareTo(BigDecimal.ZERO)!=0) {
      percentageTotal = monthTotal.divide(goalTotal, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
    }
    goalObj.put("total", goalTotal);
    monthObj.put("total", monthTotal);
    completedObj.put("total", percentageTotal.toString() + "%");
    curCompletedObj.put("total", curTotal);
    titles.put("total", "合计");
    List<JSONObject> result = Lists.newArrayList(goalObj, monthObj, completedObj, curCompletedObj);
    pageInfo.setTotal(result.size());
    pageInfo.setList(result);
    pageInfo.setMap(titles);
    return pageInfo;
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
    query.setDateRange(Collections.singletonList(date));
    List<BusinessGoalVO> goalVOS = clinicBaseServiceFeign.businessGoalList(query);
    if (StringHelper.isNotEmpty(goalVOS)) {
      return goalVOS.stream()
              .collect(Collectors.toMap(BusinessGoalVO::getBelongId, BusinessGoalVO::getBusinessGoal));
    }
    return new HashMap<>(16);
  }

  /**
   * 根据条件查询收费项目工作量列表
   *
   * @param query 查询条件
   * @return
   */
  public PageInfo<BillItemTollAndWorkloadVO> findStatisticsTariffPaymentWorkloadList(
          BillItemTollAndWorkloadQuery query) {
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
    List<BillItemTollAndWorkloadVO> resultList = mapper.selectTariffWorkloadInfo(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出项目收费及工作量明细列表
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportTariffPaymentWorkloadList(
          HttpServletResponse response, BillItemTollAndWorkloadQuery query) throws IOException {
    query.setWhetherPage(false);
    String fileName = query.getStartDate() + "-" + query.getEndDate() + "收费项目及工作量列表";
    List<BillItemTollAndWorkloadVO> resultList =
            findStatisticsTariffPaymentWorkloadList(query).getList();
    ExcelUtil<BillItemTollAndWorkloadVO> excelUtil =
            new ExcelUtil<>(BillItemTollAndWorkloadVO.class);
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    if (organization != null) {
      fileName = organization.getAbbreviation() + fileName;
    }
    excelUtil.exportExcel(response, resultList, "收费项目及工作量列表", fileName);
  }

  /**
   * 根据条件查询门诊工作量统计（优化版）
   *
   * @param queryForm 查询条件
   * @return
   */
  public DynamicHeaderPageInfo<JSONObject> clinicPerformanceList(ClinicPerformanceBusinessQuery queryForm) {
    String startDate = queryForm.getStartDate().substring(0,7);
    String endDate = queryForm.getEndDate().substring(0,7);
    String year = startDate.substring(0,4); //年份
    if (!year.equals(endDate.substring(0,4))) {
      throw new ClientServiceException("查询月份不能跨年", PARAMETERS_IS_ILLEGAL);
    }
    String sMonth = startDate.substring(5,7); //月份
    String eMonth = endDate.substring(5,7); //月份
    List<BaseOrganization> orgs = getOrganization(queryForm);
    Integer[] orgIds = orgs.stream().map(BaseOrganization::getOrgId).toArray(Integer[]::new);
    DataStatisticsQuery query = new DataStatisticsQuery();
    query.setDateType(queryForm.getDateType());
    query.setOrgIds(orgIds);
    List<String> curMonthList = DateUtil.sliceUpDateRange(startDate, endDate);
    Map<Integer, BigDecimal> workloadGoalMap = workloadMonthGoal();
    //环比：去年+查询月份范围
    String preYear = DateUtil.preYear(year);
    String chainSMonth = preYear + "-" + sMonth;
    String chainEMonth = preYear + "-" + eMonth;
    String minMonth = chainSMonth;
    List<String> chainMonthList = DateUtil.sliceUpDateRange(chainSMonth, chainEMonth);

    //同比：查询条件的开始月份 + 查询月份范围的跨度值
    int range = 1; //默认1个月
    if (!startDate.equals(endDate)) {
      range = Integer.parseInt(eMonth)-Integer.parseInt(sMonth);
    }
    String preSMonth = DateUtil.preMonth(startDate, range);
    String preEMonth = DateUtil.preMonth(endDate, range);
    if (DateUtil.compareMonth(preSMonth, minMonth) < 0) {
      minMonth = preSMonth;
    }
    List<String> preMonthList = DateUtil.sliceUpDateRange(preSMonth, preEMonth);

    //年度工作量
    String yearSMonth = year + "-01";
    String yearEMonth = year + "-12";
    if (DateUtil.compareMonth(yearSMonth, minMonth) < 0) {
      minMonth = yearSMonth;
    }
    query.setStartDate(minMonth);
    query.setEndDate(yearEMonth);
    List<String> yearMonthList = DateUtil.sliceUpDateRange(yearSMonth, yearEMonth);
    Map<String, Map<Integer, BigDecimal>> workloadMap = baseBillPayBiz.computeWorkloadGroupOrgIdAndMonth(query);
    DynamicHeaderPageInfo pageInfo = new DynamicHeaderPageInfo<>();
    List<JSONObject> result = new ArrayList<>();
    if (StringHelper.isNotEmpty(orgs)) {
      BigDecimal actualTotal = BigDecimal.ZERO;
      BigDecimal goalTotal = BigDecimal.ZERO;
      BigDecimal completedTotal = BigDecimal.ZERO;
      BigDecimal chainTotal = BigDecimal.ZERO;
      BigDecimal preTotal = BigDecimal.ZERO;
      BigDecimal yearTotal = BigDecimal.ZERO;
      JSONObject actual = new JSONObject();
      init(actual, startDate, endDate, "实际值");
      JSONObject goals = new JSONObject();
      init(goals, startDate, endDate, "目标值");
      JSONObject completed = new JSONObject();
      init(completed, startDate, endDate, "完成度");
      JSONObject chainDiff = new JSONObject();//环比
      init(chainDiff, startDate, endDate, "环比值");
      JSONObject preDiff = new JSONObject();//同比
      init(preDiff, startDate, endDate, "同比值");
      JSONObject curYear = new JSONObject();//年度总工作量
      init(curYear, startDate, endDate, "年度总工作量");
      Map<String, String> map = new LinkedHashMap<>();
      map.put("date", "时间");
      map.put("name", "工作量");
      for (BaseOrganization vo : orgs) {
        Integer orgId = vo.getOrgId();
        BigDecimal goal = workloadGoalMap.get(orgId);
        if (goal == null) {
          goal = BigDecimal.ZERO;
        }
        BigDecimal workloads = computeOrgWorkload(orgId, curMonthList, workloadMap);
        BigDecimal completedPercentage = BigDecimal.ZERO;
        if (goal.compareTo(BigDecimal.ZERO) != 0) {
          completedPercentage =
                  workloads.divide(goal, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
        }
        String key = orgId + "";
        map.put(key, vo.getAbbreviation());
        actual.put(key, workloads);
        goals.put(key, goal);
        completed.put(key, completedPercentage.toString() + "%");
        BigDecimal chainWorkload = computeOrgWorkload(orgId, chainMonthList, workloadMap);
        BigDecimal preWorkload = computeOrgWorkload(orgId, preMonthList, workloadMap);
        BigDecimal yearWorkload = computeOrgWorkload(orgId, yearMonthList, workloadMap);
        chainDiff.put(key, chainWorkload);
        preDiff.put(key, preWorkload);
        curYear.put(key, yearWorkload);
        actualTotal = actualTotal.add(workloads);
        goalTotal = goalTotal.add(goal);
        chainTotal = chainTotal.add(chainWorkload);
        preTotal = preTotal.add(preWorkload);
        yearTotal = yearTotal.add(yearWorkload);
      }
      map.put("total","合计");
      actual.put("total", actualTotal);
      goals.put("total", goalTotal);
      if (goalTotal.compareTo(BigDecimal.ZERO) != 0) {
        completedTotal =
                actualTotal.divide(goalTotal, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
      }
      completed.put("total", completedTotal.toString() + "%");
      chainDiff.put("total", chainTotal);
      preDiff.put("total", preTotal);
      curYear.put("total", yearTotal);
      result.add(actual);
      result.add(goals);
      result.add(completed);
      result.add(chainDiff);
      result.add(preDiff);
      result.add(curYear);
      pageInfo.setMap(map);
    }
    pageInfo.setPageNum(query.getPageNum());
    pageInfo.setPageSize(query.getPageSize());
    pageInfo.setTotal(result.size());
    pageInfo.setList(result);
    return pageInfo;
  }

  private BigDecimal computeOrgWorkload(Integer orgId, List<String> chainMonthList, Map<String, Map<Integer, BigDecimal>> workloadMap) {
    BigDecimal result = BigDecimal.ZERO;
    for (String month : chainMonthList) {
      Map<Integer, BigDecimal> midMap = workloadMap.get(month);
      if (midMap == null) {
        midMap = new HashMap<>(16);
      }
      BigDecimal workload = midMap.get(orgId);
      if (workload == null) {
        workload = BigDecimal.ZERO;
      }
      result = result.add(workload);
    }
    return result;
  }

  /**
   * 获取所有门诊信息
   *
   * @param query
   */
  private List<BaseOrganization> getOrganization(ClinicPerformanceBusinessQuery query) {
    return organizationMapper.selectOrganizationList(query);
  }

  /**
   * 初始化
   *
   * @param object
   * @param sMonth
   * @param eMonth
   * @param value
   */
  private void init(JSONObject object, String sMonth, String eMonth, String value) {
    String month = sMonth;
    if (!sMonth.equals(eMonth)) {
      month = sMonth + "-" + eMonth;
    }
    object.put("date", month);
    object.put("name", value);
  }

  /**
   * 门诊业绩导出
   *
   * @param query
   * @param response
   */
  public void clinicPerformanceExport(ClinicPerformanceBusinessQuery query, HttpServletResponse response) throws IOException {
    query.setWhetherPage(false);
    DynamicHeaderPageInfo<JSONObject> pageInfo = clinicPerformanceList(query);
    List<JSONObject> list = pageInfo.getList();
    Map<String, String> titles = pageInfo.getMap();//表头
    ExcelUtil excelUtil = new ExcelUtil(JSONObject.class);
    String fileName = excelUtil.getFileName(query.getStartDate(), query.getEndDate(), "", "门诊工作量目标完成情况");
    excelUtil.exportExcel(response, list, "门诊工作量目标完成情况", fileName, titles);
  }

  /**
   * 根据条件查询初诊来源数量分析
   *
   * @param query 查询条件
   * @return
   */
  public DynamicHeaderPageInfo<JSONObject> clinicFirstVisitSourceList(ClinicPerformanceBusinessQuery query) {
    String startDate = query.getStartDate().substring(0,7);
    String endDate = query.getEndDate().substring(0,7);
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<BasePatientOrigin> origins = basePatientOriginMapper.selectPatientOriginList(query);
    DynamicHeaderPageInfo pageInfo = new DynamicHeaderPageInfo<>(origins);
    if (query.getWhetherPage()) {
      PageHelper.clearPage();
    }
    List<BaseOrganization> orgs = getOrganization(query);
    if (StringHelper.isEmpty(query.getOriginTypes())) {
      query.setOriginTypes(origins.stream().map(BasePatientOrigin::getOriginType).collect(Collectors.toSet()));
    }
    List<BaseTreatmentProcessVO> patientIds = patientBaseInfoBiz.firstVisitPatientList(query);
    List<PatientFirstVisitSourceVO> patients = patientBaseInfoBiz.clinicFirstVisitSourceList(query,
            patientIds.stream().map(BaseTreatmentProcess::getPatientId).collect(Collectors.toSet()));
    Map<String, Integer> originMap = new HashMap<>(16);
    patients.forEach(vo-> originMap.put(vo.getOriginType() + "," + vo.getOrgId(), vo.getFirstVisitCount()));
    List<JSONObject> result = new ArrayList<>();
    if (StringHelper.isNotEmpty(origins)) {
      Map<String, String> map = new LinkedHashMap<>();
      map.put("date", "时间");
      map.put("name", "患者来源");
      for (BasePatientOrigin vo : origins) {
        JSONObject object = new JSONObject();
        init(object, startDate, endDate, vo.getName());
        object.put("total", computeOrgPatientCount(vo.getOriginType()+"", object, map, orgs, originMap));
        result.add(object);
      }
      map.put("total", "合计");
      pageInfo.setMap(map);
    }
    pageInfo.setList(result);
    return pageInfo;
  }

  /**
   * 计算所有门诊的总数
   *
   * @param firstKey
   * @param object
   * @param map
   * @param orgs
   * @param originMap
   * @return
   */
  private Integer computeOrgPatientCount(String firstKey, JSONObject object, Map<String, String> map,
                                         List<BaseOrganization> orgs, Map<String, Integer> originMap) {
    Integer total = 0;
    for (BaseOrganization org : orgs) {
      Integer orgId = org.getOrgId();
      String key = orgId + "";
      Integer count = originMap.get(firstKey + "," + orgId);
      if (count == null) {
        count = 0;
      }
      total += count;
      object.put(key, count);
      map.put(key, org.getAbbreviation());
    }
    return total;
  }

  /**
   * 根据条件导出初诊来源数量分析
   *
   * @param query 查询条件
   * @return
   */
  public void clinicFirstVisitSourceExport(ClinicPerformanceBusinessQuery query, HttpServletResponse response) throws IOException {
    query.setWhetherPage(false);
    DynamicHeaderPageInfo<JSONObject> pageInfo = clinicFirstVisitSourceList(query);
    List<JSONObject> list = pageInfo.getList();
    Map<String, String> titles = pageInfo.getMap();//表头
    ExcelUtil excelUtil = new ExcelUtil<>(JSONObject.class);
    String fileName = excelUtil.getFileName(query.getStartDate(), query.getEndDate(),"","门诊各初诊来源数据统计");
    excelUtil.exportExcel(response, list, "门诊各初诊来源数据统计", fileName, titles);
  }

  /**
   * 根据条件查询门诊专科项目数量统计
   *
   * @param query 查询条件
   * @return
   */
  public DynamicHeaderPageInfo<JSONObject> clinicSpecialItemList(ClinicPerformanceBusinessQuery query) {
    String startDate = query.getStartDate().substring(0,7);
    String endDate = query.getEndDate().substring(0,7);
    PageInfo<SpecialistProjectVO> pageInfo = getSpecialProjectList(query);
    List<SpecialistProjectVO> specialItems = pageInfo.getList();
    DynamicHeaderPageInfo resPageInfo = new DynamicHeaderPageInfo();
    if (StringHelper.isNotEmpty(specialItems)) {
      List<Integer> itemIds = new ArrayList<>();
      specialItems.forEach(vo->{
        String[] itemIdStr = vo.getTariffItemIds().split(",");
        for (String id : itemIdStr) {
          itemIds.add(Integer.parseInt(id));
        }
      });
      if (query.getWhetherPage()) {
        PageHelper.clearPage();
      }
      query.setItemIds(itemIds);
      List<BillItemStatisticsVO> list = billItemStatisticsGroupByOrgId(query, "item_id");
      Map<String, Integer> dataMap = new HashMap<>(16);
      list.forEach(vo -> dataMap.put(vo.getItemId() + "," + vo.getOrgId(), vo.getQuantity()));
      List<BaseOrganization> orgs = getOrganization(query);
      List<JSONObject> result = new ArrayList<>();
      if (StringHelper.isNotEmpty(specialItems)) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("date", "时间");
        map.put("name", "专科项目");
        for (SpecialistProjectVO item : specialItems) {
          JSONObject object = new JSONObject();
          init(object, startDate, endDate, item.getSpecialistProjectName());
          String[] ids = item.getTariffItemIds().split(",");
          Integer total = 0;
          for (BaseOrganization org : orgs) {
            Integer orgId = org.getOrgId();
            String key = orgId + "";
            int count = 0;
            for (String id : ids) {
              Integer quantity = dataMap.get(id + "," + orgId);
              if (quantity == null) {
                quantity = 0;
              }
              count += quantity;
            }
            object.put(key, count);
            map.put(key, org.getAbbreviation());
            total += count;
          }
          object.put("total", total);
          result.add(object);
        }
        map.put("total", "合计");
        resPageInfo.setMap(map);
      }
      resPageInfo.setTotal(pageInfo.getTotal());
      resPageInfo.setList(result);
    }
    resPageInfo.setPageNum(query.getPageNum());
    resPageInfo.setPageSize(query.getPageSize());
    return resPageInfo;
  }

  private List<BillItemStatisticsVO> billItemStatisticsGroupByOrgId(ClinicPerformanceBusinessQuery query) {
    return billItemStatisticsGroupByOrgId(query, null);
  }

  private List<BillItemStatisticsVO> billItemStatisticsGroupByOrgId(ClinicPerformanceBusinessQuery query, String column) {
    List<Integer> billIds = baseBillMapper.distinctBillIds(query);
    query.setBillIds(billIds);
    return mapper.billItemStatisticsGroupByOrgId(query, column);
  }

  private PageInfo<SpecialistProjectVO> getSpecialProjectList(ClinicPerformanceBusinessQuery query) {
    SpecialistProjectQuery queryForm = new SpecialistProjectQuery();
    queryForm.setWhetherPage(query.getWhetherPage());
    queryForm.setPageNum(query.getPageNum());
    queryForm.setPageSize(query.getPageSize());
    queryForm.setSpecialistProjectIds(query.getSpecailistProjectIds());
    return clinicBaseServiceFeign.specialProjectList(queryForm);
  }


  /**
   * 根据条件导出门诊专科项目数量统计
   *
   * @param query 查询条件
   * @return
   */
  public void clinicSpecialItemExport(ClinicPerformanceBusinessQuery query, HttpServletResponse response) throws IOException {
    query.setWhetherPage(false);
    DynamicHeaderPageInfo<JSONObject> pageInfo = clinicSpecialItemList(query);
    List<JSONObject> list = pageInfo.getList();
    Map<String, String> titles = pageInfo.getMap();//表头
    ExcelUtil excelUtil = new ExcelUtil(JSONObject.class);
    String fileName = excelUtil.getFileName(query.getStartDate(), query.getEndDate(), "", "门诊年度治疗项目数量");
    excelUtil.exportExcel(response, list, "门诊年度治疗项目数量", fileName, titles);
  }
  /**
   * 根据条件查询门诊365卡销售激活统计
   *
   * @param query 查询条件
   * @return
   */
  public PageInfo<SaleActivited365CardVO> clinic365CardSaleActivitedList(ClinicPerformanceBusinessQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<BaseOrganization> orgs = getOrganization(query);
    PageInfo pageInfo = new PageInfo(orgs);
    if (query.getWhetherPage()) {
      PageHelper.clearPage();
    }
//    IVY365-731
//    嘉医汇IVY365-413
    query.setItemIds(Arrays.asList(731,413));
    query.setOrgIds(orgs.stream().map(BaseOrganization::getOrgId).collect(Collectors.toList()));
    List<BillItemStatisticsVO> vos = billItemStatisticsGroupByOrgId(query);
    Map<Integer, SaleActivited365CardVO> result = new LinkedHashMap<>();
    for (BaseOrganization org : orgs) {
      Integer orgId = org.getOrgId();
      SaleActivited365CardVO saleActivited365CardVO = new SaleActivited365CardVO();
      saleActivited365CardVO.setAbbreviation(org.getAbbreviation());
      int quantity = 0;
      for (BillItemStatisticsVO vo : vos) {
        if (vo.getOrgId().equals(orgId)) {
          quantity = vo.getQuantity();
          break;
        }
      }
      saleActivited365CardVO.setSaleNum(quantity);
      result.put(orgId, saleActivited365CardVO);
    }

//    IVY365年卡-101
    query.setItemIds(Arrays.asList(101));
    query.setItemType(1);
    query.setOrgIds(orgs.stream().map(BaseOrganization::getOrgId).collect(Collectors.toList()));
    vos = billItemStatisticsGroupByOrgId(query);
    for (BaseOrganization org : orgs) {
      Integer orgId = org.getOrgId();
      int quantity = 0;
      for (BillItemStatisticsVO vo : vos) {
        if (vo.getOrgId().equals(orgId)) {
          quantity = vo.getQuantity();
          break;
        }
      }
      SaleActivited365CardVO entity = result.get(orgId);
      if (entity == null) {
        entity = new SaleActivited365CardVO();
        entity.setAbbreviation(org.getAbbreviation());
      }
      entity.setSaleNum(quantity);
      result.put(orgId, entity);
    }

    List<BaseCoupon> baseCoupons = baseCouponMapper.selectAll();
    String kids1 = "IVY365 KIDS";
    String kids2 = "IVY365KIDS";
    String adults1 = "IVY365 ADULTS";
    String adults2 = "IVY365ADULTS";
    String youngs1 = "IVY365 YOUNGS";
    String youngs2 = "IVY365YOUNGS";
    List kidsIds = new ArrayList();
    List adultsIds = new ArrayList();
    List youngsIds = new ArrayList();
    List couponIds = new ArrayList();
    baseCoupons.forEach(vo->{
      String couponName = vo.getCouponName();
      Integer couponId = vo.getCouponId();
      if (couponName.toUpperCase().contains(kids1)||couponName.toUpperCase().contains(kids2)) {
        kidsIds.add(couponId);
        couponIds.add(couponId);
      }
      if (couponName.toUpperCase().contains(adults1)||couponName.toUpperCase().contains(adults2)) {
        adultsIds.add(couponId);
        couponIds.add(couponId);
      }
      if (couponName.toUpperCase().contains(youngs1)||couponName.toUpperCase().contains(youngs2)) {
        youngsIds.add(couponId);
        couponIds.add(couponId);
      }
    });
    query.setCouponIds(couponIds);
    List<CouponActiveVo> couponActiveVos = baseCouponMapper.couponActivedGroupByOrgId(query);
    for (BaseOrganization org : orgs) {
      Integer orgId = org.getOrgId();
      SaleActivited365CardVO entity = result.get(orgId);
      if (entity == null) {
        entity = new SaleActivited365CardVO();
        entity.setAbbreviation(org.getAbbreviation());
      }
      Long kidsQuantity = 0L;
      Long adultsQuantity = 0L;
      Long youngsQuantity = 0L;
      for (CouponActiveVo vo : couponActiveVos) {
        if (vo.getOrgId().equals(orgId+"")) {
          Long quantity = vo.getActivatedQuantity();
          Integer couponId = vo.getCouponId();
          if (kidsIds.contains(couponId)) {
            kidsQuantity += quantity;
          }
          if (adultsIds.contains(couponId)) {
            adultsQuantity += quantity;
          }
          if (youngsIds.contains(couponId)) {
            youngsQuantity += quantity;
          }
        }
      }
      entity.setAdults365Card(adultsQuantity);
      entity.setKids365Card(kidsQuantity);
      entity.setYoungs365Card(youngsQuantity);
      result.put(orgId, entity);
    }
    List<SaleActivited365CardVO> list = result.values().stream().collect(Collectors.toList());
    pageInfo.setList(list);
    return pageInfo;
  }

  /**
   * 根据条件导出门诊365卡销售激活统计
   *
   * @param query 查询条件
   * @return
   */
  public void clinic365CardSaleActivitedExport(ClinicPerformanceBusinessQuery query, HttpServletResponse response) throws IOException {
    query.setWhetherPage(false);
    List<SaleActivited365CardVO> data = clinic365CardSaleActivitedList(query).getList();
    ExcelUtil<SaleActivited365CardVO> excelUtil = new ExcelUtil<>(SaleActivited365CardVO.class);
    String fileName = excelUtil.getFileName(query.getStartDate(), query.getEndDate(),"","门诊365卡销售激活统计");
    excelUtil.exportExcel(response, data, "门诊365卡销售激活统计", fileName);
  }

  /**
   * 根据条件查询门诊补入工作量
   *
   * @param query 查询条件
   * @return list
   */
  public List<BillRecordWorkloadVO> findCouponWorkloadList(DataStatisticsQuery query) {
    return mapper.selectCouponWorkloadList(query, null);
  }

  /**
   * 根据条件查询门诊补入工作量
   *
   * @param query 查询条件
   * @return list
   */
  public List<BillRecordWorkloadVO> findCouponWorkloadList(DataStatisticsQuery query, String column) {
    return mapper.selectCouponWorkloadList(query, column);
  }

  /**
   * 计算当月免单金额
   *
   * @param result
   * @param queryFrom
   */
  public void monthCategoryFreePayment(List<CategoryInfoIncomeVO> result, BillCategoryIncomeQuery queryFrom) {
    if (StringHelper.isNotEmpty(result)) {
      List<BaseTariffInfo> items = baseTariffInfoBiz.selectListAll();
      Map<String, String> itemMap = new HashMap<>(16);
      items.forEach(vo -> itemMap.put(vo.getItemType() + "," + vo.getItemId(), vo.getItemType() + "," + vo.getCategoryId()));
      List<Integer> billIds = mapper.billIdByMonthFreePayment(queryFrom);
      Map<String, BigDecimal> frees = new HashMap<>(16);
      if (StringHelper.isNotEmpty(billIds)) {
        List<BaseBillDetail> details = mapper.selectBillDetailByBillIds(billIds);
        details = details.stream().filter(vo -> vo.getReceivedAmount().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());
        computePercentage(details);
        Map<Integer, BigDecimal> freePaymentMap = sumFreePaymentMap(billIds);
        details.forEach(detail -> {
          String key = itemMap.get(detail.getItemType() + "," + detail.getItemId());
          Integer billId = detail.getBillId();
          BigDecimal free = freePaymentMap.get(billId);
          BigDecimal amount = detail.getDiscountAmount().multiply(free == null ? BigDecimal.ZERO : free);
          BigDecimal freeAmount = frees.get(key);
          if (freeAmount == null) {
            freeAmount = BigDecimal.ZERO;
          }
          frees.put(key, freeAmount.add(amount));
        });
      }
      result.forEach(vo -> {
        String cid = vo.getCategoryType() + "," + vo.getCategoryId();
        BigDecimal freeAmount = frees.get(cid);
        if (freeAmount == null) {
          freeAmount = BigDecimal.ZERO;
        }
        vo.setTotalFreePaymentAmount(freeAmount);
        vo.setTotalAmount(vo.getTotalActualAmount().subtract(freeAmount).add(vo.getTotalCouponAmount()));
      });
    }
  }

  /**
   * 根据条件查询非本月免单金额列表
   *
   * @param query
   * @return
   */
  public PageInfo<NonMonthCategoryVO> nonMonthCategoryList(BillCategoryIncomeQuery query) {
    List<NonMonthCategoryVO> vos = mapper.nonMonthCategoryList(query);
    List<NonMonthCategoryVO> res = new ArrayList<>();
    if (StringHelper.isNotEmpty(vos)) {
      Map<String, String> categoryMap = new HashMap<>(16);
      Map<String, String> categoryName = new HashMap<>(16);
      List<BaseTariffInfo> items = baseTariffInfoBiz.selectListAll();
      if (StringHelper.isNotEmpty(items)) {
        items.forEach(vo->{
          categoryMap.put(vo.getItemType()+","+vo.getItemId(),vo.getItemType()+","+vo.getCategoryId());
          categoryName.put(vo.getItemType()+","+vo.getCategoryId(), vo.getCategoryName());
        });
      }
      Map<Integer, BigDecimal[]> billMap = new HashMap<>(16);
      for (NonMonthCategoryVO vo : vos) {
        Integer billId = vo.getBillId();
        BigDecimal[] amounts = billMap.get(billId);
        if (amounts == null) {
          amounts = new BigDecimal[]{BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO};
        }
        amounts[0] = amounts[0].add(vo.getActualAmount());// 实收
        amounts[1] = amounts[1].add(vo.getBillAmount());// 原价
        billMap.put(billId, amounts);
      }
      Set<Integer> billIds = billMap.keySet();
      EmployeePersonalWorkloadDetailQuery queryForm = new EmployeePersonalWorkloadDetailQuery();
      queryForm.setOrgId(query.getOrgId());
      queryForm.setQueryDate(query.getQueryDate());
      queryForm.setDateType((byte) 0);
      List<EmployeeFreepaymentWorkloadDetailVO> resultList =
              mapper.selectEmployeeFreepaymentWorkloadDetailList(queryForm, billIds, FREE_PAYMENT_ID);
      // 免单支付
      if (StringHelper.isNotEmpty(resultList)) {
        Map<Integer, EmployeeFreepaymentWorkloadDetailVO> billPayIds = resultList.stream()
                .collect(Collectors.toMap(EmployeeFreepaymentWorkloadDetailVO::getBillPayId, Function.identity()));
        billIds = resultList.stream().map(EmployeeFreepaymentWorkloadDetailVO::getBillId).collect(Collectors.toSet());
        List<BaseBillDetail> details = mapper.selectBillDetailByBillIds(billIds);
        details = details.stream().filter(vo -> vo.getReceivedAmount().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());
        computePercentage(details);
        Map<Integer, List<BaseBillPayDetailVO>> freePaymentMap = sumFreePaymentMapByBillPayIds(billPayIds.keySet());
        Map<String, BigDecimal> freeMap = new LinkedHashMap<>(16);
        details.forEach(detail -> {
          Integer billId = detail.getBillId();
          String cid = categoryMap.get(detail.getItemType() + "," + detail.getItemId());
          List<BaseBillPayDetailVO> list = freePaymentMap.get(billId);
          if (StringHelper.isNotEmpty(list)) {
            list.forEach(vo -> {
              Integer billPayId = vo.getBillPayId();
              String key = billId + "," + billPayId + "," + cid;
              BigDecimal free = vo.getPrincipalAmount();
              BigDecimal amount = detail.getDiscountAmount().multiply(free == null ? BigDecimal.ZERO : free);
              BigDecimal freeAmount = freeMap.get(key);
              if (freeAmount == null) {
                freeAmount = BigDecimal.ZERO;
              }
              freeMap.put(key, freeAmount.add(amount));
              BigDecimal[] billAmounts = billMap.get(billId);
              if (billAmounts == null) {
                billAmounts = new BigDecimal[]{BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO};
              }
              billAmounts[2] = billAmounts[2].add(amount);
              billMap.put(billId, billAmounts);
            });
          }
        });

        for (Map.Entry<String, BigDecimal> entry : freeMap.entrySet()) {
          String[] keys = entry.getKey().split(",");
          Integer billId = Integer.parseInt(keys[0]);
          Integer billPayId = Integer.parseInt(keys[1]);
          Integer itemType = Integer.parseInt(keys[2]);
          Integer categoryId = Integer.parseInt(keys[3]);
          BigDecimal freeAmount = entry.getValue();
          if (freeAmount == null) {
            freeAmount = BigDecimal.ZERO;
          }
          BigDecimal[] billAmounts = billMap.get(billId);
          if (billAmounts == null) {
            billAmounts = new BigDecimal[]{BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO};
          }
          NonMonthCategoryVO vo = new NonMonthCategoryVO();
          vo.setFreeAmount(freeAmount);
          vo.setActualAmount(billAmounts[0]);
          vo.setBillAmount(billAmounts[1]);
          vo.setFreeBillAmount(billAmounts[2]);
          vo.setCategoryId(categoryId);
          vo.setCategoryName(categoryName.get(itemType+","+categoryId));
          EmployeeFreepaymentWorkloadDetailVO workload = billPayIds.get(billPayId);
          if (workload != null) {
            vo.setPatientName(workload.getPatientName());
            vo.setFreeDate(workload.getChargeDate());
            vo.setAbbreviation(workload.getAbbreviation());
          }
          res.add(vo);
        }
      }
    }
    if (query.getWhetherPage()) {
      return PageUtl.doPage(query.getPageNum(), query.getPageSize(), res);
    }
    return new PageInfo<>(res);
  }

  /**
   * 根据条件导出非本月免单金额明细列表
   *
   * @param response 响应
   * @param query 查询条件
   * @return void
   */
  public void nonMonthCategoryExport(HttpServletResponse response, BillCategoryIncomeQuery query) throws IOException {
    query.setWhetherPage(false);
    List<NonMonthCategoryVO> list = nonMonthCategoryList(query).getList();
    ExcelUtil<NonMonthCategoryVO> excelUtil = new ExcelUtil<>(NonMonthCategoryVO.class);
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    String fileName = excelUtil.getFileName(query.getQueryDate(),"", organization.getAbbreviation(),"非本月免单金额明细");
    excelUtil.exportExcel(response, list, "非本月免单金额明细", fileName);
  }

  /**
   * 根据条件查询员工个人收费项目工作量明细列表
   *
   * @param query 查询条件
   * @return list
   */
  public PageInfo<PersonalBillItemTollAndWorkloadDetailVO>
      findPersonalBillItemAndWorkloadDetailList(PersonalBillItemTollAndWorkloadQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<PersonalBillItemTollAndWorkloadDetailVO> resultList =
        mapper.selectPersonalBillItemAndWorkloadDetail(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件按月份分组门诊补入工作量
   *
   * @param query
   * @return
   */
  public List<BillRecordWorkloadVO> findCouponWorkloadGroupByPrivilegeDate(DataStatisticsQuery query) {
    return mapper.selectCouponWorkloadGroupByPrivilegeDate(query);
  }
}
