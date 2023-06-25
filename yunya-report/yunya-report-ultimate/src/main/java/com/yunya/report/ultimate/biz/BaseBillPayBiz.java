package com.yunya.report.ultimate.biz;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.bo.ClinicWorkloadGroupInfoVO;
import com.yunya.feign.report.domain.query.*;
import com.yunya.feign.report.domain.vo.*;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.enums.PatientDepositAccountTypeEnum;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.framework.common.utils.poi.ExcelUtil;
import com.yunya.models.report.BaseBillPay;
import com.yunya.models.report.BaseOrganization;
import com.yunya.report.ultimate.mapper.BaseBillMapper;
import com.yunya.report.ultimate.mapper.BaseBillPayMapper;
import com.yunya.report.ultimate.mapper.BaseBillPayShareMapper;
import com.yunya.report.ultimate.mapper.BaseOrganizationMapper;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static com.yunya.framework.common.constant.ThreadPoolConstant.CUT_SLICE_500;

/**
 * 简介: 账单收费记录业务层
 *
 * @author: chow
 * @date: 2020/10/27 16:49
 * @description:
 * @since: 1.0.0
 */
@Service
public class BaseBillPayBiz extends BaseBiz<BaseBillPayMapper, BaseBillPay> {
  /** 开单明细 */
  @Autowired private BaseBillDetailBiz billDetailBiz;
  /** 收费记录明细 */
  @Autowired private BaseBillPayDetailBiz billPayDetailBiz;
  /** 组织 */
  @Autowired private BaseOrganizationMapper organizationMapper;
  /** 退费订单 */
  @Autowired private BaseRefundBiz refundBiz;
  /** 账单信息 */
  @Autowired private BaseBillMapper baseBillMapper;

  /** 账单收费分摊 */
  @Autowired private BaseBillPayShareMapper baseBillPayShareMapper;

  @Resource(name = "customizeThreadPool")
  private ExecutorService threadPool;
  
  /** 入账方式 */
  @Autowired private BaseAccountItemBiz baseAccountItemBiz;

  /**
   * 构建门诊工作量相关信息
   *
   * @param query 查询条件
   * @return 门诊工作量
   */
  public ClinicWorkloadGroupInfoVO generateClinicWorkloadInfo(DataStatisticsQuery query) {
    ClinicWorkloadGroupInfoVO resultData = new ClinicWorkloadGroupInfoVO();
    BigDecimal firstReceivedAmount = BigDecimal.ZERO;
    BigDecimal firstReceivedWorkload = BigDecimal.ZERO;
    BigDecimal firstFreePayWorkload = BigDecimal.ZERO;
    BigDecimal firstCouponWorkload = BigDecimal.ZERO;
    BigDecimal firstNotWorkload = BigDecimal.ZERO;
    BigDecimal beCollectedReceivedAmount = BigDecimal.ZERO;
    BigDecimal beCollectedReceivedWorkload = BigDecimal.ZERO;
    BigDecimal beCollectedFreePayWorkload = BigDecimal.ZERO;
    BigDecimal beCollectedCouponWorkload = BigDecimal.ZERO;
    BigDecimal beCollectedNotWorkload = BigDecimal.ZERO;
    BigDecimal arrearsReceivedAmount = BigDecimal.ZERO;
    BigDecimal arrearsReceivedWorkload = BigDecimal.ZERO;
    BigDecimal arrearsFreePayWorkload = BigDecimal.ZERO;
    BigDecimal arrearsCouponWorkload = BigDecimal.ZERO;
    BigDecimal arrearsNotWorkload = BigDecimal.ZERO;
    // 查询时间段内门诊收费记录及账单信息列表
    List<BillIdAndBillPayIdVO> vos = mapper.selectBillIdsAndBillPayIds(query);
    Set<Integer> billIds = new LinkedHashSet<>();
    Set<Integer> billPayIds = new LinkedHashSet<>();
    if (StringHelper.isNotEmpty(vos)) {
      for (BillIdAndBillPayIdVO vo : vos) {
        billIds.add(vo.getBillId());
        if (vo.getReceivedAmount().compareTo(BigDecimal.ZERO) > 0) {
          billPayIds.add(vo.getBillPayId());
        }
      }
      List<BillRecordWorkloadVO> workloadInfos =
          billDetailBiz.findBillWorkloadInfoByBillIds(billIds, true);
      List<BillRecordWorkloadVO> notWorkloadInfos =
          billDetailBiz.findBillNotWorkloadInfoByBillIds(billIds);
      List<BillPayFreePayAmountVO> freePayAmountList =
          billPayDetailBiz.findBillFreePayAmountList(billPayIds);
      for (BillIdAndBillPayIdVO vo : vos) {
        Integer billId = vo.getBillId();
        Integer billOrgId = vo.getBillOrgId();
        Date billDate = vo.getBillDate();
        BigDecimal actualAmount = vo.getActualAmount();
        Integer payeeOrgId = vo.getPayeeOrgId();
        Date payeeDate = vo.getPayeeDate();
        Integer billPayId = vo.getBillPayId();
        BigDecimal receivedAmount = vo.getReceivedAmount();
        BillRecordWorkloadVO workloadVO = getBillTotalWorkload(billId, workloadInfos);
        BigDecimal totalWorkload = workloadVO.getBillTotalWorkload();
        BillRecordWorkloadVO notWorkload = getBillTotalNotWorkload(billId, notWorkloadInfos);
        BigDecimal totalNotWorkload = notWorkload.getBillTotalNotWorkload();
        BillPayFreePayAmountVO freePayAmountVO = getBillPayFreeAmount(billPayId, freePayAmountList);
        BigDecimal freePayAmount = freePayAmountVO.getFreePayAmount();
        if (billOrgId.equals(payeeOrgId)) {
          if (billDate.equals(payeeDate)) {
            firstReceivedAmount = firstReceivedAmount.add(receivedAmount);
            firstReceivedWorkload =
                    calculateReceivedWorkload(
                            firstReceivedWorkload, receivedAmount, actualAmount, totalWorkload);
            firstFreePayWorkload =
                    calculateFreePayWorkload(firstFreePayWorkload, freePayAmount, totalWorkload);
            firstNotWorkload =
                    calculateNotWorkload(
                            firstNotWorkload,
                            receivedAmount,
                            freePayAmount,
                            actualAmount,
                            totalNotWorkload);
          } else {
            arrearsReceivedAmount = arrearsReceivedAmount.add(receivedAmount);
            arrearsReceivedWorkload =
                    calculateReceivedWorkload(
                            arrearsReceivedWorkload, receivedAmount, actualAmount, totalWorkload);
            arrearsFreePayWorkload =
                    calculateFreePayWorkload(arrearsFreePayWorkload, freePayAmount, totalWorkload);
            arrearsNotWorkload =
                    calculateNotWorkload(
                            arrearsNotWorkload,
                            receivedAmount,
                            freePayAmount,
                            actualAmount,
                            totalNotWorkload);
          }
        } else {
          beCollectedReceivedAmount = beCollectedReceivedAmount.add(receivedAmount);
          beCollectedReceivedWorkload =
                  calculateReceivedWorkload(
                          beCollectedReceivedWorkload, receivedAmount, actualAmount, totalWorkload);
          beCollectedFreePayWorkload =
                  calculateFreePayWorkload(beCollectedFreePayWorkload, freePayAmount, totalWorkload);
          beCollectedNotWorkload =
                  calculateNotWorkload(
                          beCollectedNotWorkload,
                          receivedAmount,
                          freePayAmount,
                          actualAmount,
                          totalNotWorkload);
        }
      }
      List<BillRecordWorkloadVO> couponWorkloads = billDetailBiz.findCouponWorkloadList(query);
      if (StringHelper.isNotEmpty(couponWorkloads)) {
        for (BillRecordWorkloadVO info : couponWorkloads) {
          if (info.getBillOrgId().equals(info.getPrivilegeOrgId())) {
            if (info.getFirstPrivilege()) {
              firstCouponWorkload = calculateCouponWorkload(firstCouponWorkload, info);
            } else {
              arrearsCouponWorkload = calculateCouponWorkload(arrearsCouponWorkload, info);
            }
          } else {
            beCollectedCouponWorkload = calculateCouponWorkload(beCollectedCouponWorkload, info);
          }
        }
      }
    }
    BigDecimal totalRefundWorkload = refundBiz.findTotalRefundWorkload(query);
    resultData.setFirstReceivedAmount(firstReceivedAmount);
    resultData.setFirstReceivedWorkload(firstReceivedWorkload);
    resultData.setFirstFreePayWorkload(firstFreePayWorkload);
    resultData.setFirstCouponWorkload(firstCouponWorkload);
    resultData.setFirstReceivedNotWorkload(firstNotWorkload);
    resultData.setBeCollectedReceivedAmount(beCollectedReceivedAmount);
    resultData.setBeCollectedReceivedWorkload(beCollectedReceivedWorkload);
    resultData.setBeCollectedFreePayWorkload(beCollectedFreePayWorkload);
    resultData.setBeCollectedCouponWorkload(beCollectedCouponWorkload);
    resultData.setBeCollectedNotWorkload(beCollectedNotWorkload);
    resultData.setArrearsReceivedAmount(arrearsReceivedAmount);
    resultData.setArrearsReceivedWorkload(arrearsReceivedWorkload);
    resultData.setArrearsFreePayWorkload(arrearsFreePayWorkload);
    resultData.setArrearsCouponWorkload(arrearsCouponWorkload);
    resultData.setArrearsNotWorkload(arrearsNotWorkload);
    resultData.setTotalRefundWorkload(totalRefundWorkload);
    return resultData;
  }

  /**
   * 按门诊分组统计工作量
   *
   * @param query
   * @return
   */
  public Map<Integer, BigDecimal[]> computeWorkloadGroupOrgId(DataStatisticsQuery query) {
    Map<Integer, ClinicWorkloadGroupInfoVO[]> workloadMap = new HashMap<>(16);
    List<BillOfRefundWorkloadVO> refunds = refundBiz.groupTotalRefundWorkload(query);
    List<BillChargeVO> vos = baseBillPayShareMapper.selectBillIdsAndBillPayIds(query);
    String curDate = DateTime.now().toString("yyyy-MM-dd");
    if (StringHelper.isNotEmpty(vos)) {
      for (BillChargeVO vo : vos) {
        Integer billOrgId = vo.getBillOrgId();
        Date billDate = vo.getBillDate();
        Integer payeeOrgId = vo.getPayeeOrgId();
        Date payeeDate = vo.getPayeeDate();
        BigDecimal receivedAmount = vo.getReceivedAmount();
        BigDecimal freePayAmount = vo.getFreeAmount();
        BigDecimal firstReceivedWorkload = BigDecimal.ZERO;
        BigDecimal firstCouponWorkload = BigDecimal.ZERO;
        BigDecimal firstFreePayWorkload = BigDecimal.ZERO;
        BigDecimal arrearsReceivedWorkload = BigDecimal.ZERO;
        BigDecimal arrearsCouponWorkload = BigDecimal.ZERO;
        BigDecimal arrearsFreePayWorkload = BigDecimal.ZERO;
        BigDecimal beCollectedReceivedWorkload = BigDecimal.ZERO;
        BigDecimal beCollectedCouponWorkload = BigDecimal.ZERO;
        BigDecimal beCollectedFreePayWorkload = BigDecimal.ZERO;
        if (billOrgId.equals(payeeOrgId)) {
          if (billDate.equals(payeeDate)) {
            firstReceivedWorkload = firstReceivedWorkload.add(receivedAmount);
            firstFreePayWorkload = firstFreePayWorkload.add(freePayAmount);
          } else {
            arrearsReceivedWorkload = arrearsReceivedWorkload.add(receivedAmount);
            arrearsFreePayWorkload = arrearsFreePayWorkload.add(freePayAmount);
          }
        } else {
          beCollectedReceivedWorkload = beCollectedReceivedWorkload.add(receivedAmount);
          beCollectedFreePayWorkload = beCollectedFreePayWorkload.add(freePayAmount);
        }
        ClinicWorkloadGroupInfoVO[] workloads = workloadMap.get(billOrgId);
        if (workloads == null) {
          workloads =
                  new ClinicWorkloadGroupInfoVO[] {
                          new ClinicWorkloadGroupInfoVO(true), new ClinicWorkloadGroupInfoVO(true)
                  };
        }
        ClinicWorkloadGroupInfoVO monthWorkload = workloads[0];
        monthWorkload.setFirstReceivedWorkload(
            monthWorkload.getFirstReceivedWorkload().add(firstReceivedWorkload));
        monthWorkload.setFirstCouponWorkload(
            monthWorkload.getFirstCouponWorkload().add(firstCouponWorkload));
        monthWorkload.setFirstFreePayWorkload(
            monthWorkload.getFirstFreePayWorkload().add(firstFreePayWorkload));
        monthWorkload.setArrearsReceivedWorkload(
            monthWorkload.getArrearsReceivedWorkload().add(arrearsReceivedWorkload));
        monthWorkload.setArrearsCouponWorkload(
            monthWorkload.getArrearsCouponWorkload().add(arrearsCouponWorkload));
        monthWorkload.setArrearsFreePayWorkload(
            monthWorkload.getArrearsFreePayWorkload().add(arrearsFreePayWorkload));
        monthWorkload.setBeCollectedReceivedWorkload(
            monthWorkload.getBeCollectedReceivedWorkload().add(beCollectedReceivedWorkload));
        monthWorkload.setBeCollectedCouponWorkload(
            monthWorkload.getBeCollectedCouponWorkload().add(beCollectedCouponWorkload));
        monthWorkload.setBeCollectedFreePayWorkload(
            monthWorkload.getBeCollectedFreePayWorkload().add(beCollectedFreePayWorkload));
        if (curDate.equals(DateUtil.format(payeeDate, "yyyy-MM-dd"))) { // 当天
          ClinicWorkloadGroupInfoVO curWorkload = workloads[1];
          curWorkload.setFirstReceivedWorkload(
              curWorkload.getFirstReceivedWorkload().add(firstReceivedWorkload));
          curWorkload.setFirstCouponWorkload(
              curWorkload.getFirstCouponWorkload().add(firstCouponWorkload));
          curWorkload.setFirstFreePayWorkload(
              curWorkload.getFirstFreePayWorkload().add(firstFreePayWorkload));
          curWorkload.setArrearsReceivedWorkload(
              curWorkload.getArrearsReceivedWorkload().add(arrearsReceivedWorkload));
          curWorkload.setArrearsCouponWorkload(
              curWorkload.getArrearsCouponWorkload().add(arrearsCouponWorkload));
          curWorkload.setArrearsFreePayWorkload(
              curWorkload.getArrearsFreePayWorkload().add(arrearsFreePayWorkload));
          curWorkload.setBeCollectedReceivedWorkload(
              curWorkload.getBeCollectedReceivedWorkload().add(beCollectedReceivedWorkload));
          curWorkload.setBeCollectedCouponWorkload(
              curWorkload.getBeCollectedCouponWorkload().add(beCollectedCouponWorkload));
          curWorkload.setBeCollectedFreePayWorkload(
              curWorkload.getBeCollectedFreePayWorkload().add(beCollectedFreePayWorkload));
        }
        workloadMap.put(billOrgId, workloads);
      }
      List<BillRecordWorkloadVO> couponWorkloads = findCouponWorkloadList(query);
      if (StringHelper.isNotEmpty(couponWorkloads)) {
        for (BillRecordWorkloadVO info : couponWorkloads) {
          Integer billOrgId = info.getBillOrgId();
          ClinicWorkloadGroupInfoVO[] workloads = workloadMap.get(billOrgId);
          if (workloads == null) {
            workloads =
                new ClinicWorkloadGroupInfoVO[] {
                  new ClinicWorkloadGroupInfoVO(true), new ClinicWorkloadGroupInfoVO(true)
                };
          }
          ClinicWorkloadGroupInfoVO monthWorkload = workloads[0];
          if (billOrgId.equals(info.getPrivilegeOrgId())) {
            if (info.getFirstPrivilege()) {
              monthWorkload.setFirstCouponWorkload(
                  calculateCouponWorkload(monthWorkload.getFirstCouponWorkload(), info));
            } else {
              monthWorkload.setArrearsCouponWorkload(
                  calculateCouponWorkload(monthWorkload.getArrearsCouponWorkload(), info));
            }
          } else {
            monthWorkload.setBeCollectedCouponWorkload(
                calculateCouponWorkload(monthWorkload.getBeCollectedCouponWorkload(), info));
          }
          if (curDate.equals(DateUtil.format(info.getPrivilegeDate(), "yyyy-MM-dd"))) { // 当天
            ClinicWorkloadGroupInfoVO curWorkload = workloads[1];
            if (billOrgId.equals(info.getPrivilegeOrgId())) {
              if (info.getFirstPrivilege()) {
                curWorkload.setFirstCouponWorkload(
                    calculateCouponWorkload(curWorkload.getFirstCouponWorkload(), info));
              } else {
                curWorkload.setArrearsCouponWorkload(
                    calculateCouponWorkload(curWorkload.getArrearsCouponWorkload(), info));
              }
            } else {
              curWorkload.setBeCollectedCouponWorkload(
                  calculateCouponWorkload(curWorkload.getBeCollectedCouponWorkload(), info));
            }
          }
          workloadMap.put(billOrgId, workloads);
        }
      }
    }
    Map<Integer, BigDecimal[]> refundMap = new HashMap<>(16);
    if (StringHelper.isNotEmpty(refunds)) {
      refunds.forEach(
          vo -> {
            Integer orgId = vo.getOrgId();
            BigDecimal totalRefundWorkload = vo.getTotalRefundWorkload();
            Date refundDate = vo.getRefundDate();
            BigDecimal[] refundWorkloads = refundMap.get(orgId);
            if (refundWorkloads == null) {
              refundWorkloads = new BigDecimal[] {BigDecimal.ZERO, BigDecimal.ZERO};
            }
            refundWorkloads[0] = refundWorkloads[0].add(totalRefundWorkload);
            if (curDate.equals(DateUtil.format(refundDate, "yyyy-MM-dd"))) { // 当天
              refundWorkloads[1] = refundWorkloads[1].add(totalRefundWorkload);
            }
            refundMap.put(orgId, refundWorkloads);
          });
    }
    Map<Integer, BigDecimal[]> result = new HashMap<>(16);
    if (StringHelper.isNotEmpty(workloadMap)) {
      workloadMap.forEach(
          (orgId, workloads) -> {
            BigDecimal[] refundWorkloads = refundMap.get(orgId);
            if (refundWorkloads == null) {
              refundWorkloads = new BigDecimal[] {BigDecimal.ZERO, BigDecimal.ZERO};
            }
            BigDecimal monthActualWorkload =
                computeTotalClinicWorkload(workloads[0], refundWorkloads[0]);
            BigDecimal curActualWorkload =
                computeTotalClinicWorkload(workloads[1], refundWorkloads[1]);
            result.put(orgId, new BigDecimal[] {monthActualWorkload, curActualWorkload});
          });
    }
    return result;
  }

  @Deprecated
  public Map<Integer, BigDecimal[]> computeWorkloadGroupOrgId0(DataStatisticsQuery query) {
    Map<Integer, ClinicWorkloadGroupInfoVO[]> workloadMap = new HashMap<>(16);
    List<BillOfRefundWorkloadVO> refunds = refundBiz.groupTotalRefundWorkload(query);
    List<BillIdAndBillPayIdVO> vos = mapper.selectBillIdsAndBillPayIds(query);
    String curDate = DateTime.now().toString("yyyy-MM-dd");
    if (StringHelper.isNotEmpty(vos)) {
      Map<Integer, Date> billIds = new HashMap<>(16);
      Set<Integer> billPayIds = new HashSet<>();
      for (BillIdAndBillPayIdVO vo : vos) {
        billIds.put(vo.getBillId(), vo.getPayeeDate());
        if (vo.getReceivedAmount().compareTo(BigDecimal.ZERO) > 0) {
          billPayIds.add(vo.getBillPayId());
        }
      }
      List<BillRecordWorkloadVO> workloadInfos =
          findBillWorkloadInfoByBillIds(new ArrayList<>(billIds.keySet()), true);
      List<BillPayFreePayAmountVO> freePayAmountList =
          findBillFreePayAmountList(new ArrayList<>(billPayIds));
      for (BillIdAndBillPayIdVO vo : vos) {
        Integer billId = vo.getBillId();
        Integer billOrgId = vo.getBillOrgId();
        Date billDate = vo.getBillDate();
        BigDecimal actualAmount = vo.getActualAmount();
        Integer payeeOrgId = vo.getPayeeOrgId();
        Date payeeDate = vo.getPayeeDate();
        Integer billPayId = vo.getBillPayId();
        BigDecimal receivedAmount = vo.getReceivedAmount();
        BillRecordWorkloadVO workloadVO = getBillTotalWorkload(billId, workloadInfos);
        BigDecimal totalWorkload = workloadVO.getBillTotalWorkload();
        BillPayFreePayAmountVO freePayAmountVO = getBillPayFreeAmount(billPayId, freePayAmountList);
        BigDecimal freePayAmount = freePayAmountVO.getFreePayAmount();
        BigDecimal firstReceivedWorkload = BigDecimal.ZERO;
        BigDecimal firstCouponWorkload = BigDecimal.ZERO;
        BigDecimal firstFreePayWorkload = BigDecimal.ZERO;
        BigDecimal arrearsReceivedWorkload = BigDecimal.ZERO;
        BigDecimal arrearsCouponWorkload = BigDecimal.ZERO;
        BigDecimal arrearsFreePayWorkload = BigDecimal.ZERO;
        BigDecimal beCollectedReceivedWorkload = BigDecimal.ZERO;
        BigDecimal beCollectedCouponWorkload = BigDecimal.ZERO;
        BigDecimal beCollectedFreePayWorkload = BigDecimal.ZERO;
        if (billOrgId.equals(payeeOrgId)) {
          if (billDate.equals(payeeDate)) {
            firstReceivedWorkload =
                    calculateReceivedWorkload(
                            firstReceivedWorkload, receivedAmount, actualAmount, totalWorkload);
            firstFreePayWorkload =
                    calculateFreePayWorkload(firstFreePayWorkload, freePayAmount, totalWorkload);
          } else {
            arrearsReceivedWorkload =
                    calculateReceivedWorkload(
                            arrearsReceivedWorkload, receivedAmount, actualAmount, totalWorkload);
            arrearsFreePayWorkload =
                    calculateFreePayWorkload(arrearsFreePayWorkload, freePayAmount, totalWorkload);
          }
        } else {
          beCollectedReceivedWorkload =
                  calculateReceivedWorkload(
                          beCollectedReceivedWorkload, receivedAmount, actualAmount, totalWorkload);
          beCollectedFreePayWorkload =
                  calculateFreePayWorkload(beCollectedFreePayWorkload, freePayAmount, totalWorkload);
        }
        ClinicWorkloadGroupInfoVO[] workloads = workloadMap.get(billOrgId);
        if (workloads == null) {
          workloads =
                  new ClinicWorkloadGroupInfoVO[] {
                          new ClinicWorkloadGroupInfoVO(true), new ClinicWorkloadGroupInfoVO(true)
                  };
        }
        ClinicWorkloadGroupInfoVO monthWorkload = workloads[0];
        monthWorkload.setFirstReceivedWorkload(
            monthWorkload.getFirstReceivedWorkload().add(firstReceivedWorkload));
        monthWorkload.setFirstCouponWorkload(
            monthWorkload.getFirstCouponWorkload().add(firstCouponWorkload));
        monthWorkload.setFirstFreePayWorkload(
            monthWorkload.getFirstFreePayWorkload().add(firstFreePayWorkload));
        monthWorkload.setArrearsReceivedWorkload(
            monthWorkload.getArrearsReceivedWorkload().add(arrearsReceivedWorkload));
        monthWorkload.setArrearsCouponWorkload(
            monthWorkload.getArrearsCouponWorkload().add(arrearsCouponWorkload));
        monthWorkload.setArrearsFreePayWorkload(
            monthWorkload.getArrearsFreePayWorkload().add(arrearsFreePayWorkload));
        monthWorkload.setBeCollectedReceivedWorkload(
            monthWorkload.getBeCollectedReceivedWorkload().add(beCollectedReceivedWorkload));
        monthWorkload.setBeCollectedCouponWorkload(
            monthWorkload.getBeCollectedCouponWorkload().add(beCollectedCouponWorkload));
        monthWorkload.setBeCollectedFreePayWorkload(
            monthWorkload.getBeCollectedFreePayWorkload().add(beCollectedFreePayWorkload));
        if (curDate.equals(DateUtil.format(payeeDate, "yyyy-MM-dd"))) { // 当天
          ClinicWorkloadGroupInfoVO curWorkload = workloads[1];
          curWorkload.setFirstReceivedWorkload(
              curWorkload.getFirstReceivedWorkload().add(firstReceivedWorkload));
          curWorkload.setFirstCouponWorkload(
              curWorkload.getFirstCouponWorkload().add(firstCouponWorkload));
          curWorkload.setFirstFreePayWorkload(
              curWorkload.getFirstFreePayWorkload().add(firstFreePayWorkload));
          curWorkload.setArrearsReceivedWorkload(
              curWorkload.getArrearsReceivedWorkload().add(arrearsReceivedWorkload));
          curWorkload.setArrearsCouponWorkload(
              curWorkload.getArrearsCouponWorkload().add(arrearsCouponWorkload));
          curWorkload.setArrearsFreePayWorkload(
              curWorkload.getArrearsFreePayWorkload().add(arrearsFreePayWorkload));
          curWorkload.setBeCollectedReceivedWorkload(
              curWorkload.getBeCollectedReceivedWorkload().add(beCollectedReceivedWorkload));
          curWorkload.setBeCollectedCouponWorkload(
              curWorkload.getBeCollectedCouponWorkload().add(beCollectedCouponWorkload));
          curWorkload.setBeCollectedFreePayWorkload(
              curWorkload.getBeCollectedFreePayWorkload().add(beCollectedFreePayWorkload));
        }
        workloadMap.put(billOrgId, workloads);
      }
      List<BillRecordWorkloadVO> couponWorkloads = findCouponWorkloadList(query);
      if (StringHelper.isNotEmpty(couponWorkloads)) {
        for (BillRecordWorkloadVO info : couponWorkloads) {
          Integer billOrgId = info.getBillOrgId();
          ClinicWorkloadGroupInfoVO[] workloads = workloadMap.get(billOrgId);
          if (workloads == null) {
            workloads =
                new ClinicWorkloadGroupInfoVO[] {
                  new ClinicWorkloadGroupInfoVO(true), new ClinicWorkloadGroupInfoVO(true)
                };
          }
          ClinicWorkloadGroupInfoVO monthWorkload = workloads[0];
          if (billOrgId.equals(info.getPrivilegeOrgId())) {
            if (info.getFirstPrivilege()) {
              monthWorkload.setFirstCouponWorkload(
                  calculateCouponWorkload(monthWorkload.getFirstCouponWorkload(), info));
            } else {
              monthWorkload.setArrearsCouponWorkload(
                  calculateCouponWorkload(monthWorkload.getArrearsCouponWorkload(), info));
            }
          } else {
            monthWorkload.setBeCollectedCouponWorkload(
                calculateCouponWorkload(monthWorkload.getBeCollectedCouponWorkload(), info));
          }
          if (curDate.equals(DateUtil.format(info.getPrivilegeDate(), "yyyy-MM-dd"))) { // 当天
            ClinicWorkloadGroupInfoVO curWorkload = workloads[1];
            if (billOrgId.equals(info.getPrivilegeOrgId())) {
              if (info.getFirstPrivilege()) {
                curWorkload.setFirstCouponWorkload(
                    calculateCouponWorkload(curWorkload.getFirstCouponWorkload(), info));
              } else {
                curWorkload.setArrearsCouponWorkload(
                    calculateCouponWorkload(curWorkload.getArrearsCouponWorkload(), info));
              }
            } else {
              curWorkload.setBeCollectedCouponWorkload(
                  calculateCouponWorkload(curWorkload.getBeCollectedCouponWorkload(), info));
            }
          }
          workloadMap.put(billOrgId, workloads);
        }
      }
    }
    Map<Integer, BigDecimal[]> refundMap = new HashMap<>(16);
    if (StringHelper.isNotEmpty(refunds)) {
      refunds.forEach(
          vo -> {
            Integer orgId = vo.getOrgId();
            BigDecimal totalRefundWorkload = vo.getTotalRefundWorkload();
            Date refundDate = vo.getRefundDate();
            BigDecimal[] refundWorkloads = refundMap.get(orgId);
            if (refundWorkloads == null) {
              refundWorkloads = new BigDecimal[] {BigDecimal.ZERO, BigDecimal.ZERO};
            }
            refundWorkloads[0] = refundWorkloads[0].add(totalRefundWorkload);
            if (curDate.equals(DateUtil.format(refundDate, "yyyy-MM-dd"))) { // 当天
              refundWorkloads[1] = refundWorkloads[1].add(totalRefundWorkload);
            }
            refundMap.put(orgId, refundWorkloads);
          });
    }
    Map<Integer, BigDecimal[]> result = new HashMap<>(16);
    if (StringHelper.isNotEmpty(workloadMap)) {
      workloadMap.forEach(
          (orgId, workloads) -> {
            BigDecimal[] refundWorkloads = refundMap.get(orgId);
            if (refundWorkloads == null) {
              refundWorkloads = new BigDecimal[] {BigDecimal.ZERO, BigDecimal.ZERO};
            }
            BigDecimal monthActualWorkload =
                computeTotalClinicWorkload(workloads[0], refundWorkloads[0]);
            BigDecimal curActualWorkload =
                computeTotalClinicWorkload(workloads[1], refundWorkloads[1]);
            result.put(orgId, new BigDecimal[] {monthActualWorkload, curActualWorkload});
          });
    }
    return result;
  }

  /**
   * 按月份分组求已收工作量合计
   *
   * @return
   */
  public List<BillWorkloadVO> findReceivedWorkloadsGroupByMonth(DataStatisticsQuery query) {
    return mapper.selectRecievedWorkloadsGroupByMonth(query);//1.6
  }

  /**
   * 计算工作总量，按门诊和月份分组
   * @param query
   * @param workloads
   * @return
   */
  public Map<String, Map<Integer, BigDecimal>> computeWorkloadGroupOrgIdAndMonth(
          DataStatisticsQuery query, List<BillWorkloadVO> workloads) {
    Map<String, Map<Integer, BigDecimal>> result = new HashMap<>(16);
    Set<Integer> billIds =
        workloads.stream().map(BillWorkloadVO::getBillId).collect(Collectors.toSet());
    List<BillRecordWorkloadVO> workloadInfos =
        findBillWorkloadInfoByBillIds(new ArrayList<>(billIds), true);//1.7, 2.3
    Map<Integer, BigDecimal> totalWorkloadMaps =
        workloadInfos.stream()
            .collect(
                Collectors.toMap(
                    BillRecordWorkloadVO::getBillId, BillRecordWorkloadVO::getBillTotalWorkload));
    List<BillRecordWorkloadVO> couponWorkloads = findCouponWorkloadGroupByPrivilegeDate(query);
    List<BillOfRefundWorkloadVO> totalRefundWorkload =
        refundBiz.findTotalRefundWorkloadGroupByMonth(query);
    if (StringHelper.isNotEmpty(workloads)) {
      workloads.forEach(
          vo -> {
            String month = vo.getMonth();
            Integer orgId = vo.getBillOrgId();
            BigDecimal totalWorkload = totalWorkloadMaps.get(vo.getBillId());
            if (totalWorkload == null || totalWorkload.compareTo(BigDecimal.ZERO) <= 0) {
              return;
            }
            BigDecimal actualAmount = vo.getActualAmount();
            BigDecimal receivedAmount = vo.getReceivedAmount();
            BigDecimal workloaded = BigDecimal.ZERO;
            BigDecimal freePayAmount = vo.getFreePayAmount();
            BigDecimal freeWorkload = BigDecimal.ZERO;
            if (actualAmount.compareTo(BigDecimal.ZERO) > 0
                && receivedAmount.compareTo(BigDecimal.ZERO) > 0) {
              workloaded =
                  totalWorkload
                      .divide(actualAmount, 8, BigDecimal.ROUND_HALF_UP)
                      .multiply(receivedAmount)
                      .setScale(4, BigDecimal.ROUND_HALF_UP);
            }

            if (freePayAmount.compareTo(BigDecimal.ZERO) > 0) {
              if (freePayAmount.compareTo(totalWorkload) > 0) {
                freePayAmount = totalWorkload;
              }
              freeWorkload = freeWorkload.add(freePayAmount);
            }
            Map<Integer, BigDecimal> res = result.get(month);
            if (res == null) {
              res = new HashMap<>(16);
            }
            BigDecimal workload = res.get(orgId);
            if (workload == null) {
              workload = BigDecimal.ZERO;
            }
            res.put(orgId, workload.add(workloaded.subtract(freeWorkload)));
            result.put(month, res);
          });
    }
    if (StringHelper.isNotEmpty(couponWorkloads)) {
      couponWorkloads.forEach(
          vo -> {
            String month = vo.getMonth();
            Integer orgId = vo.getBillOrgId();
            Map<Integer, BigDecimal> res = result.get(month);
            if (res == null) {
              res = new HashMap<>(16);
            }
            BigDecimal workload = res.get(orgId);
            if (workload == null) {
              workload = BigDecimal.ZERO;
            }
            res.put(orgId, workload.add(vo.getBillTotalCouponWorkload()));
            result.put(month, res);
          });
    }
    if (StringHelper.isNotEmpty(totalRefundWorkload)) {
      totalRefundWorkload.forEach(
          vo -> {
            String month = vo.getMonth();
            Integer orgId = vo.getOrgId();
            Map<Integer, BigDecimal> res = result.get(month);
            if (res == null) {
              res = new HashMap<>(16);
            }
            BigDecimal workload = res.get(orgId);
            if (workload == null) {
              workload = BigDecimal.ZERO;
            }
            res.put(orgId, workload.subtract(vo.getTotalRefundWorkload()));
            result.put(month, res);
          });
    }
    return result;
  }

  /**
   * 计算非工作总量，按门诊和月份分组
   *
   * @param workloads
   * @return
   */
  public Map<String, Map<Integer, BigDecimal>> computeNotWorkloadGroupOrgIdAndMonth(List<BillWorkloadVO> workloads) {
    Map<String, Map<Integer, BigDecimal>> result = new HashMap<>(16);
    Set<Integer> billIds =
            workloads.stream().map(BillWorkloadVO::getBillId).collect(Collectors.toSet());
    List<BillRecordWorkloadVO> workloadInfos =
            findBillWorkloadInfoByBillIds(new ArrayList<>(billIds), false);//1.7, 2.3
    Map<Integer, BigDecimal> totalWorkloadMaps =
            workloadInfos.stream()
                    .collect(
                            Collectors.toMap(
                                    BillRecordWorkloadVO::getBillId, BillRecordWorkloadVO::getBillTotalWorkload));
    if (StringHelper.isNotEmpty(workloads)) {
      workloads.forEach(
              vo -> {
                String month = vo.getMonth();
                Integer orgId = vo.getBillOrgId();
                BigDecimal totalWorkload = totalWorkloadMaps.get(vo.getBillId());
                if (totalWorkload == null || totalWorkload.compareTo(BigDecimal.ZERO) <= 0) {
                  return;
                }
                BigDecimal actualAmount = vo.getActualAmount();
                BigDecimal receivedAmount = vo.getReceivedAmount();
                BigDecimal workloaded = BigDecimal.ZERO;
                // 非工作量=商品总应收/订单总应收*实收 + 划扣卡核销工作量（商品）
                if (actualAmount.compareTo(BigDecimal.ZERO) > 0
                        && receivedAmount.compareTo(BigDecimal.ZERO) > 0) {
                  workloaded =
                          totalWorkload
                                  .divide(actualAmount, 8, BigDecimal.ROUND_HALF_UP)
                                  .multiply(receivedAmount)
                                  .setScale(4, BigDecimal.ROUND_HALF_UP);
                }
                BigDecimal overageAmount = vo.getFreePayAmount().subtract(totalWorkload);
                if (overageAmount.compareTo(BigDecimal.ZERO)>0) {
                  workloaded = workloaded.subtract(overageAmount);
                }
                workloaded = workloaded.add(vo.getSwipeWorkload());
                Map<Integer, BigDecimal> res = result.computeIfAbsent(month, k->new HashMap<>());
                BigDecimal workload = res.computeIfAbsent(orgId, k->BigDecimal.ZERO);
                res.put(orgId, workload.add(workloaded));
              });
    }
    return result;
  }

  private List<BillRecordWorkloadVO> findCouponWorkloadGroupByPrivilegeDate(
      DataStatisticsQuery dataQuery) {
    EmployeeWorkloadQuery queryFrom = new EmployeeWorkloadQuery();
    queryFrom.setOrgIds(dataQuery.getOrgIds());
    queryFrom.setPrivilegeStartDate(dataQuery.getStartDate());
    queryFrom.setPrivilegeEndDate(dataQuery.getEndDate());
    queryFrom.setDateType(dataQuery.getDateType());
    List<Integer> billIds = baseBillMapper.distinctBillIdByOrderDate(queryFrom);
    if (StringHelper.isEmpty(billIds)) {
      return null;
    }
    List<List<Integer>> partition = Lists.partition(billIds, CUT_SLICE_500);
    CountDownLatch downLatch = new CountDownLatch(partition.size());
    List<BillRecordWorkloadVO> result = Lists.newCopyOnWriteArrayList();
    for (List<Integer> ids : partition) {
      DataStatisticsQuery query = new DataStatisticsQuery();
      query.setBillIds(ids);
      query.setStartDate(dataQuery.getStartDate());
      query.setEndDate(dataQuery.getEndDate());
      query.setOrgIds(dataQuery.getOrgIds());
      query.setDateType(dataQuery.getDateType());
      // 多线程异步插入
      threadPool.execute(
          () -> {
            try {
              result.addAll(billDetailBiz.findCouponWorkloadGroupByPrivilegeDate(query));
            } catch (Exception e) {
              e.printStackTrace();
            } finally {
              downLatch.countDown();
            }
          });
    }
    try {
      downLatch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return result;
  }

  private List<BillRecordWorkloadVO> findCouponWorkloadList(DataStatisticsQuery dataQuery) {
    EmployeeWorkloadQuery queryFrom = new EmployeeWorkloadQuery();
    queryFrom.setOrgIds(dataQuery.getOrgIds());
    queryFrom.setPrivilegeStartDate(dataQuery.getStartDate());
    queryFrom.setPrivilegeEndDate(dataQuery.getEndDate());
    queryFrom.setDateType(dataQuery.getDateType());
    List<Integer> billIds = baseBillMapper.distinctBillIdByOrderDate(queryFrom);
    if (StringHelper.isEmpty(billIds)) {
      return null;
    }
    List<List<Integer>> partition = Lists.partition(billIds, CUT_SLICE_500);
    CountDownLatch downLatch = new CountDownLatch(partition.size());
    List<BillRecordWorkloadVO> result = Lists.newCopyOnWriteArrayList();
    for (List<Integer> ids : partition) {
      DataStatisticsQuery query = new DataStatisticsQuery();
      query.setBillIds(ids);
      query.setStartDate(dataQuery.getStartDate());
      query.setEndDate(dataQuery.getEndDate());
      query.setOrgIds(dataQuery.getOrgIds());
      query.setDateType(dataQuery.getDateType());
      // 多线程异步插入
      threadPool.execute(
          () -> {
            try {
              result.addAll(billDetailBiz.findCouponWorkloadList(query, "privilege_date"));
              downLatch.countDown();
            } catch (Exception e) {
              downLatch.countDown();
              e.printStackTrace();
            }
          });
    }
    try {
      downLatch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return result;
  }

  private List<BillRecordWorkloadVO> findBillWorkloadInfoByBillIds(List<Integer> list, Boolean existsExecutor) {
    List<List<Integer>> partition = Lists.partition(list, CUT_SLICE_500);
    CountDownLatch downLatch = new CountDownLatch(partition.size());
    List<BillRecordWorkloadVO> result = Lists.newCopyOnWriteArrayList();
    for (List<Integer> ids : partition) {
      // 多线程异步插入
      threadPool.execute(
          () -> {
            try {
              result.addAll(billDetailBiz.findBillWorkloadInfoByBillIds(ids, existsExecutor));
              downLatch.countDown();
            } catch (Exception e) {
              downLatch.countDown();
              e.printStackTrace();
            }
          });
    }
    try {
      downLatch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return result;
  }

  private List<BillPayFreePayAmountVO> findBillFreePayAmountList(List<Integer> list) {
    List<List<Integer>> partition = Lists.partition(list, CUT_SLICE_500);
    CountDownLatch downLatch = new CountDownLatch(partition.size());
    List<BillPayFreePayAmountVO> result = Lists.newCopyOnWriteArrayList();
    for (List<Integer> ids : partition) {
      // 多线程异步插入
      threadPool.execute(
          () -> {
            try {
              result.addAll(billPayDetailBiz.findBillFreePayAmountList(ids));
              downLatch.countDown();
            } catch (Exception e) {
              downLatch.countDown();
              e.printStackTrace();
            }
          });
    }
    try {
      downLatch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return result;
  }

  private BigDecimal computeTotalClinicWorkload(
          ClinicWorkloadGroupInfoVO monthWorkload, BigDecimal totalRefundWorkload) {
    BigDecimal firstReceivedWorkload = monthWorkload.getFirstReceivedWorkload();
    BigDecimal firstCouponWorkload = monthWorkload.getFirstCouponWorkload();
    BigDecimal firstFreePayWorkload = monthWorkload.getFirstFreePayWorkload();
    BigDecimal arrearsReceivedWorkload = monthWorkload.getArrearsReceivedWorkload();
    BigDecimal arrearsCouponWorkload = monthWorkload.getArrearsCouponWorkload();
    BigDecimal arrearsFreePayWorkload = monthWorkload.getArrearsFreePayWorkload();
    BigDecimal beCollectedReceivedWorkload = monthWorkload.getBeCollectedReceivedWorkload();
    BigDecimal beCollectedCouponWorkload = monthWorkload.getBeCollectedCouponWorkload();
    BigDecimal beCollectedFreePayWorkload = monthWorkload.getBeCollectedFreePayWorkload();
    firstReceivedWorkload =
            firstReceivedWorkload.add(firstCouponWorkload).subtract(firstFreePayWorkload);
    firstReceivedWorkload =
            firstReceivedWorkload.add(
                    arrearsReceivedWorkload.add(arrearsCouponWorkload.subtract(arrearsFreePayWorkload)));
    firstReceivedWorkload =
            firstReceivedWorkload.add(
                    beCollectedReceivedWorkload.add(
                            beCollectedCouponWorkload.subtract(beCollectedFreePayWorkload)));
    return firstReceivedWorkload.subtract(totalRefundWorkload);
  }

  /**
   * 获取订单的实收非工作量对象
   *
   * @param billId 订单ID
   * @param workloadInfos 订单工作量对象
   * @return BillRecordWorkloadVO
   */
  private BillRecordWorkloadVO getBillTotalNotWorkload(
          Integer billId, List<BillRecordWorkloadVO> workloadInfos) {
    BillRecordWorkloadVO workloadVO = new BillRecordWorkloadVO();
    workloadVO.setBillId(billId);
    workloadVO.setBillTotalNotWorkload(BigDecimal.ZERO);
    return workloadInfos.stream()
            .filter(info -> info.getBillId().equals(billId))
            .findFirst()
            .orElse(workloadVO);
  }

  /**
   * 获取支付记录的免单金额
   *
   * @param billPayId 支付记录ID
   * @param freePayAmountList 免单支付对象
   * @return BillPayFreePayAmountVO
   */
  private BillPayFreePayAmountVO getBillPayFreeAmount(
          Integer billPayId, List<BillPayFreePayAmountVO> freePayAmountList) {
    BillPayFreePayAmountVO payFreePayAmountVO = new BillPayFreePayAmountVO();
    payFreePayAmountVO.setBillPayId(billPayId);
    payFreePayAmountVO.setFreePayAmount(BigDecimal.ZERO);
    return freePayAmountList.stream()
            .filter(vo -> vo.getBillPayId().equals(billPayId))
            .findFirst()
            .orElse(payFreePayAmountVO);
  }

  /**
   * 获取订单的实收工作量对象
   *
   * @param billId 订单ID
   * @param workloadInfos 订单工作量对象
   * @return BillRecordWorkloadVO
   */
  private BillRecordWorkloadVO getBillTotalWorkload(
          Integer billId, List<BillRecordWorkloadVO> workloadInfos) {
    BillRecordWorkloadVO workloadVO = new BillRecordWorkloadVO();
    workloadVO.setBillId(billId);
    workloadVO.setBillTotalWorkload(BigDecimal.ZERO);
    workloadVO.setBillTotalCouponWorkload(BigDecimal.ZERO);
    return workloadInfos.stream()
            .filter(info -> info.getBillId().equals(billId))
            .findFirst()
            .orElse(workloadVO);
  }

  /**
   * 计算非工作量
   *
   * @param receivedNotWorkload 已收非工作量
   * @param receivedAmount 已收金额
   * @param freePayAmount 免单金额
   * @param actualAmount 实收金额
   * @param totalNotWorkload 订单非工作量
   * @return
   */
  private BigDecimal calculateNotWorkload(
          BigDecimal receivedNotWorkload,
          BigDecimal receivedAmount,
          BigDecimal freePayAmount,
          BigDecimal actualAmount,
          BigDecimal totalNotWorkload) {
    if (actualAmount.compareTo(BigDecimal.ZERO) > 0
            && totalNotWorkload.compareTo(BigDecimal.ZERO) > 0) {
      receivedNotWorkload =
              receivedNotWorkload.add(
                      totalNotWorkload
                              .divide(actualAmount, 8, BigDecimal.ROUND_HALF_UP)
                              .multiply(receivedAmount)
                              .setScale(4, BigDecimal.ROUND_HALF_UP));
      if (freePayAmount.compareTo(totalNotWorkload) > 0) {
        // 免单非工作量
        BigDecimal freePayNotWorkload = freePayAmount.subtract(totalNotWorkload);
        receivedNotWorkload = receivedNotWorkload.subtract(freePayNotWorkload);
      }
    }
    return receivedNotWorkload;
  }

  /**
   * 计算收费补入工作量
   *
   * @param firstCouponWorkload 收费补入工作量
   * @param workloadInfo 订单工作量对象
   * @return BigDecimal - 收费补入工作量
   */
  private BigDecimal calculateCouponWorkload(
          BigDecimal firstCouponWorkload, BillRecordWorkloadVO workloadInfo) {
    return firstCouponWorkload.add(workloadInfo.getBillTotalCouponWorkload());
  }

  /**
   * 计算收费免单工作量合计
   *
   * @param freePayWorkload 收费免单工作量
   * @param freePayAmount 收费免单金额
   * @param totalWorkload 订单工作量
   * @return BigDecimal - 收费免单工作量
   */
  private BigDecimal calculateFreePayWorkload(
          BigDecimal freePayWorkload, BigDecimal freePayAmount, BigDecimal totalWorkload) {
    if (freePayAmount.compareTo(BigDecimal.ZERO) > 0) {
      if (freePayAmount.compareTo(totalWorkload) > 0) {
        freePayAmount = totalWorkload;
      }
      freePayWorkload = freePayWorkload.add(freePayAmount);
    }
    return freePayWorkload;
  }

  /**
   * 计算收费工作量
   *
   * @param receivedWorkload 收费工作量
   * @param receivedAmount 收费金额
   * @param actualAmount 实收总额
   * @param totalWorkload 开单工作量
   * @return BigDecimal - 收费工作量合计
   */
  private BigDecimal calculateReceivedWorkload(
          BigDecimal receivedWorkload,
          BigDecimal receivedAmount,
          BigDecimal actualAmount,
          BigDecimal totalWorkload) {
    if (actualAmount.compareTo(BigDecimal.ZERO) > 0
            && receivedAmount.compareTo(BigDecimal.ZERO) > 0) {
      receivedWorkload =
              receivedWorkload.add(
                      totalWorkload
                              .divide(actualAmount, 8, BigDecimal.ROUND_HALF_UP)
                              .multiply(receivedAmount)
                              .setScale(4, BigDecimal.ROUND_HALF_UP));
    }
    return receivedWorkload;
  }

  /**
   * 根据账单ID（开单记录ID）查询收费记录列表
   *
   * @param billId 账单ID
   * @return List<BaseBillPayVO>
   */
  public List<BaseBillPayVO> findBillPayList(Integer billId) {
    return mapper.selectBillPayList(billId);
  }

  /**
   * 根据条件查询账单支付记录列表
   *
   * @param query 查询条件
   * @return
   */
  public PageInfo<BillOfPayRecordVO> findBillRecordOfPayList(BillPayRecordQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<BillOfPayRecordVO> resultList = mapper.selectBillRecordOfPayList(query);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出账单收费记录列表
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportBillOfPayRecord(HttpServletResponse response, BillPayRecordQuery query)
          throws IOException {
    List<BillOfPayRecordVO> list = mapper.selectBillRecordOfPayList(query);
    ExcelUtil<BillOfPayRecordVO> excelUtil = new ExcelUtil<>(BillOfPayRecordVO.class);
    String fileName = "账单收费记录表";
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    if (null != organization) {
      fileName =
              MessageFormat.format(
                      "{0}{1}-{2}{3}",
                      organization.getAbbreviation(),
                      query.getBillStartDate(),
                      query.getBillEndDate(),
                      fileName);
    }
    excelUtil.exportExcel(response, list, "账单收费记录表", fileName);
  }

  /**
   * 根据条件查询门诊收费数据总览
   *
   * @param query 查询条件
   * @return TollDataStatisticsVO
   */
  public TollDataStatisticsVO findClinicTollDataStatistic(DataStatisticsQuery query) {
    TollDataStatisticsVO resultData = mapper.selectClinicTollDataStatistic(query);
    return resultData;
  }

  /**
   * 根据条件查询账单收费（本期账单本期首次收费）详情信息列表
   *
   * @param query 查询条件
   * @return PageInfo<StatementBillChargeDetailVO>
   */
  public PageInfo<StatementBillChargeDetailVO> findBillChargeDetailInfoList(
          StatementBillChargeDetailInfoQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<StatementBillChargeDetailVO> resultList = mapper.selectBillChargeDetailInfoList(query);
    generateBillChargeAccountItemDetail(resultList);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出门诊账单收费（本期）明细列表
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportBillChargeDetailInfoList(
          HttpServletResponse response, StatementBillChargeDetailInfoQuery query) throws IOException {
    PageInfo<StatementBillChargeDetailVO> pageInfo = findBillChargeDetailInfoList(query);
    List<StatementBillChargeDetailVO> resultList = pageInfo.getList();
    String fileName = "门诊账单收费（本期）明细列表";
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    if (null != organization) {
      fileName =
              MessageFormat.format(
                      "{0}{1}-{2}{3}",
                      organization.getAbbreviation(), query.getStartDate(), query.getEndDate(), fileName);
    }
    DynamicHeaderPageInfo<JSONObject> page = buildDynamicChargeDetails(resultList);
    List<JSONObject> result = page.getList();
    ExcelUtil excelUtil = new ExcelUtil(JSONObject.class);
    excelUtil.exportExcel(response, result, "门诊账单收费（本期）明细", fileName, page.getMap());
  }

  /**
   * 根据条件查询账单收欠费（本期账单本期收费）详情信息列表
   *
   * @param query 查询条件
   * @return PageInfo<StatementBillChargeDetailVO>
   */
  public PageInfo<StatementBillChargeDetailVO> findBillCurrentCollectDebtDetailList(
          StatementBillChargeDetailInfoQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<StatementBillChargeDetailVO> resultList =
            mapper.selectBillCurrentChargeDebtDetailList(query);
    generateBillChargeAccountItemDetail(resultList);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出账单收欠费（本期账单本期收费）详情信息列表
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportBillCollectDebtDetailList(
          HttpServletResponse response, StatementBillChargeDetailInfoQuery query) throws IOException {
    PageInfo<StatementBillChargeDetailVO> pageInfo = findBillCurrentCollectDebtDetailList(query);
    List<StatementBillChargeDetailVO> resultList = pageInfo.getList();
    String fileName = "门诊账单收欠费（本期）明细列表";
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    if (null != organization) {
      fileName =
              MessageFormat.format(
                      "{0}{1}-{2}{3}",
                      organization.getAbbreviation(), query.getStartDate(), query.getEndDate(), fileName);
    }
    DynamicHeaderPageInfo<JSONObject> page = buildDynamicChargeDetails(resultList);
    List<JSONObject> result = page.getList();
    ExcelUtil excelUtil = new ExcelUtil(JSONObject.class);
    excelUtil.exportExcel(response, result, "门诊账单收欠费（本期）明细", fileName, page.getMap());
  }

  private DynamicHeaderPageInfo<JSONObject> buildDynamicChargeDetails(List<StatementBillChargeDetailVO> resultList) {
    List<BaseAccountItemVO> payments = baseAccountItemBiz.findAllPaymentList();
    Map<String, String> titles = new LinkedHashMap<>();
    List<JSONObject> list = new ArrayList<>();
    if (StringHelper.isNotEmpty(resultList)) {
      Map<String, BigDecimal> totals = new LinkedHashMap<>(16);
      resultList.forEach(vo->{
        JSONObject obj = new JSONObject();
        obj.put("payeeDate", vo.getPayeeDate());
        obj.put("payeeOrgName", vo.getPayeeOrgName());
        obj.put("billNum", vo.getBillNum());
        obj.put("billOrgName", vo.getBillOrgName());
        obj.put("billDate", vo.getBillDate());
        obj.put("patientName", vo.getPatientName());
        obj.put("patientMobile", vo.getPatientMobile());
        obj.put("regDentistName", vo.getRegDentistName());
        obj.put("originalAmount", vo.getOriginalAmount());
        obj.put("privilegeAmount", vo.getPrivilegeAmount());
        obj.put("actualAmount", vo.getActualAmount());
        obj.put("totalReceivedAmount", vo.getTotalReceivedAmount());
        obj.put("receivedAmount", vo.getReceivedAmount());
        titles.putIfAbsent("payeeDate", "收费日期");
        titles.putIfAbsent("payeeOrgName", "代收门诊");
        titles.putIfAbsent("billNum", "账单编号");
        titles.putIfAbsent("billOrgName", "开单门诊");
        titles.putIfAbsent("billDate", "账单日期");
        titles.putIfAbsent("patientName", "患者");
        titles.putIfAbsent("patientMobile", "手机号");
        titles.putIfAbsent("regDentistName", "挂号医生");
        titles.putIfAbsent("originalAmount", "原价合计");
        titles.putIfAbsent("privilegeAmount", "优惠金额");
        titles.putIfAbsent("actualAmount", "应收金额");
        titles.putIfAbsent("totalReceivedAmount", "实收金额");
        titles.putIfAbsent("receivedAmount", "本次收费金额");
        totals.put("originalAmount", totals.computeIfAbsent("originalAmount", k->BigDecimal.ZERO).add(vo.getOriginalAmount()));
        totals.put("privilegeAmount", totals.computeIfAbsent("privilegeAmount", k->BigDecimal.ZERO).add(vo.getPrivilegeAmount()));
        totals.put("actualAmount", totals.computeIfAbsent("actualAmount", k->BigDecimal.ZERO).add(vo.getActualAmount()));
        totals.put("totalReceivedAmount", totals.computeIfAbsent("totalReceivedAmount", k->BigDecimal.ZERO).add(vo.getTotalReceivedAmount()));
        totals.put("receivedAmount", totals.computeIfAbsent("receivedAmount", k->BigDecimal.ZERO).add(vo.getReceivedAmount()));
        for (BaseAccountItemVO item : payments) {
          BigDecimal itemAmount = BigDecimal.ZERO;
          String name = item.getAccountItemName();
          for (StatementPaymentVO payment : vo.getStatementPayments()) {
            if (name.equals(payment.getAccountItemName())) {
              itemAmount = payment.getTotalAmount();
              break;
            }
          }
          obj.put(name, itemAmount);
          titles.put(name, name);
          totals.put(name, totals.computeIfAbsent(name, k -> BigDecimal.ZERO).add(itemAmount));
        }
        obj.put("payeeName", vo.getPayeeName());
        titles.put("payeeName", "收费人");
        list.add(obj);
      });
      list.add(crtTotalObj(totals));
    } else {
      // 补充表头
      titles.putIfAbsent("payeeDate", "收费日期");
      titles.putIfAbsent("payeeOrgName", "代收门诊");
      titles.putIfAbsent("billNum", "账单编号");
      titles.putIfAbsent("billOrgName", "开单门诊");
      titles.putIfAbsent("billDate", "账单日期");
      titles.putIfAbsent("patientName", "患者");
      titles.putIfAbsent("patientMobile", "手机号");
      titles.putIfAbsent("regDentistName", "挂号医生");
      titles.putIfAbsent("originalAmount", "原价合计");
      titles.putIfAbsent("privilegeAmount", "优惠金额");
      titles.putIfAbsent("actualAmount", "应收金额");
      titles.putIfAbsent("totalReceivedAmount", "实收金额");
      titles.putIfAbsent("receivedAmount", "本次收费金额");
      for (BaseAccountItemVO item : payments) {
        String name = item.getAccountItemName();
        titles.put(name, name);
      }
      titles.put("payeeName", "收费人");
    }
    DynamicHeaderPageInfo<JSONObject> pageInfo = new DynamicHeaderPageInfo<>(list);
    pageInfo.setMap(titles);
    return pageInfo;
  }

  private JSONObject crtTotalObj(Map<String, BigDecimal> datas) {
    JSONObject totalObj = new JSONObject();
    totalObj.putIfAbsent("payeeDate", "合计");
    datas.forEach((name, data)-> totalObj.put(name, data));
    return totalObj;
  }

  /**
   * 根据条件查询账单收欠费（非本期账单本期收费）详情信息列表
   *
   * @param query 查询条件
   * @return PageInfo<StatementBillChargeDetailVO>
   */
  public PageInfo<StatementBillChargeDetailVO> findOtherCollectDebtDetailList(
          StatementBillChargeDetailInfoQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<StatementBillChargeDetailVO> resultList =
            mapper.selectBillOtherChargeDebtDetailList(query);
    generateBillChargeAccountItemDetail(resultList);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出账单收欠费（非本期账单本期收费）详情信息列表
   *
   * @param response http响应
   * @param query 查询条件
   */
  public void exportBillOtherCollectDebtDetailList(
          HttpServletResponse response, StatementBillChargeDetailInfoQuery query) throws IOException {
    PageInfo<StatementBillChargeDetailVO> pageInfo = findOtherCollectDebtDetailList(query);
    List<StatementBillChargeDetailVO> resultList = pageInfo.getList();
    String fileName = "门诊账单收欠费（非本期）明细列表";
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    if (null != organization) {
      fileName =
              MessageFormat.format(
                      "{0}{1}-{2}{3}",
                      organization.getAbbreviation(), query.getStartDate(), query.getEndDate(), fileName);
    }
    DynamicHeaderPageInfo<JSONObject> page = buildDynamicChargeDetails(resultList);
    List<JSONObject> result = page.getList();
    ExcelUtil excelUtil = new ExcelUtil(JSONObject.class);
    excelUtil.exportExcel(response, result, "门诊账单收欠费（非本期）明细", fileName, page.getMap());
  }

  /**
   * 根据条件查询门诊账单代收详情信息列表
   *
   * @param query 查询条件
   * @return
   */
  public PageInfo<StatementBillChargeDetailVO> findCurrentBillCollectionDetailList(
          StatementBillChargeDetailInfoQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<StatementBillChargeDetailVO> resultList =
            mapper.selectCurrentBillCollectionDetailList(query);
    generateBillChargeAccountItemDetail(resultList);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出诊所代收(本期)记录明细
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportCurrentBillCollectionDetailList(
          HttpServletResponse response, StatementBillChargeDetailInfoQuery query) throws IOException {
    PageInfo<StatementBillChargeDetailVO> pageInfo = findCurrentBillCollectionDetailList(query);
    List<StatementBillChargeDetailVO> list = pageInfo.getList();
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    String fileName = "诊所代收(本期)记录明细列表";
    if (null != organization) {
      String abbreviation = organization.getAbbreviation();
      fileName = abbreviation + fileName;
    }
    DynamicHeaderPageInfo<JSONObject> page = buildDynamicChargeDetails(list);
    List<JSONObject> result = page.getList();
    ExcelUtil excelUtil = new ExcelUtil(JSONObject.class);
    excelUtil.exportExcel(response, result, "诊所代收(本期)记录明细", fileName, page.getMap());
  }

  /**
   * 根据条件查询门诊账单代（非本期）收详情信息列表
   *
   * @param query 查询条件
   * @return PageInfo<StatementBillChargeDetailVO>
   */
  public PageInfo<StatementBillChargeDetailVO> findOtherBillCollectionDetailList(
          StatementBillChargeDetailInfoQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<StatementBillChargeDetailVO> resultList =
            mapper.selectOtherBillCollectionDetailList(query);
    generateBillChargeAccountItemDetail(resultList);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出诊所代收(非本期)记录明细
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportOtherBillCollectionDetailList(
          HttpServletResponse response, StatementBillChargeDetailInfoQuery query) throws IOException {
    PageInfo<StatementBillChargeDetailVO> pageInfo = findOtherBillCollectionDetailList(query);
    List<StatementBillChargeDetailVO> list = pageInfo.getList();
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    String fileName = "诊所代收(非本期)记录明细列表";
    if (null != organization) {
      String abbreviation = organization.getAbbreviation();
      fileName = abbreviation + fileName;
    }
    DynamicHeaderPageInfo<JSONObject> page = buildDynamicChargeDetails(list);
    List<JSONObject> result = page.getList();
    ExcelUtil excelUtil = new ExcelUtil(JSONObject.class);
    excelUtil.exportExcel(response, result, "诊所代收(非本期)记录明细", fileName, page.getMap());
  }

  /**
   * 根据条件查询诊所被代收账（本期）明细列表
   *
   * @param query 查询条件
   * @return PageInfo<StatementBillChargeDetailVO>
   */
  public PageInfo<StatementBillChargeDetailVO> findCurrentBillIsAcceptedDetailList(
          StatementBillChargeDetailInfoQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<StatementBillChargeDetailVO> resultList =
            mapper.selectCurrentBillIsAcceptedDetailList(query);
    generateBillChargeAccountItemDetail(resultList);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出诊所被代收帐(本期)记录明细
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportCurrentBillIsAcceptedDetailList(
          HttpServletResponse response, StatementBillChargeDetailInfoQuery query) throws IOException {
    PageInfo<StatementBillChargeDetailVO> pageInfo = findCurrentBillIsAcceptedDetailList(query);
    List<StatementBillChargeDetailVO> list = pageInfo.getList();
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    String fileName = "诊所被代收账(本期)记录明细列表";
    if (null != organization) {
      String abbreviation = organization.getAbbreviation();
      fileName = abbreviation + fileName;
    }
    DynamicHeaderPageInfo<JSONObject> page = buildDynamicChargeDetails(list);
    List<JSONObject> result = page.getList();
    ExcelUtil excelUtil = new ExcelUtil(JSONObject.class);
    excelUtil.exportExcel(response, result, "诊所被代收账(本期)记录明细", fileName, page.getMap());
  }

  /**
   * 根据条件查询诊所被代收账（非本期）明细列表
   *
   * @param query 查询条件
   * @return PageInfo<StatementBillChargeDetailVO>
   */
  public PageInfo<StatementBillChargeDetailVO> findOtherBillIsAcceptedDetailList(
          StatementBillChargeDetailInfoQuery query) {
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<StatementBillChargeDetailVO> resultList =
            mapper.selectOtherBillIsAcceptedDetailList(query);
    generateBillChargeAccountItemDetail(resultList);
    return new PageInfo<>(resultList);
  }

  /**
   * 根据条件导出诊所被代收帐(非本期)记录明细
   *
   * @param response 响应
   * @param query 查询条件
   */
  public void exportOtherBillIsAcceptedDetailList(
          HttpServletResponse response, StatementBillChargeDetailInfoQuery query) throws IOException {
    PageInfo<StatementBillChargeDetailVO> pageInfo = findOtherBillIsAcceptedDetailList(query);
    List<StatementBillChargeDetailVO> list = pageInfo.getList();
    BaseOrganization organization = organizationMapper.selectByPrimaryKey(query.getOrgId());
    String fileName = "诊所被代收账(非本期)记录明细列表";
    if (null != organization) {
      String abbreviation = organization.getAbbreviation();
      fileName = abbreviation + fileName;
    }
    DynamicHeaderPageInfo<JSONObject> page = buildDynamicChargeDetails(list);
    List<JSONObject> result = page.getList();
    ExcelUtil excelUtil = new ExcelUtil(JSONObject.class);
    excelUtil.exportExcel(response, result, "诊所被代收账(非本期)记录明细", fileName, page.getMap());
  }

  /**
   * 构建账单收费记录的支付方式明细信息
   *
   * @param resultList 账单收费记录列表
   */
  private void generateBillChargeAccountItemDetail(List<StatementBillChargeDetailVO> resultList) {
    if (StringHelper.isNotEmpty(resultList)) {
      for (StatementBillChargeDetailVO vo : resultList) {
        List<StatementPaymentVO> statementPaymentResult = new ArrayList<>();
        List<StatementPaymentVO> statementPayments =
                billPayDetailBiz.findBillPayDetailList(vo.getBillPayId());
        setStatementPaymentValue(statementPaymentResult, statementPayments);
        vo.setStatementPayments(statementPaymentResult);
      }
    }
  }

  /**
   * 设置对账单会员卡、预付款本金、赠金
   *
   * @param statementPaymentResult 原对账单列表
   * @param statementPayments 对账单结果
   */
  static void setStatementPaymentValue(
          List<StatementPaymentVO> statementPaymentResult, List<StatementPaymentVO> statementPayments) {
    if (StringHelper.isNotEmpty(statementPayments)) {
      int index = 0;
      for (StatementPaymentVO payment : statementPayments) {
        Integer accountItemId = payment.getAccountItemId();
        // 将支付方式名称为：会员卡或预付款的支付港式拆分为-会员卡本金/赠金；预付款本金/赠金
        PatientDepositAccountTypeEnum typeEnum = PatientDepositAccountTypeEnum.getTypeEnumRelId(accountItemId);
        if (StringHelper.isNotNull(typeEnum)) {
          String name = typeEnum.getName();
          payment.setAccountItemName(name + "本金");
          statementPaymentResult.add(index++, payment);

          StatementPaymentVO bouns = new StatementPaymentVO();
          bouns.setAccountItemId(accountItemId);
          bouns.setAccountItemName(name + "赠金");
          bouns.setTotalAmount(payment.getBonusAmount());
          statementPaymentResult.add(index++, bouns);
        } else {
          statementPaymentResult.add(payment);
        }
      }
    }
  }

  /**
   * 构建对账单账单收费明细导出列表
   *
   * @param resultList 账单收费明细列表
   * @return List<StatementBillChargeDetailExportVO>
   */
  private List<StatementBillChargeDetailExportVO> buildStatementBillChargeDetailExportList(
          List<StatementBillChargeDetailVO> resultList) {
    ArrayList<StatementBillChargeDetailExportVO> chargeDetailExports = new ArrayList<>();
    if (StringHelper.isNotEmpty(resultList)) {
      for (StatementBillChargeDetailVO vo : resultList) {
        StatementBillChargeDetailExportVO chargeDetailExport =
                new StatementBillChargeDetailExportVO();
        BeanUtils.copyProperties(vo, chargeDetailExport);
        List<StatementPaymentVO> payments = vo.getStatementPayments();
        if (StringHelper.isNotEmpty(payments)) {
          for (StatementPaymentVO payment : payments) {
            // todo 当前硬编码全部支付方式，后期再考虑根据现有支付方式动态设置
            String accountItemName = payment.getAccountItemName();
            BigDecimal totalAmount = payment.getTotalAmount();
            switch (accountItemName) {
              case "会员卡本金":
                chargeDetailExport.setMemberPrincipleAmount(totalAmount);
                break;
              case "会员卡赠金":
                chargeDetailExport.setMemberBonusAmount(totalAmount);
                break;
              case "预付款本金":
                chargeDetailExport.setPrepaidPrincipleAmount(totalAmount);
                break;
              case "预付款赠金":
                chargeDetailExport.setPrepaidBonusAmount(totalAmount);
                break;
              case "现金":
                chargeDetailExport.setCashAmount(totalAmount);
                break;
              case "支付宝":
                chargeDetailExport.setAliPayAmount(totalAmount);
                break;
              case "微信":
                chargeDetailExport.setWeChatAmount(totalAmount);
                break;
              case "银行账户":
                chargeDetailExport.setBankAmount(totalAmount);
                break;
              case "浙江省医保":
                chargeDetailExport.setZheJiangProvinceMedicalInsuranceAmount(totalAmount);
                break;
              case "杭州市医保":
                chargeDetailExport.setHangZhouCityMedicalInsuranceAmount(totalAmount);
                break;
              case "杭州市余杭医保":
                chargeDetailExport.setHangZhouYuHangMedicalInsuranceAmount(totalAmount);
                break;
              case "杭州市萧山医保":
                chargeDetailExport.setHangZhouXiaoShanMedicalInsuranceAmount(totalAmount);
                break;
              case "招商信诺":
                chargeDetailExport.setZhaoShangXinNuoAmount(totalAmount);
                break;
              case "Cigna":
                chargeDetailExport.setCignaAmount(totalAmount);
                break;
              case "MSH":
                chargeDetailExport.setMshAmount(totalAmount);
                break;
              case "AXA":
                chargeDetailExport.setAxaAmount(totalAmount);
                break;
              case "中意保险":
                chargeDetailExport.setFengShiAmount(totalAmount);
                break;
              case "本次免单支付":
                chargeDetailExport.setThisWaiverAmount(totalAmount);
                break;
              case "艾维员工免单":
                chargeDetailExport.setEmployeeWaiverAmount(totalAmount);
              case "2020年会员充值送":
                chargeDetailExport.setMemberRechargeAmount(totalAmount);
                break;
              default:
                break;
            }
          }
        }
        chargeDetailExports.add(chargeDetailExport);
      }
    }
    return chargeDetailExports;
  }

  /**
   * 根据条件查询折扣&免单列表
   *
   * @param query 查询条件
   * @return PageInfo<BillDiscountAndFreePaymentVO>
   */
  public PageInfo<BillDiscountAndFreePaymentVO> billDiscountAndFreePaymentList(
          BillDiscountAndFreePaymentQuery query) {
    if (query.getEndDate() == null) {
      query.setEndDate(query.getStartDate());
    }
    if (query.getWhetherPage()) {
      PageHelper.startPage(query.getPageNum(), query.getPageSize());
    }
    List<BillDiscountAndFreePaymentVO> list =
            billPayDetailBiz.billDiscountAndFreePaymentList(query);
    return new PageInfo<>(list);
  }

  /**
   * 根据条件查询折扣&免单列表
   *
   * @param query 查询条件
   * @param response
   */
  public void billDiscountAndFreePaymentExport(
          BillDiscountAndFreePaymentQuery query, HttpServletResponse response) throws IOException {
    query.setWhetherPage(false);
    PageInfo<BillDiscountAndFreePaymentVO> pageInfo = billDiscountAndFreePaymentList(query);
    List<BillDiscountAndFreePaymentVO> resultList = pageInfo.getList();
    ExcelUtil<BillDiscountAndFreePaymentVO> excelUtil =
            new ExcelUtil<>(BillDiscountAndFreePaymentVO.class);
    String fileName =
            excelUtil.getFileName(query.getStartDate(), query.getEndDate(), "", "折扣&免单报表");
    excelUtil.exportExcel(response, resultList, "折扣&免单报表", fileName);
  }

  public List<BillDiscountAndFreePaymentItemVO> billDiscountAndFreePaymentItems() {
    List<BillDiscountAndFreePaymentItemVO> result = new ArrayList<>();
    BillDiscountAndFreePaymentItemVO item = new BillDiscountAndFreePaymentItemVO();
    item.setAccountType(0);
    item.setName("全部");
    result.add(item);
    item = new BillDiscountAndFreePaymentItemVO();
    item.setAccountType(23);
    item.setName("本次免单支付");
    result.add(item);
    item = new BillDiscountAndFreePaymentItemVO();
    item.setAccountType(26);
    item.setName("艾维员工免单");
    result.add(item);
    item = new BillDiscountAndFreePaymentItemVO();
    item.setAccountType(999);
    item.setName("授权折扣");
    result.add(item);
    return result;
  }

  public List<PatientAmountVO> findPatientTotalPayAmount(PatientDimensionQueryForm query, List<Integer> patientIds) {
    return mapper.selectPatientTotalPayAmount(query, patientIds);
  }
}
