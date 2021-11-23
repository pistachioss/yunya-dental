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
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.PageUtl;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.*;
import com.yunya.report.ultimate.mapper.*;
import org.apache.commons.lang3.ObjectUtils;
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
import static java.util.stream.Collectors.toMap;

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
  /** 员工 */
  @Autowired private BaseEmployeeMapper employeeMapper;
  /** 账单 */
  @Autowired private BaseBillMapper baseBillMapper;
  /** 收费记录 */
  @Autowired private BaseBillPayBiz baseBillPayBiz;
  /** 收费明细记录 */
  @Autowired private BaseBillPayDetailBiz baseBillPayDetailBiz;
  /** 账单收费明细 */
  @Autowired private BaseBillPayDetailMapper baseBillPayDetailMapper;
  /** 诊所基础信息 */
  @Autowired private RemoteClinicBaseServiceFeign clinicBaseServiceFeign;
  /** 患者信息 */
  @Autowired private PatientBaseInfoBiz patientBaseInfoBiz;
  /** 患者来源 */
  @Autowired private BasePatientOriginMapper basePatientOriginMapper;
  /** 卡券 */
  @Autowired private BaseCouponMapper baseCouponMapper;
  /** 项目信息 */
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
      BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
      resultList.forEach(
          vo -> {
            if (ObjectUtils.isNotEmpty(organization)) {
              vo.setAbbreviation(organization.getAbbreviation());
            }
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
    query.setBillIds(computePercentage(details));
    Map<Integer, BigDecimal> freePaymentMap = sumFreePaymentMap(query);
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
    String fileName = query.getStartDate() + "-" + query.getEndDate() + "员工工作量统计";
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
    String fileName = query.getStartDate() + "-" + query.getEndDate() + "员工工作量统计";
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
    List<ItemCategoryInfoVO> categoryList = baseTariffInfoBiz.findItemCategoryList();
    List<CategoryInfoIncomeVO> resultList = findCategoryIncomeList(categoryList, query);
    mergeOriginAmount(resultList, query);
    monthCategoryFreePayment(resultList, query);
    return new PageInfo<>(resultList);
  }

  private List<CategoryInfoIncomeVO> findCategoryIncomeList(
      List<ItemCategoryInfoVO> categoryList, BillCategoryIncomeQuery query) {
    List<CategoryInfoIncomeVO> resultList = new ArrayList<>();
    List<CategoryInfoIncomeVO> billList = mapper.selectCategoryIncomeList(query);
    Map<String, CategoryInfoIncomeVO> billMap = new HashMap<>(16);
    billList.forEach(vo -> billMap.put(vo.getCategoryType() + "," + vo.getCategoryId(), vo));
    categoryList.forEach(
        vo -> {
          Integer categoryId = vo.getCategoryId();
          Byte categoryType = vo.getItemType();
          CategoryInfoIncomeVO infoIncomeVO = new CategoryInfoIncomeVO();
          infoIncomeVO.setCategoryId(categoryId);
          infoIncomeVO.setCategoryName(vo.getCategoryName());
          infoIncomeVO.setCategoryType(categoryType);
          CategoryInfoIncomeVO billVO = billMap.get(categoryType + "," + categoryId);
          BigDecimal totalDiscountAmount = BigDecimal.ZERO;
          BigDecimal totalCouponAmount = BigDecimal.ZERO;
          BigDecimal totalOriginalAmount = BigDecimal.ZERO;
          BigDecimal totalActualAmount = BigDecimal.ZERO;
          if (billVO != null) {
            totalDiscountAmount = billVO.getTotalDiscountAmount();
            totalCouponAmount = billVO.getTotalCouponAmount();
            totalOriginalAmount = billVO.getTotalOriginalAmount();
            totalActualAmount = billVO.getTotalActualAmount();
          }
          infoIncomeVO.setTotalDiscountAmount(totalDiscountAmount);
          infoIncomeVO.setTotalCouponAmount(totalCouponAmount);
          infoIncomeVO.setTotalOriginalAmount(totalOriginalAmount);
          infoIncomeVO.setTotalActualAmount(totalActualAmount);
          resultList.add(infoIncomeVO);
        });
    return resultList;
  }

  /**
   * 查询当月账单的项目大类的原价，计算应收总额
   *
   * @param resultList
   * @param query
   */
  private void mergeOriginAmount(
      List<CategoryInfoIncomeVO> resultList, BillCategoryIncomeQuery query) {
    List<CategoryInfoIncomeVO> originList = mapper.selectOriginalAmountGroupByCategory(query);
    if (StringHelper.isNotEmpty(originList) && StringHelper.isNotEmpty(resultList)) {
      resultList.forEach(
          vo ->
              originList.forEach(
                  origin -> {
                    if (vo.getCategoryId().equals(origin.getCategoryId())
                        && vo.getCategoryType().equals(origin.getCategoryType())) {
                      BigDecimal totalOriginalAmount = origin.getTotalOriginalAmount();
                      vo.setTotalOriginalAmount(totalOriginalAmount);
                      vo.setTotalActualAmount(
                          totalOriginalAmount.subtract(vo.getTotalDiscountAmount()));
                    }
                  }));
    }
  }

  /**
   * 公司端报表-财务报表-分类收入汇总-导出
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportCategoryIncome(HttpServletResponse response, BillCategoryIncomeQuery query)
      throws IOException {
    query.setWhetherPage(false);
    List<CategoryInfoIncomeVO> list = findCategoryIncomeList(query).getList();
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
    String fileName = query.getStartDate() + "-" + query.getEndDate() + "实收工作量统计明细表";
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
    String fileName = query.getStartDate() + "-" + query.getEndDate() + "已收工作量统计明细表";
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
    String fileName = query.getStartDate() + "-" + query.getEndDate() + "补入工作量统计明细表";
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
    workloadQuery.setStartDate(query.getStartDate());
    workloadQuery.setEndDate(query.getEndDate());
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
                  toMap(EmployeeFreepaymentWorkloadDetailVO::getBillPayId, v -> BigDecimal.ZERO));
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
   * @param query
   * @return
   */
  private Map<Integer, BigDecimal> sumFreePaymentMap(EmployeeWorkloadQuery query) {
    Map<Integer, BigDecimal> result = new HashMap<>(16);
    List<BaseBillPayDetailVO> freePayments =
        baseBillPayDetailMapper.sumPayDetailListByBillIds(query, FREE_PAYMENT_ID);
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
            query.getStartDate(), query.getEndDate(), organization.getAbbreviation(), "免单支付工作量明细");
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
            query.getStartDate(), query.getEndDate(), organization.getAbbreviation(), "员工工作量统计");
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
  public PageInfo<BillItemStatisticsDetailVO> billItemStatisticsDetail(BillItemDetailQuery query) {
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
  public void billItemStatisticsDetailExport(
      BillItemDetailQuery query, HttpServletResponse response) throws IOException {
    query.setWhetherPage(false);
    PageInfo<BillItemStatisticsDetailVO> pageInfo = billItemStatisticsDetail(query);
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
    for(CouponExecutoredVO vo:list){
        vo.setOrgIds(query.getOrgIds());
    }
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
    Map<String, String> titles = pageInfo.getMap(); // 表头
    ExcelUtil excelUtil = new ExcelUtil(JSONObject.class);
    String fileName = "月营业目标完成度报表";
    excelUtil.exportExcel(response, resultList, fileName, fileName, titles);
  }

  /**
   * 各个门诊的月工作量和日工作量合计
   *
   * @param startDate
   * @param curDate
   * @return
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
    if (goalTotal.compareTo(BigDecimal.ZERO) != 0) {
      percentageTotal =
          monthTotal.divide(goalTotal, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
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
    return workloadMonthGoal((byte) 1, date, date);// 按月查
  }

  private Map<Integer, BigDecimal> workloadMonthGoal(Byte dateType, String startDate, String endDate) {
    BusinessGoalCompletedInfoQuery query = new BusinessGoalCompletedInfoQuery();
    query.setBusinessTypes(new Byte[] {1}); // 工作量
    query.setDateType(dateType);
    query.setBusinessType((byte) 1); // 工作量
    query.setStartDate("1");
    query.setEndDate("1");
    query.setOrgId(0);
    query.setDateRange(DateUtil.sliceUpDateRange(startDate, endDate));
    List<BusinessGoalVO> goalVOS = clinicBaseServiceFeign.businessGoalList(query);
    if (StringHelper.isNotEmpty(goalVOS)) {
      return goalVOS.stream()
          .collect(toMap(BusinessGoalVO::getBelongId, BusinessGoalVO::getBusinessGoal));
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
  public DynamicHeaderPageInfo<JSONObject> clinicPerformanceList(
      ClinicPerformanceBusinessQuery queryForm) {
    correctQueryDate(queryForm);
    // 门诊的目标工作量
    Map<Integer, BigDecimal> workloadGoalMap = workloadMonthGoal(queryForm.getDateType(), queryForm.getStartDate(), queryForm.getEndDate());
    List<BaseOrganization> orgs = getOrganization(queryForm);
    Integer[] orgIds = orgs.stream().map(BaseOrganization::getOrgId).toArray(Integer[]::new);
    String startDate = queryForm.getStartDate();
    String endDate = queryForm.getEndDate();
    String year = checkCrossYear(startDate, endDate);
    DataStatisticsQuery query = new DataStatisticsQuery();
    query.setDateType(queryForm.getDateType());
    query.setOrgIds(orgIds);
    List<String> curMonthList = DateUtil.sliceUpDateRange(startDate, endDate);

    // 同比：去年+查询月份范围
    String chainSDate = DateUtil.chainDate(startDate);
    String chainEDate = DateUtil.chainDate(endDate);
    String minDate = chainSDate;
    List<String> chainDateList = DateUtil.sliceUpDateRange(chainSDate, chainEDate);

    // 环比：查询条件的开始月份 + 查询月份范围的跨度值
    int range = 1; // 默认差值：1
    if (!startDate.equals(endDate)) {
      range += DateUtil.dateFieldDiff(startDate, endDate);
    }
    String preSDate = DateUtil.preDate(startDate, range);
    String preEDate = DateUtil.preDate(endDate, range);
    if (DateUtil.compareDate(minDate, preSDate) < 0) {
        minDate = preSDate;
    }
    List<String> preDateList = DateUtil.sliceUpDateRange(preSDate, preEDate);

    // 年度工作量
    String yearSDate = DateUtil.yearStart(startDate);
    String yearEDate = DateUtil.yearEnd(endDate);
    if (DateUtil.compareDate(minDate, yearSDate) < 0) {
      minDate = yearSDate;
    }
    query.setStartDate(minDate);
    query.setEndDate(yearEDate);
    List<String> yearDateList = DateUtil.sliceUpDateRange(yearSDate, yearEDate);
    Map<String, Map<Integer, BigDecimal>> workloadMap =
        baseBillPayBiz.computeWorkloadGroupOrgIdAndMonth(query);
    DynamicHeaderPageInfo pageInfo = new DynamicHeaderPageInfo<>();
    List<JSONObject> result = new ArrayList<>();
    if (StringHelper.isNotEmpty(orgs)) {
      BigDecimal actualTotal = BigDecimal.ZERO;
      BigDecimal goalTotal = BigDecimal.ZERO;
      BigDecimal completedTotal = BigDecimal.ZERO;
      BigDecimal chainTotal = BigDecimal.ZERO;
      BigDecimal preTotal = BigDecimal.ZERO;
      BigDecimal yearTotal = BigDecimal.ZERO;
      JSONObject actual = init(startDate, endDate, "实际值");
      JSONObject goals = init(startDate, endDate, "目标值");
      JSONObject completed = init(startDate, endDate, "完成度");
      JSONObject preDiff = init(preSDate, preEDate, "环比值");
      JSONObject chainDiff = init(chainSDate, chainEDate, "同比值");
      JSONObject curYear = init(year, year, "年度总工作量");
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
        actual.put(key, workloads.setScale(2, BigDecimal.ROUND_HALF_UP));
        goals.put(key, goal);
        completed.put(key, completedPercentage.toString() + "%");
        BigDecimal chainWorkload = computeOrgWorkload(orgId, chainDateList, workloadMap);
        BigDecimal preWorkload = computeOrgWorkload(orgId, preDateList, workloadMap);
        BigDecimal yearWorkload = computeOrgWorkload(orgId, yearDateList, workloadMap);
        chainDiff.put(key, chainWorkload.setScale(2, BigDecimal.ROUND_HALF_UP));
        preDiff.put(key, preWorkload.setScale(2, BigDecimal.ROUND_HALF_UP));
        curYear.put(key, yearWorkload.setScale(2, BigDecimal.ROUND_HALF_UP));
        actualTotal = actualTotal.add(workloads.setScale(2, BigDecimal.ROUND_HALF_UP));
        goalTotal = goalTotal.add(goal);
        chainTotal = chainTotal.add(chainWorkload.setScale(2, BigDecimal.ROUND_HALF_UP));
        preTotal = preTotal.add(preWorkload.setScale(2, BigDecimal.ROUND_HALF_UP));
        yearTotal = yearTotal.add(yearWorkload.setScale(2, BigDecimal.ROUND_HALF_UP));
      }
      map.put("total", "合计");
      actual.put("total", actualTotal);
      goals.put("total", goalTotal);
      if (goalTotal.compareTo(BigDecimal.ZERO) != 0) {
        completedTotal =
            actualTotal
                .divide(goalTotal, 2, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal(100));
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
    pageInfo.setPageNum(queryForm.getPageNum());
    pageInfo.setPageSize(queryForm.getPageSize());
    pageInfo.setTotal(result.size());
    pageInfo.setList(result);
    return pageInfo;
  }

  private void correctQueryDate(ClinicPerformanceBusinessQuery queryForm) {
    int dateType = queryForm.getDateType().intValue();
    String startDate = queryForm.getStartDate();
    String endDate = queryForm.getEndDate();
    try {
      if (dateType == 1) {
        queryForm.setStartDate(startDate.substring(0, 7));
        queryForm.setEndDate(endDate.substring(0, 7));
      } else if (dateType == 2) {
        queryForm.setStartDate(startDate.substring(0, 4));
        queryForm.setEndDate(endDate.substring(0, 4));
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new ClientServiceException("查询日期参数格式错误",PARAMETERS_IS_ILLEGAL);
    }
  }

    /**
     * 检查日期是否跨年
     *
     * @param startDate
     * @param endDate
     * @return
     */
    private String checkCrossYear(String startDate, String endDate) {
        String year = startDate.substring(0, 4); // 年份
        if (!year.equals(endDate.substring(0, 4))) {
            throw new ClientServiceException("查询日期不能跨年！", PARAMETERS_IS_ILLEGAL);
        }
        return year;
    }

    private BigDecimal computeOrgWorkload(
      Integer orgId,
      List<String> chainMonthList,
      Map<String, Map<Integer, BigDecimal>> workloadMap) {
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
   * @param sMonth
   * @param eMonth
   * @param value
   */
  private JSONObject init(String sMonth, String eMonth, String value) {
    JSONObject object = new JSONObject();
    String month = sMonth.replaceAll("-", ".");
    if (!sMonth.equals(eMonth)) {
      month = month + "-" + eMonth.replaceAll("-", ".");
    }
    object.put("date", month);
    object.put("name", value);
    return object;
  }

  /**
   * 门诊业绩导出
   *
   * @param query
   * @param response
   */
  public void clinicPerformanceExport(
      ClinicPerformanceBusinessQuery query, HttpServletResponse response) throws IOException {
    query.setWhetherPage(false);
    DynamicHeaderPageInfo<JSONObject> pageInfo = clinicPerformanceList(query);
    List<JSONObject> list = pageInfo.getList();
    Map<String, String> titles = pageInfo.getMap(); // 表头
    ExcelUtil excelUtil = new ExcelUtil(JSONObject.class);
    String endDate = "";
    if (!query.getStartDate().equals(query.getEndDate())) {
      endDate = query.getEndDate();
    }
    String fileName = excelUtil.getFileName(query.getStartDate(), endDate, "", "门诊工作量统计");
    excelUtil.exportExcel(response, list, "门诊工作量统计", fileName, titles);
  }

  /**
   * 根据条件查询初诊来源数量分析
   *
   * @param query 查询条件
   * @return
   */
  public DynamicHeaderPageInfo<JSONObject> clinicFirstVisitSourceList(
      ClinicPerformanceBusinessQuery query) {
    String startDate = query.getStartDate().substring(0, 7);
    String endDate = query.getEndDate().substring(0, 7);
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<BasePatientOrigin> origins = basePatientOriginMapper.selectPatientOriginList(query);
    //    BasePatientOrigin basePatientOrigin = new BasePatientOrigin();
    //    basePatientOrigin.setOriginType(0);
    //    basePatientOrigin.setName("未知来源");
    //    origins.add(basePatientOrigin);
    DynamicHeaderPageInfo pageInfo = new DynamicHeaderPageInfo<>(origins);
    if (query.getWhetherPage()) {
      PageHelper.clearPage();
    }
    List<BaseOrganization> orgs = getOrganization(query);
    if (StringHelper.isEmpty(query.getOriginTypes())) {
      query.setOriginTypes(
          origins.stream().map(BasePatientOrigin::getOriginType).collect(Collectors.toSet()));
    }
    List<BaseTreatmentProcessVO> patientIds = patientBaseInfoBiz.firstVisitPatientList(query);
    List<PatientFirstVisitSourceVO> patients =
        patientBaseInfoBiz.clinicFirstVisitSourceList(
            query,
            patientIds.stream()
                .map(BaseTreatmentProcess::getPatientId)
                .collect(Collectors.toSet()));
    Map<String, Integer> originMap = new HashMap<>(16);
    patients.forEach(
        vo -> originMap.put(vo.getOriginType() + "," + vo.getOrgId(), vo.getFirstVisitCount()));
    List<JSONObject> result = new ArrayList<>();
    if (StringHelper.isNotEmpty(origins)) {
      Map<String, String> map = new LinkedHashMap<>();
      map.put("date", "时间");
      map.put("name", "患者来源");
      for (BasePatientOrigin vo : origins) {
        JSONObject object = init(startDate, endDate, vo.getName());
        object.put(
            "total", computeOrgPatientCount(vo.getOriginType() + "", object, map, orgs, originMap));
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
  private Integer computeOrgPatientCount(
      String firstKey,
      JSONObject object,
      Map<String, String> map,
      List<BaseOrganization> orgs,
      Map<String, Integer> originMap) {
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
  public void clinicFirstVisitSourceExport(
      ClinicPerformanceBusinessQuery query, HttpServletResponse response) throws IOException {
    query.setWhetherPage(false);
    DynamicHeaderPageInfo<JSONObject> pageInfo = clinicFirstVisitSourceList(query);
    List<JSONObject> list = pageInfo.getList();
    Map<String, String> titles = pageInfo.getMap(); // 表头
    ExcelUtil excelUtil = new ExcelUtil<>(JSONObject.class);
    String endDate = "";
    if (!query.getStartDate().equals(query.getEndDate())) {
      endDate = query.getEndDate();
    }
    String fileName = excelUtil.getFileName(query.getStartDate(), endDate, "", "门诊初诊来源数量统计");
    excelUtil.exportExcel(response, list, "门诊初诊来源数量统计", fileName, titles);
  }

  /**
   * 根据条件查询门诊专科项目数量统计
   *
   * @param query 查询条件
   * @return
   */
  public DynamicHeaderPageInfo<JSONObject> clinicSpecialItemList(
      ClinicPerformanceBusinessQuery query) {
    String startDate = query.getStartDate().substring(0, 7);
    String endDate = query.getEndDate().substring(0, 7);
    PageInfo<SpecialistProjectVO> pageInfo = getSpecialProjectList(query);
    List<SpecialistProjectVO> specialItems = pageInfo.getList();
    DynamicHeaderPageInfo resPageInfo = new DynamicHeaderPageInfo();
    if (StringHelper.isNotEmpty(specialItems)) {
      Map<String, Integer> dataMap =
          mapQuantityGroupOrgItemId(findBillItemStatistics(query, specialItems));
      List<BaseOrganization> orgs = getOrganization(query);
      List<JSONObject> result = new ArrayList<>();
      if (StringHelper.isNotEmpty(specialItems)) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("date", "时间");
        map.put("name", "专科项目");
        for (SpecialistProjectVO item : specialItems) {
          JSONObject object = init(startDate, endDate, item.getSpecialistProjectName());
          Integer total = 0;
          for (BaseOrganization org : orgs) {
            Integer orgId = org.getOrgId();
            String key = orgId + "";
            int count = computeSpecialNum(item, dataMap, key);
            object.put(key, count);
            total += count;
            map.put(key, org.getAbbreviation());
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

  /**
   * 分组统计各个项目在各门诊的数量
   *
   * @param list
   * @return
   */
  private Map<String, Integer> mapQuantityGroupOrgItemId(List<BillItemStatisticsVO> list) {
    Map<String, Integer> dataMap = new HashMap<>(16);
    if (StringHelper.isNotEmpty(list)) {
      list.forEach(
          vo ->
              dataMap.put(
                  vo.getItemType() + "," + vo.getItemId() + "," + vo.getOrgId(), vo.getQuantity()));
    }
    return dataMap;
  }

  /**
   * 统计专科项目数量
   *
   * @param item
   * @param dataMap
   * @param orgId
   * @return
   */
  private int computeSpecialNum(
      SpecialistProjectVO item, Map<String, Integer> dataMap, String orgId) {
    String[] ids = StringHelper.split(item.getTariffItemIds(), ",");
    String[] oralIds = StringHelper.split(item.getOralIds(), ",");
    int count = getQuantityById(dataMap, "0", ids, orgId);
    count += getQuantityById(dataMap, "1", oralIds, orgId);
    return count;
  }

  /**
   * 获取当前项目在给定门诊下的数量
   *
   * @param dataMap
   * @param itemType
   * @param ids
   * @param orgId
   * @return
   */
  private int getQuantityById(
      Map<String, Integer> dataMap, String itemType, String[] ids, String orgId) {
    int count = 0;
    if (StringHelper.isNotEmpty(ids)) {
      for (String id : ids) {
        Integer quantity = dataMap.get(itemType + "," + id + "," + orgId);
        if (quantity == null) {
          quantity = 0;
        }
        count += quantity;
      }
    }
    return count;
  }

  /**
   * 查找专科项目数量列表统计
   *
   * @param query
   * @param specialItems
   * @return
   */
  private List<BillItemStatisticsVO> findBillItemStatistics(
      ClinicPerformanceBusinessQuery query, List<SpecialistProjectVO> specialItems) {
    List<Integer> itemIds = new ArrayList<>();
    List<Integer> oralIds = new ArrayList<>();
    specialItems.forEach(
        vo -> {
          String[] itemIdStr = StringHelper.split(vo.getTariffItemIds(), ",");
          if (StringHelper.isNotEmpty(itemIdStr)) {
            for (String id : itemIdStr) {
              itemIds.add(Integer.parseInt(id));
            }
          }
          String[] oralIdStr = StringHelper.split(vo.getOralIds(), ",");
          if (StringHelper.isNotEmpty(oralIdStr)) {
            for (String id : oralIdStr) {
              oralIds.add(Integer.parseInt(id));
            }
          }
        });
    if (query.getWhetherPage()) {
      PageHelper.clearPage();
    }
    List<BillItemStatisticsVO> list = new ArrayList<>();
    setDistinctBillIds(query);
    // 价目表
    loadSpecailProjectItemList(list, query, 0, itemIds);
    // 商品表
    loadSpecailProjectItemList(list, query, 1, oralIds);
    return list;
  }

  /**
   * 设置当前条件下的账单id列表
   *
   * @param query
   */
  private void setDistinctBillIds(ClinicPerformanceBusinessQuery query) {
    List<Integer> billIds = baseBillMapper.distinctBillIds(query);
    if (StringHelper.isEmpty(billIds)) {
      billIds.add(-1); // 查不到
    }
    query.setBillIds(billIds);
  }

  /**
   * 加载专科项目的项目列表
   *
   * @param list
   * @param query
   * @param itemType
   * @param itemIds
   */
  private void loadSpecailProjectItemList(
      List<BillItemStatisticsVO> list,
      ClinicPerformanceBusinessQuery query,
      int itemType,
      List<Integer> itemIds) {
    Collection<Integer> specailistProjectIds = query.getSpecailistProjectIds();
    // 当前端不传 专科项目id列表 或者 专科项目id列表非空时查询
    if (StringHelper.isEmpty(specailistProjectIds) || StringHelper.isNotEmpty(itemIds)) {
      query.setItemType(itemType);
      query.setItemIds(itemIds);
      List<BillItemStatisticsVO> tariffs =
          mapper.billItemStatisticsGroupByOrgId(query, "item_type", "item_id");
      if (StringHelper.isNotEmpty(tariffs)) {
        list.addAll(tariffs);
      }
    }
  }

  private List<BillItemStatisticsVO> billItemStatisticsGroupByOrgId(
      ClinicPerformanceBusinessQuery query) {
    return billItemStatisticsGroupByOrgId(query, null, null);
  }

  private List<BillItemStatisticsVO> billItemStatisticsGroupByOrgId(
      ClinicPerformanceBusinessQuery query, String column1, String column2) {
    setDistinctBillIds(query);
    return mapper.billItemStatisticsGroupByOrgId(query, column1, column2);
  }

  private PageInfo<SpecialistProjectVO> getSpecialProjectList(
      ClinicPerformanceBusinessQuery query) {
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
  public void clinicSpecialItemExport(
      ClinicPerformanceBusinessQuery query, HttpServletResponse response) throws IOException {
    query.setWhetherPage(false);
    DynamicHeaderPageInfo<JSONObject> pageInfo = clinicSpecialItemList(query);
    List<JSONObject> list = pageInfo.getList();
    Map<String, String> titles = pageInfo.getMap(); // 表头
    ExcelUtil excelUtil = new ExcelUtil(JSONObject.class);
    String endDate = "";
    if (!query.getStartDate().equals(query.getEndDate())) {
      endDate = query.getEndDate();
    }
    String fileName = excelUtil.getFileName(query.getStartDate(), endDate, "", "门诊专科项目数量统计");
    excelUtil.exportExcel(response, list, "门诊专科项目数量统计", fileName, titles);
  }
  /**
   * 根据条件查询门诊365卡销售激活统计
   *
   * @param query 查询条件
   * @return
   */
  public PageInfo<SaleActivited365CardVO> clinic365CardSaleActivitedList(
      ClinicPerformanceBusinessQuery query) {
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
    query.setItemIds(Arrays.asList(731, 413));
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
    baseCoupons.forEach(
        vo -> {
          String couponName = vo.getCouponName();
          Integer couponId = vo.getCouponId();
          if (couponName.toUpperCase().contains(kids1)
              || couponName.toUpperCase().contains(kids2)) {
            kidsIds.add(couponId);
            couponIds.add(couponId);
          }
          if (couponName.toUpperCase().contains(adults1)
              || couponName.toUpperCase().contains(adults2)) {
            adultsIds.add(couponId);
            couponIds.add(couponId);
          }
          if (couponName.toUpperCase().contains(youngs1)
              || couponName.toUpperCase().contains(youngs2)) {
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
        if (vo.getOrgId().equals(orgId + "")) {
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
  public void clinic365CardSaleActivitedExport(
      ClinicPerformanceBusinessQuery query, HttpServletResponse response) throws IOException {
    query.setWhetherPage(false);
    List<SaleActivited365CardVO> data = clinic365CardSaleActivitedList(query).getList();
    ExcelUtil<SaleActivited365CardVO> excelUtil = new ExcelUtil<>(SaleActivited365CardVO.class);
    String endDate = "";
    if (!query.getStartDate().equals(query.getEndDate())) {
      endDate = query.getEndDate();
    }
    String fileName = excelUtil.getFileName(query.getStartDate(), endDate, "", "门诊365卡销售激活统计");
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
  public List<BillRecordWorkloadVO> findCouponWorkloadList(
      DataStatisticsQuery query, String column) {
    return mapper.selectCouponWorkloadList(query, column);
  }

  /**
   * 计算当月免单金额
   *
   * @param result
   * @param queryFrom
   */
  public void monthCategoryFreePayment(
      List<CategoryInfoIncomeVO> result, BillCategoryIncomeQuery queryFrom) {
    if (StringHelper.isNotEmpty(result)) {
      List<BaseTariffInfo> items = baseTariffInfoBiz.selectListAll();
      Map<String, String> itemMap = new HashMap<>(16);
      items.forEach(
          vo ->
              itemMap.put(
                  vo.getItemType() + "," + vo.getItemId(),
                  vo.getItemType() + "," + vo.getCategoryId()));
      List<Integer> billIds = mapper.billIdByMonthFreePayment(queryFrom);
      Map<String, BigDecimal> frees = new HashMap<>(16);
      if (StringHelper.isNotEmpty(billIds)) {
        List<BaseBillDetail> details = mapper.selectBillDetailByBillIds(billIds);
        details =
            details.stream()
                .filter(vo -> vo.getReceivedAmount().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());
        computePercentage(details);
        EmployeeWorkloadQuery query = new EmployeeWorkloadQuery();
        query.setDateType((byte) 1);
        query.setStartDate(queryFrom.getQueryDate());
        query.setEndDate(queryFrom.getQueryDate());
        query.setBillIds(billIds);
        Map<Integer, BigDecimal> freePaymentMap = sumFreePaymentMap(query);
        details.forEach(
            detail -> {
              String key = itemMap.get(detail.getItemType() + "," + detail.getItemId());
              Integer billId = detail.getBillId();
              BigDecimal free = freePaymentMap.get(billId);
              BigDecimal amount =
                  detail.getDiscountAmount().multiply(free == null ? BigDecimal.ZERO : free);
              BigDecimal freeAmount = frees.get(key);
              if (freeAmount == null) {
                freeAmount = BigDecimal.ZERO;
              }
              frees.put(key, freeAmount.add(amount));
            });
      }
      result.forEach(
          vo -> {
            String cid = vo.getCategoryType() + "," + vo.getCategoryId();
            BigDecimal freeAmount = frees.get(cid);
            if (freeAmount == null) {
              freeAmount = BigDecimal.ZERO;
            }
            vo.setTotalFreePaymentAmount(freeAmount);
            vo.setTotalAmount(
                vo.getTotalActualAmount().subtract(freeAmount).add(vo.getTotalCouponAmount()));
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
    List<NonMonthCategoryVO> res = new ArrayList<>();
    List<Integer> ids = mapper.findBillIdsByNonMonth(query);
    if (StringHelper.isNotEmpty(ids)) {
      List<NonMonthCategoryVO> vos = mapper.nonMonthCategoryList(query, ids);
      if (StringHelper.isNotEmpty(vos)) {
        Map<String, String> categoryMap = new HashMap<>(16);
        Map<String, String> categoryName = new HashMap<>(16);
        List<BaseTariffInfo> items = baseTariffInfoBiz.selectListAll();
        if (StringHelper.isNotEmpty(items)) {
          items.forEach(
              vo -> {
                categoryMap.put(
                    vo.getItemType() + "," + vo.getItemId(),
                    vo.getItemType() + "," + vo.getCategoryId());
                categoryName.put(vo.getItemType() + "," + vo.getCategoryId(), vo.getCategoryName());
              });
        }
        Map<String, BigDecimal> couponMap = new HashMap<>(16);
        query.setPrivilegeDate(query.getQueryDate());
        ids = mapper.findBillIdsByNonMonth(query);
        if (StringHelper.isNotEmpty(ids)) {
          List<NonMonthCategoryVO> coupons = mapper.nonMonthCategoryList(query, ids);
          if (StringHelper.isNotEmpty(coupons)) {
            coupons.forEach(
                vo -> {
                  String key =
                      vo.getBillId()
                          + ","
                          + categoryMap.get(vo.getItemType() + "," + vo.getItemId());
                  BigDecimal couponWorkload = couponMap.get(key);
                  if (couponWorkload == null) {
                    couponWorkload = BigDecimal.ZERO;
                  }
                  couponMap.put(key, couponWorkload.add(vo.getCouponWorkload()));
                });
          }
        }
        Map<Integer, BigDecimal[]> billMap = new HashMap<>(16);
        for (NonMonthCategoryVO vo : vos) {
          Integer billId = vo.getBillId();
          BigDecimal[] amounts = billMap.get(billId);
          if (amounts == null) {
            amounts = new BigDecimal[] {BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
          }
          amounts[0] = amounts[0].add(vo.getActualAmount()); // 实收
          amounts[1] = amounts[1].add(vo.getBillAmount()); // 原价
          billMap.put(billId, amounts);
        }
        Set<Integer> billIds = billMap.keySet();
        EmployeePersonalWorkloadDetailQuery queryForm = new EmployeePersonalWorkloadDetailQuery();
        queryForm.setOrgId(query.getOrgId());
        queryForm.setStartDate(query.getQueryDate());
        queryForm.setEndDate(query.getQueryDate());
        queryForm.setDateType((byte) 1);
        List<EmployeeFreepaymentWorkloadDetailVO> resultList =
            mapper.selectEmployeeFreepaymentWorkloadDetailList(queryForm, billIds, FREE_PAYMENT_ID);
        // 免单支付
        if (StringHelper.isNotEmpty(resultList)) {
          Map<Integer, EmployeeFreepaymentWorkloadDetailVO> billPayIds =
              resultList.stream()
                  .collect(
                      toMap(
                          EmployeeFreepaymentWorkloadDetailVO::getBillPayId, Function.identity()));
          billIds =
              resultList.stream()
                  .map(EmployeeFreepaymentWorkloadDetailVO::getBillId)
                  .collect(Collectors.toSet());
          List<BaseBillDetail> details = mapper.selectBillDetailByBillIds(billIds);
          details =
              details.stream()
                  .filter(vo -> vo.getReceivedAmount().compareTo(BigDecimal.ZERO) > 0)
                  .collect(Collectors.toList());
          computePercentage(details);
          Map<Integer, List<BaseBillPayDetailVO>> freePaymentMap =
              sumFreePaymentMapByBillPayIds(billPayIds.keySet());
          Map<String, BigDecimal> freeMap = new LinkedHashMap<>(16);
          details.forEach(
              detail -> {
                Integer billId = detail.getBillId();
                String cid = categoryMap.get(detail.getItemType() + "," + detail.getItemId());
                List<BaseBillPayDetailVO> list = freePaymentMap.get(billId);
                if (StringHelper.isNotEmpty(list)) {
                  list.forEach(
                      vo -> {
                        Integer billPayId = vo.getBillPayId();
                        String key = billId + "," + billPayId + "," + cid;
                        BigDecimal free = vo.getPrincipalAmount();
                        BigDecimal amount =
                            detail
                                .getDiscountAmount()
                                .multiply(free == null ? BigDecimal.ZERO : free);
                        BigDecimal freeAmount = freeMap.get(key);
                        if (freeAmount == null) {
                          freeAmount = BigDecimal.ZERO;
                        }
                        freeMap.put(key, freeAmount.add(amount));
                        BigDecimal[] billAmounts = billMap.get(billId);
                        if (billAmounts == null) {
                          billAmounts =
                              new BigDecimal[] {BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
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
              billAmounts = new BigDecimal[] {BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
            }
            NonMonthCategoryVO vo = new NonMonthCategoryVO();
            vo.setFreeAmount(freeAmount);
            vo.setActualAmount(billAmounts[0]);
            vo.setBillAmount(billAmounts[1]);
            vo.setFreeBillAmount(billAmounts[2]);
            vo.setCategoryId(categoryId);
            vo.setCategoryName(categoryName.get(itemType + "," + categoryId));
            EmployeeFreepaymentWorkloadDetailVO workload = billPayIds.get(billPayId);
            if (workload != null) {
              vo.setPatientName(workload.getPatientName());
              vo.setFreeDate(workload.getChargeDate());
              vo.setAbbreviation(workload.getAbbreviation());
              vo.setBillDate(workload.getBillDate());
            }
            BigDecimal couponWorkload = couponMap.get(billId + "," + itemType + "," + categoryId);
            if (couponWorkload == null) {
              couponWorkload = BigDecimal.ZERO;
            }
            vo.setCouponWorkload(couponWorkload);
            res.add(vo);
          }
        }
      }
    }
    if (query.getWhetherPage()) {
      return PageUtl.doPage(query.getPageNum(), query.getPageSize(), res);
    }
    return new PageInfo<>(res);
  }

  /**
   * 根据条件查询非当月优惠金额列表
   *
   * @param query
   * @return
   */
  public PageInfo<NonDiscountVO> nonDiscountList(BillCategoryIncomeQuery query) {
    List<NonDiscountVO> res = mapper.nonDiscountList(query);
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
  public void nonMonthCategoryExport(HttpServletResponse response, BillCategoryIncomeQuery query)
      throws IOException {
    query.setWhetherPage(false);
    List<NonMonthCategoryVO> list = nonMonthCategoryList(query).getList();
    ExcelUtil<NonMonthCategoryVO> excelUtil = new ExcelUtil<>(NonMonthCategoryVO.class);
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    String fileName =
        excelUtil.getFileName(
            query.getQueryDate(), "", organization.getAbbreviation(), "非本月免单金额明细");
    excelUtil.exportExcel(response, list, "非本月免单金额明细", fileName);
  }

  /**
   * 根据条件导出非当月优惠金额列表
   *
   * @param response
   * @param query
   * @throws IOException
   */
  public void nonDiscountExport(HttpServletResponse response, BillCategoryIncomeQuery query)
      throws IOException {
    query.setWhetherPage(false);
    List<NonDiscountVO> list = nonDiscountList(query).getList();
    ExcelUtil<NonDiscountVO> excelUtil = new ExcelUtil<>(NonDiscountVO.class);
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    String fileName =
        excelUtil.getFileName(
            query.getQueryDate(), "", organization.getAbbreviation(), "非本月优惠金额明细");
    excelUtil.exportExcel(response, list, "非本月优惠金额明细", fileName);
  }

  /**
   * 根据条件查询员工个人收费项目工作量明细列表
   *
   * @param query 查询条件
   * @return list
   */
  public PageInfo<PersonalBillItemReceivedWorkloadDetailVO>
      findPersonalBillItemReceivedWorkloadDetailList(PersonalBillItemTollAndWorkloadQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<PersonalBillItemReceivedWorkloadDetailVO> resultList =
        mapper.selectPersonalBillItemReceivedWorkloadDetail(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 个人收费项目已收工作量明细导出
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportPersonalBillItemReceivedWorkloadList(
      HttpServletResponse response, PersonalBillItemTollAndWorkloadQuery query) throws IOException {
    ExcelUtil<PersonalBillItemReceivedWorkloadDetailVO> excelUtil =
        new ExcelUtil<>(PersonalBillItemReceivedWorkloadDetailVO.class);
    List<PersonalBillItemReceivedWorkloadDetailVO> resultList =
        mapper.selectPersonalBillItemReceivedWorkloadDetail(query);
    String fileName = "个人收费项目已收工作量明细列表";
    BaseEmployee employee = employeeMapper.selectByPrimaryKey(query.getExecutorId());
    if (employee != null) {
      fileName = fileName + "-" + employee.getEmployeeName();
    }
    excelUtil.exportExcel(response, resultList, "个人收费项目已收工作量明细列表", fileName);
  }

  /**
   * 根据条件查询员工个人收费项目免单工作量明细列表
   *
   * @param query 查询条件
   * @return list
   */
  public PageInfo<PersonalBillItemFreeWorkloadDetailVO> findPersonalBillItemFreeWorkloadDetailList(
      PersonalBillItemTollAndWorkloadQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<PersonalBillItemFreeWorkloadDetailVO> resultList =
        mapper.selectPersonalBillItemFreeWorkloadDetail(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 个人收费项目免单工作量明细导出
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportPersonalBillItemFreeWorkloadList(
      HttpServletResponse response, PersonalBillItemTollAndWorkloadQuery query) throws IOException {
    ExcelUtil<PersonalBillItemFreeWorkloadDetailVO> excelUtil =
        new ExcelUtil<>(PersonalBillItemFreeWorkloadDetailVO.class);
    List<PersonalBillItemFreeWorkloadDetailVO> resultList =
        mapper.selectPersonalBillItemFreeWorkloadDetail(query);
    String fileName = "个人收费项目已收工作量明细列表";
    BaseEmployee employee = employeeMapper.selectByPrimaryKey(query.getExecutorId());
    if (employee != null) {
      fileName = fileName + "-" + employee.getEmployeeName();
    }
    excelUtil.exportExcel(response, resultList, "个人收费项目免单工作量明细列表", fileName);
  }

  /**
   * 根据条件查询员工个人收费项目补入工作量明细列表
   *
   * @param query 查询条件
   * @return list
   */
  public PageInfo<PersonalBillItemSupplyWorkloadDetailVO>
      findPersonalBillItemSupplyWorkloadDetailList(PersonalBillItemTollAndWorkloadQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<PersonalBillItemSupplyWorkloadDetailVO> resultList =
        mapper.selectPersonalBillItemSupplyWorkloadDetail(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 个人收费项目补入工作量明细导出
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportPersonalBillItemSupplyWorkloadList(
      HttpServletResponse response, PersonalBillItemTollAndWorkloadQuery query) throws IOException {
    ExcelUtil<PersonalBillItemSupplyWorkloadDetailVO> excelUtil =
        new ExcelUtil<>(PersonalBillItemSupplyWorkloadDetailVO.class);
    List<PersonalBillItemSupplyWorkloadDetailVO> resultList =
        mapper.selectPersonalBillItemSupplyWorkloadDetail(query);
    String fileName = "个人收费项目已收工作量明细列表";
    BaseEmployee employee = employeeMapper.selectByPrimaryKey(query.getExecutorId());
    if (employee != null) {
      fileName = fileName + "-" + employee.getEmployeeName();
    }
    excelUtil.exportExcel(response, resultList, "个人收费项目补入工作量明细列表", fileName);
  }

  /**
   * 根据条件查询员工个人收费项目退费工作量明细列表
   *
   * @param query 查询条件
   * @return list
   */
  public PageInfo<PersonalBillItemRefundWorkloadDetailVO>
      findPersonalBillItemRefundWorkloadDetailList(PersonalBillItemTollAndWorkloadQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<PersonalBillItemRefundWorkloadDetailVO> resultList =
        mapper.selectPersonalBillItemRefundWorkloadDetail(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 个人收费项目退费工作量明细导出
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportPersonalBillItemRefundWorkloadList(
      HttpServletResponse response, PersonalBillItemTollAndWorkloadQuery query) throws IOException {
    ExcelUtil<PersonalBillItemRefundWorkloadDetailVO> excelUtil =
        new ExcelUtil<>(PersonalBillItemRefundWorkloadDetailVO.class);
    List<PersonalBillItemRefundWorkloadDetailVO> resultList =
        mapper.selectPersonalBillItemRefundWorkloadDetail(query);
    String fileName = "个人收费项目已收工作量明细列表";
    BaseEmployee employee = employeeMapper.selectByPrimaryKey(query.getExecutorId());
    if (employee != null) {
      fileName = fileName + "-" + employee.getEmployeeName();
    }
    excelUtil.exportExcel(response, resultList, "个人收费项目退费工作量明细列表", fileName);
  }

  /**
   * 根据条件按月份分组门诊补入工作量
   *
   * @param query
   * @return
   */
  public List<BillRecordWorkloadVO> findCouponWorkloadGroupByPrivilegeDate(
      DataStatisticsQuery query) {
    return mapper.selectCouponWorkloadGroupByPrivilegeDate(query);
  }

  /**
   * 根据条件查询个人开单项目应收明细表
   *
   * @param query 查询条件
   * @return PageInfo<BillItemStatisticsInfoVO>
   */
  public PageInfo<BillItemStatisticsInfoVO> billItemStatisticsInfo(BillItemInfoQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    queryCategoryItem(query);
    List<BillItemStatisticsInfoVO> resultList = mapper.billItemStatiticsInfo(query);
    return new PageInfo<>(resultList);
  }

  private void queryCategoryItem(BillItemInfoQuery query) {
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
  }

  /**
   * 根据条件查询个人开单项目应收明细表导出
   *
   * @param query 查询条件
   * @return
   */
  public void billItemStatisticsInfoExport(BillItemInfoQuery query, HttpServletResponse response)
      throws IOException {
    query.setWhetherPage(false);
    PageInfo<BillItemStatisticsInfoVO> pageInfo = billItemStatisticsInfo(query);
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    List<BillItemStatisticsInfoVO> resultList = pageInfo.getList();
    ExcelUtil<BillItemStatisticsInfoVO> excelUtil = new ExcelUtil<>(BillItemStatisticsInfoVO.class);
    String fileName =
        excelUtil.getFileName(
            organization.getAbbreviation(),
            query.getStartDate(),
            query.getEndDate(),
            "个人开单项目应收明细表");
    excelUtil.exportExcel(response, resultList, "个人开单项目应收明细表", fileName);
  }

  /**
   * 根据条件查询个人开单项目实收金额统计明细表
   *
   * @param query
   * @return
   */
  public PageInfo<BillItemReceivedStatisticsVO> billItemReceivedStatistics(
      BillItemInfoQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    queryCategoryItem(query);
    List<BillItemReceivedStatisticsVO> resultList = mapper.billItemReceivedStatistics(query);
    if (StringHelper.isNotEmpty(resultList)) {
      Set<Integer> billPayIds =
          resultList.stream()
              .map(BillItemReceivedStatisticsVO::getBillPayId)
              .collect(Collectors.toSet());
      List<BillPayFreePayAmountVO> freePayAmounts =
          baseBillPayDetailBiz.findBillFreePayAmountList(billPayIds);
      if (StringHelper.isNotEmpty(freePayAmounts)) {
        Map<Integer, BigDecimal> freePaymentMap =
            freePayAmounts.stream()
                .collect(
                    toMap(
                        BillPayFreePayAmountVO::getBillPayId,
                        BillPayFreePayAmountVO::getFreePayAmount));
        resultList.forEach(
            vo -> {
              BigDecimal free = vo.getFreePaymentAmount();
              BigDecimal freeTotal = freePaymentMap.get(vo.getBillPayId());
              if (free != null && freeTotal != null) {
                free = free.multiply(freeTotal);
              } else {
                free = BigDecimal.ZERO;
              }
              vo.setFreePaymentAmount(free);
            });
      }
    }
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出个人开单项目实收明细表
   *
   * @param query 查询条件
   * @return
   */
  public void billItemReceivedStatisticsExport(
      BillItemInfoQuery query, HttpServletResponse response) throws IOException {
    query.setWhetherPage(false);
    List<BillItemReceivedStatisticsVO> resultList = billItemReceivedStatistics(query).getList();
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    ExcelUtil<BillItemReceivedStatisticsVO> excelUtil =
        new ExcelUtil<>(BillItemReceivedStatisticsVO.class);
    String fileName =
        excelUtil.getFileName(
            organization.getAbbreviation(),
            query.getStartDate(),
            query.getEndDate(),
            "个人开单项目实收明细表");
    excelUtil.exportExcel(response, resultList, "个人开单项目实收明细表", fileName);
  }
}
